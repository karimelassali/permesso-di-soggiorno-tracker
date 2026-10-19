package com.example.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

class AppRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val credentialsDao = database.userCredentialsDao()
    private val historyDao = database.statusHistoryDao()
    private val bookmarkDao = database.guideBookmarkDao()
    private val checklistDao = database.documentChecklistDao()
    private val sofiaDao = database.sofiaChatDao()

    val credentialsFlow: Flow<UserCredentials?> = credentialsDao.getCredentialsFlow()
    val historyFlow: Flow<List<StatusHistory>> = historyDao.getAllHistoryFlow()
    val bookmarksFlow: Flow<List<GuideBookmark>> = bookmarkDao.getAllBookmarksFlow()
    val allConversationsFlow: Flow<List<SofiaConversation>> = sofiaDao.getAllConversationsFlow()

    fun getMessagesForConversationFlow(conversationId: Long): Flow<List<SofiaMessage>> {
        return sofiaDao.getMessagesForConversationFlow(conversationId)
    }

    suspend fun createConversation(title: String, category: String? = null): Long {
        return sofiaDao.insertConversation(SofiaConversation(title = title, category = category))
    }

    suspend fun updateConversation(conversation: SofiaConversation) {
        sofiaDao.updateConversation(conversation)
    }

    suspend fun deleteConversation(id: Long) {
        sofiaDao.deleteConversationById(id)
    }

    suspend fun insertSofiaMessage(message: SofiaMessage): Long {
        return sofiaDao.insertMessage(message)
    }

    suspend fun updateSofiaMessage(message: SofiaMessage) {
        sofiaDao.updateMessage(message)
    }

    suspend fun clearSofiaMessages(conversationId: Long) {
        sofiaDao.deleteMessagesForConversation(conversationId)
    }

    suspend fun searchConversations(query: String): List<SofiaConversation> {
        return sofiaDao.searchConversations("%$query%")
    }

    fun getChecklistStatesFlow(guideId: String): Flow<List<DocumentChecklistState>> {
        return checklistDao.getChecklistStatesFlow(guideId)
    }

    suspend fun toggleBookmark(guideId: String, isBookmarked: Boolean) {
        if (isBookmarked) {
            bookmarkDao.insert(GuideBookmark(guideId))
        } else {
            bookmarkDao.delete(guideId)
        }
    }

    suspend fun saveChecklistState(guideId: String, documentId: String, isChecked: Boolean) {
        checklistDao.insertOrUpdate(DocumentChecklistState(guideId, documentId, isChecked))
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    private fun buildSessionClient(): OkHttpClient {
        val cookieStore = mutableMapOf<String, MutableList<Cookie>>()
        return client.newBuilder()
            .cookieJar(object : CookieJar {
                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    val hostCookies = cookieStore.getOrPut(url.host) { mutableListOf() }
                    cookies.forEach { newCookie ->
                        hostCookies.removeAll { it.name == newCookie.name }
                        hostCookies.add(newCookie)
                    }
                }

                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    return cookieStore[url.host] ?: emptyList()
                }
            })
            .build()
    }

    suspend fun getCredentialsDirect(): UserCredentials? {
        return credentialsDao.getCredentialsDirect()
    }

    suspend fun saveCredentials(credentials: UserCredentials) {
        credentialsDao.insertOrUpdate(credentials)
    }

    suspend fun clearHistory() {
        historyDao.clearAll()
    }

    suspend fun clearAllSofiaData() {
        sofiaDao.deleteAllConversations()
        sofiaDao.deleteAllMessages()
    }

    suspend fun insertHistory(history: StatusHistory) {
        historyDao.insert(history)
    }

    // Runs a status check for Polizia di Stato
    suspend fun checkPoliziaStatus(rawNumero: String): CheckResult = withContext(Dispatchers.IO) {
        val numero = rawNumero.replace("-", "").replace(" ", "").trim()
        if (numero.isBlank()) {
            return@withContext CheckResult(false, "Numero di assicurata o pratica non inserito", "")
        }

        val url = "https://questure.poliziadistato.it/stranieri/?mime=1&lang=it&pratica=$numero"
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val errorMsg = "HTTP Error: ${response.code}"
                val history = StatusHistory(
                    portalType = "POLIZIA",
                    statusText = "Errore di connessione",
                    isSuccess = false,
                    details = errorMsg
                )
                historyDao.insert(history)
                return@withContext CheckResult(false, "Errore del portale (HTTP ${response.code})", "")
            }

            val html = response.body?.string() ?: ""
            val parsed = parsePoliziaHtml(html)

            // Update user credentials cache
            val currentCreds = credentialsDao.getCredentialsDirect() ?: UserCredentials()
            val updatedCreds = currentCreds.copy(
                lastCheckedTime = System.currentTimeMillis(),
                lastPoliziaStatus = parsed.statusText
            )
            credentialsDao.insertOrUpdate(updatedCreds)

            val isFound = parsed.statusCode == StatusCode.READY || parsed.statusCode == StatusCode.PROCESSING

            // Add to history
            val history = StatusHistory(
                portalType = "POLIZIA",
                statusText = parsed.statusText,
                isSuccess = isFound,
                details = parsed.details
            )
            historyDao.insert(history)

            CheckResult(isFound, parsed.statusText, parsed.details, parsed.statusCode)
        } catch (e: IOException) {
            val errorMsg = "Network Error: ${e.message}"
            val history = StatusHistory(
                portalType = "POLIZIA",
                statusText = "Errore di rete",
                isSuccess = false,
                details = errorMsg
            )
            historyDao.insert(history)
            CheckResult(false, "Errore di rete. Controlla la tua connessione.", "")
        }
    }

    // Runs a real authenticated status check on Portale Immigrazione (ELI2ImmigrazioneWeb)
    suspend fun checkPortaleStatus(rawUserId: String, rawPass: String): CheckResult = withContext(Dispatchers.IO) {
        val userId = rawUserId.replace("-", "").replace(" ", "").trim()
        val pass = rawPass.replace("-", "").replace(" ", "").trim()

        if (userId.isBlank() || pass.isBlank()) {
            return@withContext CheckResult(false, "Credenziali non inserite", "")
        }

        val sessionClient = buildSessionClient()
        val startUrl = "https://www.portaleimmigrazione.it/eli2immigrazioneweb/pagine/startpage.aspx"
        val istanzaUrl = "https://www.portaleimmigrazione.it/eli2immigrazioneweb/Pagine/VisualizzaIstanza.aspx"

        try {
            // Step 1: Load startpage.aspx to obtain initial ViewState and session cookies
            val initRequest = Request.Builder()
                .url(startUrl)
                .header("User-Agent", USER_AGENT)
                .build()

            var viewState: String
            var viewStateGen: String
            var eventValidation: String

            sessionClient.newCall(initRequest).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext CheckResult(false, "Errore del portale (HTTP ${response.code})", "")
                }
                val html = response.body?.string() ?: ""
                viewState = extractHiddenField(html, "__VIEWSTATE")
                viewStateGen = extractHiddenField(html, "__VIEWSTATEGENERATOR")
                eventValidation = extractHiddenField(html, "__EVENTVALIDATION")
            }

            // Step 2: Accept cookie banner via ASP.NET __doPostBack('lnkAcceptCookie', '') so login button is enabled
            if (eventValidation.isNotBlank()) {
                val cookieForm = FormBody.Builder()
                    .add("__EVENTTARGET", "lnkAcceptCookie")
                    .add("__EVENTARGUMENT", "")
                    .add("__VIEWSTATE", viewState)
                    .add("__VIEWSTATEGENERATOR", viewStateGen)
                    .add("__EVENTVALIDATION", eventValidation)
                    .add("UC_LogIn1:txtUtente", "")
                    .add("UC_LogIn1:txtPassword", "")
                    .build()

                val cookieRequest = Request.Builder()
                    .url(startUrl)
                    .header("User-Agent", USER_AGENT)
                    .header("Referer", startUrl)
                    .post(cookieForm)
                    .build()

                sessionClient.newCall(cookieRequest).execute().use { response ->
                    val html = response.body?.string() ?: ""
                    val newVs = extractHiddenField(html, "__VIEWSTATE")
                    val newVsg = extractHiddenField(html, "__VIEWSTATEGENERATOR")
                    val newEv = extractHiddenField(html, "__EVENTVALIDATION")
                    if (newVs.isNotBlank()) viewState = newVs
                    if (newVsg.isNotBlank()) viewStateGen = newVsg
                    if (newEv.isNotBlank()) eventValidation = newEv
                }
            }

            // Step 3: Post credentials to startpage.aspx
            val loginForm = FormBody.Builder()
                .add("__EVENTTARGET", "")
                .add("__EVENTARGUMENT", "")
                .add("__VIEWSTATE", viewState)
                .add("__VIEWSTATEGENERATOR", viewStateGen)
                .add("__EVENTVALIDATION", eventValidation)
                .add("UC_LogIn1:txtUtente", userId)
                .add("UC_LogIn1:txtPassword", pass)
                .add("UC_LogIn1:btnConferma", "Conferma [C]")
                .build()

            val loginRequest = Request.Builder()
                .url(startUrl)
                .header("User-Agent", USER_AGENT)
                .header("Referer", startUrl)
                .post(loginForm)
                .build()

            var loginHtml = ""
            var finalLoginUrl = ""
            sessionClient.newCall(loginRequest).execute().use { response ->
                loginHtml = response.body?.string() ?: ""
                finalLoginUrl = response.request.url.toString()
            }

            val isLoggedIn = finalLoginUrl.contains("HomePage.aspx", ignoreCase = true) ||
                loginHtml.contains("VisualizzaIstanza.aspx", ignoreCase = true)

            if (!isLoggedIn) {
                val msgStatus = extractElementTextById(loginHtml, "UC_LogIn1_MsgStatus")
                val errDetails = if (msgStatus.isNotBlank()) {
                    msgStatus
                } else {
                    "User ID ($userId) o Password non riconosciuti dal Portale Immigrazione."
                }
                val history = StatusHistory(
                    portalType = "PORTALE",
                    statusText = "Credenziali non valide",
                    isSuccess = false,
                    details = errDetails
                )
                historyDao.insert(history)
                return@withContext CheckResult(
                    isSuccess = false,
                    statusText = "Credenziali non valide",
                    details = errDetails,
                    statusCode = StatusCode.NOT_FOUND
                )
            }

            // Step 4: Fetch VisualizzaIstanza.aspx inside the authenticated session
            val istanzaRequest = Request.Builder()
                .url(istanzaUrl)
                .header("User-Agent", USER_AGENT)
                .header("Referer", finalLoginUrl.ifBlank { "https://www.portaleimmigrazione.it/ELI2ImmigrazioneWEB/Pagine/HomePage.aspx" })
                .build()

            val istanzaHtml = sessionClient.newCall(istanzaRequest).execute().use { response ->
                response.body?.string() ?: ""
            }

            val parsed = parsePortaleIstanzaHtml(istanzaHtml, userId, pass)

            // Update user credentials cache with all real fields from Dettaglio Istanza
            val currentCreds = credentialsDao.getCredentialsDirect() ?: UserCredentials()
            val updatedCreds = currentCreds.copy(
                userId = userId,
                password = pass,
                numeroAssicurata = parsed.codiceAssicurata.ifBlank { currentCreds.numeroAssicurata.ifBlank { pass } },
                numeroPratica = parsed.codiceIstanza.ifBlank { currentCreds.numeroPratica },
                lastCheckedTime = System.currentTimeMillis(),
                lastPortaleStatus = parsed.statusText,
                codiceIstanza = parsed.codiceIstanza,
                codiceOlogramma = parsed.codiceOlogramma.ifBlank { userId },
                dettaglioAnomalie = parsed.dettaglioAnomalie,
                convocazioneDate = parsed.convocazioneDate,
                convocazioneLocation = parsed.convocazioneLocation,
                convocazioneMotivo = parsed.convocazioneMotivo
            )
            credentialsDao.insertOrUpdate(updatedCreds)

            val isFound = parsed.statusCode == StatusCode.READY || parsed.statusCode == StatusCode.PROCESSING

            val history = StatusHistory(
                portalType = "PORTALE",
                statusText = parsed.statusText,
                isSuccess = isFound,
                details = parsed.details
            )
            historyDao.insert(history)

            CheckResult(
                isSuccess = isFound,
                statusText = parsed.statusText,
                details = parsed.details,
                statusCode = parsed.statusCode,
                codiceAssicurata = parsed.codiceAssicurata,
                codiceIstanza = parsed.codiceIstanza,
                codiceOlogramma = parsed.codiceOlogramma,
                dettaglioAnomalie = parsed.dettaglioAnomalie,
                convocazioneDate = parsed.convocazioneDate,
                convocazioneLocation = parsed.convocazioneLocation,
                convocazioneMotivo = parsed.convocazioneMotivo
            )
        } catch (e: IOException) {
            val errorMsg = "Network Error: ${e.message}"
            val history = StatusHistory(
                portalType = "PORTALE",
                statusText = "Errore di rete",
                isSuccess = false,
                details = errorMsg
            )
            historyDao.insert(history)
            CheckResult(false, "Errore di rete. Controlla la tua connessione.", "")
        }
    }

    private fun extractHiddenField(html: String, fieldName: String): String {
        val regex = Regex("""name="${Regex.escape(fieldName)}"[^>]*value="([^"]*)"""", RegexOption.IGNORE_CASE)
        return regex.find(html)?.groupValues?.get(1) ?: ""
    }

    private fun extractElementTextById(html: String, elementId: String): String {
        val regex = Regex("""id="${Regex.escape(elementId)}"[^>]*>(.*?)</(?:span|th|td|div)>""", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
        val raw = regex.find(html)?.groupValues?.get(1) ?: return ""
        return unescapeHtml(raw)
    }

    private fun unescapeHtml(html: String): String {
        return html
            .replace("&egrave;", "è")
            .replace("&eacute;", "é")
            .replace("&agrave;", "à")
            .replace("&ograve;", "ò")
            .replace("&ugrave;", "ù")
            .replace("&igrave;", "ì")
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&apos;", "'")
            .replace("<[^>]*>".toRegex(), " ")
            .replace("\\s+".toRegex(), " ")
            .trim()
    }

    private fun parsePortaleIstanzaHtml(
        html: String,
        fallbackOlogramma: String,
        fallbackAssicurata: String
    ): ParsedStatus {
        val assicurata = extractElementTextById(html, "txtTRACCIATURA_ASSICURATA").ifBlank { fallbackAssicurata }
        val codiceIstanza = extractElementTextById(html, "txtTRACCIATURA_CODICEISTANZA")
        val ologramma = extractElementTextById(html, "txtTRACCIATURA_OLOGRAMMA").ifBlank { fallbackOlogramma }
        val statoAttuale = extractElementTextById(html, "txtTRACCIATURA_STATOATTUALE")

        val anom1 = extractElementTextById(html, "txtDETTAGLIOANOMALIA1")
        val anom2 = extractElementTextById(html, "txtDETTAGLIOANOMALIA2")
        val anom3 = extractElementTextById(html, "txtDETTAGLIOANOMALIA3")

        // Extract Dettaglio Anomalie Documentali from trAnomalie_3
        val docAnomRegex = Regex("""id="trAnomalie_3"[^>]*>(.*?)</tr>""", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
        val docAnomText = docAnomRegex.find(html)?.groupValues?.get(1)?.let { unescapeHtml(it) } ?: ""

        val anomaliesList = listOf(anom1, anom2, anom3, docAnomText).filter { it.isNotBlank() }
        val dettaglioAnomalie = if (anomaliesList.isNotEmpty()) {
            anomaliesList.distinct().joinToString(" • ")
        } else {
            "Nessuna Anomalia Documentale"
        }

        // Extract Convocazione from trConvocazione_2 -> <li>...</li>
        val convRegex = Regex("""id="trConvocazione_2"[^>]*>.*?<li[^>]*>(.*?)</li>""", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
        val rawConv = convRegex.find(html)?.groupValues?.get(1) ?: ""
        val convParts = rawConv
            .split(Regex("""<br\s*/?>""", RegexOption.IGNORE_CASE))
            .map { unescapeHtml(it) }
            .filter { it.isNotBlank() }

        val convocazioneDate = convParts.getOrNull(0) ?: ""
        val convocazioneLocation = convParts.getOrNull(1) ?: ""
        val convocazioneMotivo = convParts.getOrNull(2)
            ?.replace(Regex("^Motivo\\s*:\\s*", RegexOption.IGNORE_CASE), "")
            ?.trim() ?: ""

        if (statoAttuale.isBlank() && codiceIstanza.isBlank()) {
            val msgStatus = extractElementTextById(html, "MsgStatus")
            return ParsedStatus(
                statusText = "Nessuna istanza trovata",
                details = msgStatus.ifBlank { "Nessun dettaglio istanza presente per questo account." },
                statusCode = StatusCode.NOT_FOUND
            )
        }

        val statusSummary = if (statoAttuale.isNotBlank()) {
            "$statoAttuale - $dettaglioAnomalie"
        } else {
            dettaglioAnomalie
        }

        val detailsBuilder = StringBuilder()
        if (statoAttuale.isNotBlank()) {
            detailsBuilder.append("Stato Attuale: $statoAttuale\n")
        }
        if (codiceIstanza.isNotBlank()) {
            detailsBuilder.append("Codice Istanza: $codiceIstanza • Assicurata: $assicurata\n")
        }
        detailsBuilder.append("Anomalie: $dettaglioAnomalie")
        if (convocazioneDate.isNotBlank()) {
            detailsBuilder.append("\nConvocazione: $convocazioneDate")
            if (convocazioneLocation.isNotBlank()) {
                detailsBuilder.append(" presso $convocazioneLocation")
            }
            if (convocazioneMotivo.isNotBlank()) {
                detailsBuilder.append(" ($convocazioneMotivo)")
            }
        }

        val code = if (statoAttuale.contains("consegna", ignoreCase = true) || statoAttuale.contains("pronto", ignoreCase = true)) {
            StatusCode.READY
        } else {
            StatusCode.PROCESSING
        }

        return ParsedStatus(
            statusText = statusSummary,
            details = detailsBuilder.toString().trim(),
            statusCode = code,
            codiceAssicurata = assicurata,
            codiceIstanza = codiceIstanza,
            codiceOlogramma = ologramma,
            dettaglioAnomalie = dettaglioAnomalie,
            convocazioneDate = convocazioneDate,
            convocazioneLocation = convocazioneLocation,
            convocazioneMotivo = convocazioneMotivo
        )
    }

    private fun parsePoliziaHtml(html: String): ParsedStatus {
        // Isolate the actual result box around the last occurrence of icona-stranieri
        val iconIdx = html.lastIndexOf("icona-stranieri")
        val targetSlice = if (iconIdx != -1) {
            html.substring((iconIdx - 50).coerceAtLeast(0), (iconIdx + 600).coerceAtMost(html.length))
        } else {
            html
        }
        val text = unescapeHtml(targetSlice)

        return when {
            text.contains("non validi", ignoreCase = true) ||
            text.contains("non valido", ignoreCase = true) ||
            text.contains("caratteri non validi", ignoreCase = true) -> {
                ParsedStatus(
                    "Numero non valido",
                    "Numero di caratteri non validi. Il numero di assicurata deve essere di 12 cifre, oppure il numero di pratica di 10 caratteri.",
                    StatusCode.NOT_FOUND
                )
            }
            text.contains("non è presente in archivio", ignoreCase = true) ||
            text.contains("non presente in archivio", ignoreCase = true) ||
            text.contains("non presente", ignoreCase = true) ||
            text.contains("nessun risultato", ignoreCase = true) ||
            text.contains("non trovato", ignoreCase = true) -> {
                ParsedStatus(
                    "Non presente in archivio",
                    "Il documento di soggiorno non è presente in archivio.",
                    StatusCode.NOT_FOUND
                )
            }
            text.contains("è pronto per la consegna", ignoreCase = true) ||
            text.contains("pronto per la consegna", ignoreCase = true) -> {
                var details = "Il tuo permesso di soggiorno è pronto per la consegna!"
                val pickIndex = text.indexOf("ritirare presso", ignoreCase = true)
                if (pickIndex != -1) {
                    val rawDetails = text.substring(pickIndex)
                    details = rawDetails.split(".")[0] + "."
                }
                ParsedStatus("Pronto per la consegna", details, StatusCode.READY)
            }
            text.contains("non è pronto", ignoreCase = true) ||
            text.contains("in trattazione", ignoreCase = true) ||
            text.contains("in corso di trattazione", ignoreCase = true) ||
            text.contains("fase di trattazione", ignoreCase = true) -> {
                ParsedStatus(
                    "In trattazione",
                    "Il documento di soggiorno è in trattazione presso la Questura.",
                    StatusCode.PROCESSING
                )
            }
            else -> {
                ParsedStatus(
                    "Non presente in archivio",
                    "Il documento di soggiorno non è presente in archivio.",
                    StatusCode.NOT_FOUND
                )
            }
        }
    }

    data class CheckResult(
        val isSuccess: Boolean,
        val statusText: String,
        val details: String,
        val statusCode: StatusCode = StatusCode.UNKNOWN,
        val codiceAssicurata: String = "",
        val codiceIstanza: String = "",
        val codiceOlogramma: String = "",
        val dettaglioAnomalie: String = "",
        val convocazioneDate: String = "",
        val convocazioneLocation: String = "",
        val convocazioneMotivo: String = ""
    )

    private data class ParsedStatus(
        val statusText: String,
        val details: String,
        val statusCode: StatusCode,
        val codiceAssicurata: String = "",
        val codiceIstanza: String = "",
        val codiceOlogramma: String = "",
        val dettaglioAnomalie: String = "",
        val convocazioneDate: String = "",
        val convocazioneLocation: String = "",
        val convocazioneMotivo: String = ""
    )

    enum class StatusCode {
        READY, PROCESSING, NOT_FOUND, UNKNOWN
    }

    companion object {
        private const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    }
}
