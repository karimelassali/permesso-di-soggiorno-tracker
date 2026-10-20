package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiMessage
import com.example.ai.AiProvider
import com.example.ai.GeminiAiProvider
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class SofiaUiState {
    object Landing : SofiaUiState()
    data class ActiveChat(val conversationId: Long) : SofiaUiState()
}

sealed class SofiaActionEvent {
    object OpenMyGuide : SofiaActionEvent()
    object ShowMyQuestura : SofiaActionEvent()
    object OpenOfficialWebsite : SofiaActionEvent()
    object ShowAppointment : SofiaActionEvent()
    object OpenTimeline : SofiaActionEvent()
    object ExplainStatus : SofiaActionEvent()
    object ShowRequiredDocuments : SofiaActionEvent()
    object OpenRenewalGuide : SofiaActionEvent()
    object GoToProfile : SofiaActionEvent()
    object OpenBookmarks : SofiaActionEvent()
    object OpenImmigrationGuides : SofiaActionEvent()
}

class SofiaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)
    private val aiProvider: AiProvider = GeminiAiProvider()

    // UI state navigation (Landing page or specific conversation)
    private val _uiState = MutableStateFlow<SofiaUiState>(SofiaUiState.Landing)
    val uiState: StateFlow<SofiaUiState> = _uiState.asStateFlow()

    // Shared flow to send navigation actions to the UI
    private val _actionEvent = MutableSharedFlow<SofiaActionEvent>()
    val actionEvent = _actionEvent.asSharedFlow()

    // Conversations lists
    val conversations: StateFlow<List<SofiaConversation>> = repository.allConversationsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search query for conversations list
    val conversationSearchQuery = MutableStateFlow("")

    private val _searchedConversations = MutableStateFlow<List<SofiaConversation>>(emptyList())
    val searchedConversations: StateFlow<List<SofiaConversation>> = combine(
        conversations,
        conversationSearchQuery
    ) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            list.filter { it.title.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active messages flow
    private val _activeMessages = MutableStateFlow<List<SofiaMessage>>(emptyList())
    val activeMessages: StateFlow<List<SofiaMessage>> = _activeMessages.asStateFlow()

    // Current streaming typing text to simulate smooth response streaming
    private val _typingResponseText = MutableStateFlow("")
    val typingResponseText: StateFlow<String> = _typingResponseText.asStateFlow()

    val isAiLoading = MutableStateFlow(false)

    // Gather overall app context dynamically for the AI prompt
    private val credentials = repository.credentialsFlow.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val userCredentialsState: StateFlow<UserCredentials?> = credentials
    private val checkHistory = repository.historyFlow.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    private val bookmarkedGuideIds = repository.bookmarksFlow
        .map { list -> list.map { it.guideId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    // User extra preference flows (from shared preferences)
    private val prefs = application.getSharedPreferences("permesso_extra_prefs", android.content.Context.MODE_PRIVATE)
    private val selectedProvince = prefs.getString("selected_province", "Milano") ?: "Milano"
    private val appointmentDate = prefs.getString("appointment_date", "") ?: ""
    private val appointmentTime = prefs.getString("appointment_time", "") ?: ""
    private val appointmentLocation = prefs.getString("appointment_location", "") ?: ""
    private val checkedDocs = prefs.getStringSet("checked_docs", emptySet()) ?: emptySet()

    init {
        // Observe changes to active conversation and reload messages
        viewModelScope.launch {
            _uiState.collect { state ->
                if (state is SofiaUiState.ActiveChat) {
                    repository.getMessagesForConversationFlow(state.conversationId).collect { msgs ->
                        _activeMessages.value = msgs
                    }
                } else {
                    _activeMessages.value = emptyList()
                }
            }
        }
    }

    fun selectConversation(id: Long) {
        viewModelScope.launch {
            _uiState.value = SofiaUiState.ActiveChat(id)
            // Update last active
            val conv = repository.allConversationsFlow.firstOrNull()?.find { it.id == id }
            if (conv != null) {
                repository.updateConversation(conv.copy(lastActive = System.currentTimeMillis()))
            }
        }
    }

    fun startNewConversation(title: String, initialMessage: String? = null, category: String? = null, selectedLanguageCode: String) {
        viewModelScope.launch {
            val convId = repository.createConversation(title = title, category = category)
            _uiState.value = SofiaUiState.ActiveChat(convId)
            
            if (!initialMessage.isNullOrBlank()) {
                sendMessage(initialMessage, selectedLanguageCode)
            }
        }
    }

    fun goBackToLanding() {
        _uiState.value = SofiaUiState.Landing
    }

    fun deleteConversation(id: Long) {
        viewModelScope.launch {
            repository.deleteConversation(id)
            if (_uiState.value is SofiaUiState.ActiveChat && 
                (_uiState.value as SofiaUiState.ActiveChat).conversationId == id) {
                _uiState.value = SofiaUiState.Landing
            }
        }
    }

    fun togglePinConversation(id: Long) {
        viewModelScope.launch {
            val list = conversations.value
            val conv = list.find { it.id == id }
            if (conv != null) {
                repository.updateConversation(conv.copy(isPinned = !conv.isPinned))
            }
        }
    }

    fun renameConversation(id: Long, newTitle: String) {
        viewModelScope.launch {
            val list = conversations.value
            val conv = list.find { it.id == id }
            if (conv != null) {
                repository.updateConversation(conv.copy(title = newTitle))
            }
        }
    }

    fun likeMessage(messageId: Long, liked: Boolean) {
        viewModelScope.launch {
            val msg = _activeMessages.value.find { it.id == messageId }
            if (msg != null) {
                repository.updateSofiaMessage(msg.copy(liked = liked))
            }
        }
    }

    fun triggerSmartAction(action: SofiaActionEvent) {
        viewModelScope.launch {
            _actionEvent.emit(action)
        }
    }

    fun sendMessage(text: String, selectedLanguageCode: String) {
        val currentState = _uiState.value
        if (currentState !is SofiaUiState.ActiveChat) return
        val convId = currentState.conversationId

        if (text.trim().isEmpty()) return

        viewModelScope.launch {
            // Get history BEFORE inserting the new message to avoid role inconsistency in AI
            val historyBefore = _activeMessages.value.map {
                AiMessage(sender = it.sender, text = it.text)
            }

            // Save user message to DB
            repository.insertSofiaMessage(
                SofiaMessage(
                    conversationId = convId,
                    sender = "user",
                    text = text,
                    sourceType = "official" 
                )
            )

            // Update conversation last active timestamp
            val currentConv = conversations.value.find { it.id == convId }
            if (currentConv != null) {
                repository.updateConversation(currentConv.copy(lastActive = System.currentTimeMillis()))
            }

            // Perform AI response generation with explicit history
            generateAiResponse(convId, text, selectedLanguageCode, historyBefore)
        }
    }

    fun regenerateLastResponse(selectedLanguageCode: String) {
        val currentState = _uiState.value
        if (currentState !is SofiaUiState.ActiveChat) return
        val convId = currentState.conversationId

        val messagesList = _activeMessages.value
        if (messagesList.isEmpty()) return

        // Find the last user message
        val lastUserMessage = messagesList.findLast { it.sender == "user" } ?: return
        
        // Build history up to that user message (exclusive)
        val userIndex = messagesList.indexOf(lastUserMessage)
        val historyUpToUser = if (userIndex > 0) {
            messagesList.subList(0, userIndex).map { AiMessage(sender = it.sender, text = it.text) }
        } else {
            emptyList()
        }

        viewModelScope.launch {
            generateAiResponse(convId, lastUserMessage.text, selectedLanguageCode, historyUpToUser)
        }
    }

    private suspend fun generateAiResponse(convId: Long, userQuery: String, selectedLanguageCode: String, history: List<AiMessage>) {
        isAiLoading.value = true
        _typingResponseText.value = ""

        try {
            // Build the dynamic, context-aware system instructions
            val systemInstructions = buildContextSystemInstructions(selectedLanguageCode)

            // Call abstracted AI provider
            val rawResponse = aiProvider.generateResponse(
                systemInstruction = systemInstructions,
                history = history,
                userMessage = userQuery
            )

            val parsedResponse = if (rawResponse == "ERR_NO_KEY") {
                when (selectedLanguageCode) {
                    "it" -> "Chiave API Gemini mancante. Configurala nella sezione Segreti di AI Studio."
                    "ar" -> "مفتاح API الخاص بـ Gemini غير متوفر. يرجى تكوينه في لوحة الأسرار."
                    "es" -> "Falta la clave de API de Gemini. Configúrala en el panel de secretos."
                    "fr" -> "Clé API Gemini mancante. Veuillez la configurer dans les secrets."
                    else -> "Gemini API key is missing. Please configure it in the Secrets Panel of AI Studio."
                }
            } else {
                rawResponse
            }

            // Typing animation logic: stream characters to typingResponseText in a smooth loop
            withContext(Dispatchers.Main) {
                var currentString = ""
                val chunkSize = 3
                var i = 0
                while (i < parsedResponse.length) {
                    val end = (i + chunkSize).coerceAtMost(parsedResponse.length)
                    currentString += parsedResponse.substring(i, end)
                    _typingResponseText.value = currentString
                    i += chunkSize
                    kotlinx.coroutines.delay(10) // Smooth streaming rate
                }
            }

            // Save the finalized AI message to local DB
            val source = when {
                parsedResponse.contains("Official", ignoreCase = true) || parsedResponse.contains("Portale", ignoreCase = true) || parsedResponse.contains("Polizia", ignoreCase = true) -> "official"
                parsedResponse.contains("Community", ignoreCase = true) || parsedResponse.contains("forum", ignoreCase = true) || parsedResponse.contains("esperienze", ignoreCase = true) -> "community"
                else -> "ai"
            }

            repository.insertSofiaMessage(
                SofiaMessage(
                    conversationId = convId,
                    sender = "sofia",
                    text = parsedResponse,
                    sourceType = source
                )
            )

        } catch (e: Exception) {
            val errorText = "Error: Could not retrieve advice. ${e.message}"
            repository.insertSofiaMessage(
                SofiaMessage(
                    conversationId = convId,
                    sender = "sofia",
                    text = errorText,
                    sourceType = "ai"
                )
            )
        } finally {
            _typingResponseText.value = ""
            isAiLoading.value = false
        }
    }

    private fun buildContextSystemInstructions(lang: String): String {
        val creds = credentials.value
        val historyList = checkHistory.value
        val bookmarks = bookmarkedGuideIds.value
        val isDemo = prefs.getBoolean("institutional_demo_mode", false)

        // Fetch User and Status data dynamically without hardcoded PII
        val userName = creds?.fullName?.takeIf { it.isNotBlank() }
            ?: if (isDemo) PermessoViewModel.DEMO_FULL_NAME else "Utente"
        val liveProvince = prefs.getString("selected_province", "Roma") ?: "Roma"
        val currentProvince = creds?.selectedProvinceCode ?: liveProvince
        val selectedPermit = creds?.selectedPermitType ?: "Rinnovo / Rilascio Permesso di Soggiorno"
        val lastPolizia = creds?.lastPoliziaStatus ?: "No checks yet"
        val lastPortale = creds?.lastPortaleStatus ?: "No checks yet"
        val codiceAssicurata = creds?.numeroAssicurata?.ifBlank { creds.password }
            ?.takeIf { it.isNotBlank() }
            ?: if (isDemo) PermessoViewModel.DEMO_ASSICURATA else "Non inserito"
        val codiceIstanza = creds?.codiceIstanza?.ifBlank { creds.numeroPratica }
            ?.takeIf { it.isNotBlank() }
            ?: if (isDemo) PermessoViewModel.DEMO_ISTANZA else "Non inserito"
        val codiceOlogramma = creds?.codiceOlogramma?.ifBlank { creds.userId }
            ?.takeIf { it.isNotBlank() }
            ?: if (isDemo) PermessoViewModel.DEMO_USER_ID else "Non inserito"
        val anomalie = creds?.dettaglioAnomalie?.ifBlank { "Nessuna Anomalia Documentale" } ?: "Nessuna Anomalia Documentale"

        val historyText = if (historyList.isNotEmpty()) {
            historyList.take(3).joinToString("; ") { "${it.portalType}: ${it.statusText} (${it.details})" }
        } else {
            "No portal checks run yet"
        }

        val liveCheckedDocs = prefs.getStringSet("checked_docs", emptySet()) ?: emptySet()
        val checkedDocsText = if (liveCheckedDocs.isNotEmpty()) {
            "Completed checklist: ${liveCheckedDocs.joinToString(", ")}"
        } else {
            "No checklist documents marked as completed yet"
        }

        val liveAppDate = creds?.convocazioneDate?.ifBlank { prefs.getString("appointment_date", "") ?: "" } ?: ""
        val liveAppTime = prefs.getString("appointment_time", "") ?: ""
        val liveAppLoc = creds?.convocazioneLocation?.ifBlank { prefs.getString("appointment_location", "") ?: "" } ?: ""
        val liveAppMotivo = creds?.convocazioneMotivo ?: ""

        val appointmentText = if (liveAppDate.isNotBlank()) {
            "Convocazione: $liveAppDate ${if (liveAppTime.isNotBlank() && !liveAppDate.contains(liveAppTime)) "at $liveAppTime" else ""}, Location: $liveAppLoc ${if (liveAppMotivo.isNotBlank()) "(Motivo: $liveAppMotivo)" else ""}"
        } else {
            "No questura fingerprint appointment scheduled yet"
        }

        val bookmarksText = if (bookmarks.isNotEmpty()) {
            "User bookmarked guide IDs: ${bookmarks.joinToString(", ")}"
        } else {
            "No guide bookmarks added yet"
        }

        return """
            You are "Sofia AI", an informational civic assistant specialized in Italian administrative procedures for residence permits (Permesso di Soggiorno), built for Soggiorno Track.

            == STRICT LEGAL SAFEGUARD (ART. 348 C.P. - DIVIETO DI CONSULENZA LEGALE PROFESSIONALE) ==
            • You provide general, orientative administrative information based exclusively on public Italian laws and ministerial circulars (e.g., D.Lgs. 286/1998, D.P.R. 394/1999, Legge 241/1990, Circolare Ministero dell'Interno 5 agosto 2006).
            • You are STRICTLY FORBIDDEN from:
              1. Giving definitive legal counsel or binding legal opinions (pareri legali vincolanti);
              2. Predicting outcomes of judicial appeals (ricorsi al TAR o al Tribunale Ordinario);
              3. Presenting yourself as a substitute for a qualified immigration lawyer (avvocato) or accredited Patronato.
            • Always cite general public regulations when explaining rules.
            • For any complex, contested, or critical legal situation — including preavviso di rigetto (Art. 10-bis L. 241/1990), rifiuto/revoca del permesso, decreto di espulsione, contenzioso lavorativo, o ricorsi — you MUST explicitly conclude by recommending that the user consult an accredited Patronato (such as ACLI, INCA, ITAL, INAS) or a qualified immigration lawyer (avvocato esperto in diritto dell'immigrazione).

            == SOFIA PERSONALITY & STYLE ==
            • Tone: Institutional, calm, respectful, clear, and collaborative towards Public Administration (Questure, Prefetture, Poste Italiane).
            • NEVER robotic, never overly casual, limit emojis to 1 maximum per response.
            • Always write in the user's selected language: $lang (supported: 'it', 'en', 'es', 'fr', 'ar'). If 'ar' is used, ensure full RTL compliance and accurate administrative Arabic vocabulary.
            • Distinguish clearly in your text between:
              - [NORMATIVA PUBBLICA] (general public rules from D.Lgs. 286/98, Portale Immigrazione, or Polizia di Stato).
              - [ORIENTAMENTO PRATICO] (organizational checklist or administrative guidance).

            == CURRENT USER CONTEXT (FROM DEVICE PROFILE) ==
            • User Name: $userName
            • Province/City: $currentProvince
            • Selected Permit Type: $selectedPermit
            • Codice Assicurata (Password): $codiceAssicurata
            • Codice Istanza: $codiceIstanza
            • Codice Ologramma (User ID): $codiceOlogramma
            • Portale Immigrazione Status: $lastPortale (Anomalie: $anomalie)
            • Polizia di Stato Status: $lastPolizia
            • Recent Checks History: $historyText
            • Questura Convocazione / Appointment: $appointmentText
            • Documents Checked: $checkedDocsText
            • Bookmarked Guides: $bookmarksText

            == CONTEXT AWARENESS & ORIENTATIVE ANSWERS ==
            - Travel: Explain the general rules of the Ministry of Interior Circular of August 5, 2006 (direct flights without Schengen transit for renewals with original paper Mod. 22A receipt, expired permit, and valid passport), and always remind the user to verify boarding policy directly with their airline and Border Police.
            - Status: Explain what Polizia ("In trattazione", "Pronto per la consegna") or Portale statuses mean in general administrative terms.
            - Documents & Appointments: Remind the user to always bring the original paper receipt (Mod. 22A) with hologram and original passport to the Questura counter.

            == MANDATORY CLOSING REMINDER ==
            Conclude every response with a brief reminder that your answers are purely informational and that for formal assistance on their specific file they should contact an accredited Patronato (ACLI, INCA, ITAL, INAS) or legal professional.
            Keep the response length under 280 words. Focus on pristine markdown.
        """.trimIndent()
    }
}
