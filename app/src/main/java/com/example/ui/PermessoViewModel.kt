package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.background.WorkScheduler
import com.example.data.AppRepository
import com.example.data.UserCredentials
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PermessoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)

    val credentials: StateFlow<UserCredentials?> = repository.credentialsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val history = repository.historyFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val bookmarkedGuideIds: StateFlow<Set<String>> = repository.bookmarksFlow
        .map { list -> list.map { it.guideId }.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    val guideSearchQuery = MutableStateFlow("")
    val activeGuideId = MutableStateFlow<String?>(null)

    // Ask AI state flows
    val aiResponse = MutableStateFlow<String?>(null)
    val aiLoading = MutableStateFlow(false)

    fun askGuideAssistant(prompt: String, guideContentJson: String) {
        aiLoading.value = true
        aiResponse.value = null
        viewModelScope.launch {
            val response = com.example.util.GeminiHelper.askAssistant(prompt, guideContentJson)
            aiResponse.value = response
            aiLoading.value = false
        }
    }

    fun clearAiResponse() {
        aiResponse.value = null
        aiLoading.value = false
    }

    // UI Interactive state for manual check
    private val _checkState = MutableStateFlow<CheckUiState>(CheckUiState.Idle)
    val checkState: StateFlow<CheckUiState> = _checkState.asStateFlow()

    // Form fields editable state
    val formFullName = MutableStateFlow("")
    val formUserId = MutableStateFlow("")
    val formPassword = MutableStateFlow("")
    val formNumeroAssicurata = MutableStateFlow("")
    val formNumeroPratica = MutableStateFlow("")

    // PREMIUM EXTRA FEATURES STATES (Shared Preferences Backed)
    private val prefs = application.getSharedPreferences("permesso_extra_prefs", android.content.Context.MODE_PRIVATE)

    private val _selectedLanguage = MutableStateFlow(prefs.getString("selected_language", "it") ?: "it")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _onboardingCompleted = MutableStateFlow(prefs.getBoolean("onboarding_completed", false))
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    private val _selectedProvince = MutableStateFlow(prefs.getString("selected_province", "Piacenza") ?: "Piacenza")
    val selectedProvince: StateFlow<String> = _selectedProvince.asStateFlow()

    private val _appointmentDate = MutableStateFlow(prefs.getString("appointment_date", "") ?: "")
    val appointmentDate: StateFlow<String> = _appointmentDate.asStateFlow()

    private val _appointmentTime = MutableStateFlow(prefs.getString("appointment_time", "") ?: "")
    val appointmentTime: StateFlow<String> = _appointmentTime.asStateFlow()

    private val _appointmentLocation = MutableStateFlow(prefs.getString("appointment_location", "Questura di Piacenza - V.le Malta, 10/C") ?: "Questura di Piacenza - V.le Malta, 10/C")
    val appointmentLocation: StateFlow<String> = _appointmentLocation.asStateFlow()

    private val _checkedDocs = MutableStateFlow<Set<String>>(
        prefs.getStringSet("checked_docs", emptySet()) ?: emptySet()
    )
    val checkedDocs: StateFlow<Set<String>> = _checkedDocs.asStateFlow()

    init {
        viewModelScope.launch {
            var existing = repository.getCredentialsDirect()
            if (existing == null || (existing.userId.isBlank() && existing.password.isBlank() && existing.numeroAssicurata.isBlank())) {
                // Pre-populate with the user's real Ricevuta Poste Italiane credentials
                val initialCreds = UserCredentials(
                    fullName = "EL ASSALI KARIM",
                    userId = "550000133317",
                    password = "055987476941",
                    numeroAssicurata = "055987476941",
                    numeroPratica = "587979509914",
                    codiceIstanza = "587979509914",
                    codiceOlogramma = "550000133317",
                    selectedQuesturaId = "piacenza",
                    selectedProvinceCode = "PC",
                    selectedPermitType = "lavoro_subordinato"
                )
                repository.saveCredentials(initialCreds)
                existing = initialCreds
                selectProvince("Piacenza")
            }

            if (existing != null) {
                formFullName.value = existing.fullName
                formUserId.value = existing.userId
                formPassword.value = existing.password
                formNumeroAssicurata.value = existing.numeroAssicurata
                formNumeroPratica.value = existing.numeroPratica
                if (existing.lastCheckedTime == 0L) {
                    checkStatusNow()
                }
            }

            // Collect flow updates safely
            credentials.collect { creds ->
                if (creds != null) {
                    formFullName.value = creds.fullName
                    formUserId.value = creds.userId
                    formPassword.value = creds.password
                    formNumeroAssicurata.value = creds.numeroAssicurata
                    formNumeroPratica.value = creds.numeroPratica
                }
            }
        }

        // Start background worker
        WorkScheduler.scheduleDailyCheck(application)
    }

    // EXTRA FEATURE HANDLERS
    fun selectLanguage(langCode: String) {
        _selectedLanguage.value = langCode
        prefs.edit().putString("selected_language", langCode).apply()
    }

    fun completeOnboarding() {
        _onboardingCompleted.value = true
        prefs.edit().putBoolean("onboarding_completed", true).apply()
    }

    fun selectProvince(province: String) {
        _selectedProvince.value = province
        prefs.edit().putString("selected_province", province).apply()
    }

    fun selectQuesturaAndProvince(questuraId: String?, provinceCode: String?, provinceName: String?) {
        viewModelScope.launch {
            val currentCreds = credentials.value ?: UserCredentials()
            val updated = currentCreds.copy(
                selectedQuesturaId = questuraId,
                selectedProvinceCode = provinceCode
            )
            repository.saveCredentials(updated)

            if (provinceName != null) {
                _selectedProvince.value = provinceName
                prefs.edit().putString("selected_province", provinceName).apply()
            }
        }
    }

    fun selectPermitType(permitType: String?) {
        viewModelScope.launch {
            val currentCreds = credentials.value ?: UserCredentials()
            val updated = currentCreds.copy(
                selectedPermitType = permitType
            )
            repository.saveCredentials(updated)
        }
    }

    fun updateAppointment(date: String, time: String, location: String) {
        _appointmentDate.value = date
        _appointmentTime.value = time
        _appointmentLocation.value = location
        prefs.edit()
            .putString("appointment_date", date)
            .putString("appointment_time", time)
            .putString("appointment_location", location)
            .apply()
    }

    fun toggleDocument(docId: String) {
        val current = _checkedDocs.value.toMutableSet()
        if (current.contains(docId)) {
            current.remove(docId)
        } else {
            current.add(docId)
        }
        _checkedDocs.value = current
        prefs.edit().putStringSet("checked_docs", current).apply()
    }

    fun saveCredentials() {
        viewModelScope.launch {
            val cleanUserId = formUserId.value.replace("-", "").replace(" ", "").trim()
            val cleanPassword = formPassword.value.replace("-", "").replace(" ", "").trim()
            var cleanAssicurata = formNumeroAssicurata.value.replace("-", "").replace(" ", "").trim()
            val cleanPratica = formNumeroPratica.value.replace("-", "").replace(" ", "").trim()

            // On Poste Italiane receipts, the password is the 12-digit Codice Assicurata
            if (cleanAssicurata.isBlank() && cleanPassword.length == 12 && cleanPassword.all { it.isDigit() }) {
                cleanAssicurata = cleanPassword
            }

            formUserId.value = cleanUserId
            formPassword.value = cleanPassword
            formNumeroAssicurata.value = cleanAssicurata
            formNumeroPratica.value = cleanPratica

            val currentCreds = repository.getCredentialsDirect() ?: UserCredentials()
            val credsChanged = currentCreds.userId != cleanUserId ||
                currentCreds.password != cleanPassword ||
                currentCreds.numeroAssicurata != cleanAssicurata ||
                currentCreds.numeroPratica != cleanPratica

            val updated = currentCreds.copy(
                fullName = formFullName.value.trim(),
                userId = cleanUserId,
                password = cleanPassword,
                numeroAssicurata = cleanAssicurata,
                numeroPratica = cleanPratica,
                // Reset cached statuses if the user entered a different set of credentials
                lastPoliziaStatus = if (credsChanged) "No checks yet" else currentCreds.lastPoliziaStatus,
                lastPortaleStatus = if (credsChanged) "No checks yet" else currentCreds.lastPortaleStatus,
                codiceIstanza = if (credsChanged) "" else currentCreds.codiceIstanza,
                codiceOlogramma = if (credsChanged) "" else currentCreds.codiceOlogramma,
                dettaglioAnomalie = if (credsChanged) "" else currentCreds.dettaglioAnomalie,
                convocazioneDate = if (credsChanged) "" else currentCreds.convocazioneDate,
                convocazioneLocation = if (credsChanged) "" else currentCreds.convocazioneLocation,
                convocazioneMotivo = if (credsChanged) "" else currentCreds.convocazioneMotivo
            )
            repository.saveCredentials(updated)
            WorkScheduler.scheduleDailyCheck(getApplication())

            // Automatically run a live check against the portals with the newly saved credentials
            checkStatusNow()
        }
    }

    fun checkStatusNow() {
        val cleanUserId = formUserId.value.replace("-", "").replace(" ", "").trim()
        val cleanPassword = formPassword.value.replace("-", "").replace(" ", "").trim()
        val cleanAssicurata = formNumeroAssicurata.value.replace("-", "").replace(" ", "").trim()
        val cleanPratica = formNumeroPratica.value.replace("-", "").replace(" ", "").trim()

        val hasPortale = cleanUserId.isNotBlank() && cleanPassword.isNotBlank()
        val hasPolizia = cleanAssicurata.isNotBlank() || cleanPratica.isNotBlank() || (cleanPassword.length == 12 && cleanPassword.all { it.isDigit() })

        if (!hasPolizia && !hasPortale) {
            _checkState.value = CheckUiState.Error("Si prega di salvare prima un codice di ricevuta valido o le credenziali del portale.")
            return
        }

        _checkState.value = CheckUiState.Loading

        viewModelScope.launch {
            var successCount = 0
            var errorCount = 0
            val statusParts = mutableListOf<String>()
            val detailParts = mutableListOf<String>()
            val errorParts = mutableListOf<String>()

            // 1. Check Portale Immigrazione (authenticated Dettaglio Istanza)
            if (hasPortale) {
                val oldCreds = repository.getCredentialsDirect()
                val result = repository.checkPortaleStatus(cleanUserId, cleanPassword)
                if (result.isSuccess && result.statusCode != AppRepository.StatusCode.NOT_FOUND) {
                    successCount++
                    statusParts.add("Portale: ${result.statusText}")
                    detailParts.add(result.details)

                    // Automatically sync Convocazione appointment details if present in Portale Immigrazione
                    if (result.convocazioneDate.isNotBlank()) {
                        val timeRegex = Regex("""(\d{1,2}:\d{2}(?::\d{2})?)""")
                        val extractedTime = timeRegex.find(result.convocazioneDate)?.groupValues?.get(1) ?: ""
                        val extractedDate = result.convocazioneDate.replace(extractedTime, "").trim()
                        val fullLoc = buildString {
                            append(result.convocazioneLocation)
                            if (result.convocazioneMotivo.isNotBlank()) {
                                append(" (${result.convocazioneMotivo})")
                            }
                        }
                        updateAppointment(
                            date = extractedDate.ifBlank { result.convocazioneDate },
                            time = extractedTime,
                            location = fullLoc.ifBlank { _appointmentLocation.value }
                        )
                        if (result.convocazioneLocation.contains("PIACENZA", ignoreCase = true)) {
                            selectQuesturaAndProvince("piacenza", "PC", "Piacenza")
                        }
                    }

                    val oldStatus = oldCreds?.lastPortaleStatus
                    if (oldStatus != null && oldStatus != "No checks yet" && oldStatus != result.statusText) {
                        com.example.util.NotificationHelper.showStatusNotification(
                            getApplication(),
                            "Aggiornamento Portale Immigrazione",
                            "Stato pratica (${cleanUserId}): ${result.statusText}"
                        )
                    }
                } else {
                    errorCount++
                    errorParts.add("Portale Immigrazione: ${result.statusText} (${result.details})")
                }
            }

            // 2. Check Polizia di Stato
            if (hasPolizia) {
                val latestCreds = repository.getCredentialsDirect()
                val primaryCode = when {
                    cleanAssicurata.isNotBlank() -> cleanAssicurata
                    cleanPratica.isNotBlank() -> cleanPratica
                    !latestCreds?.numeroAssicurata.isNullOrBlank() -> latestCreds?.numeroAssicurata ?: cleanPassword
                    else -> cleanPassword
                }
                val oldPoliziaStatus = latestCreds?.lastPoliziaStatus
                var polResult = repository.checkPoliziaStatus(primaryCode)

                // If primary code was not found and we also have a distinct codiceIstanza / numeroPratica, try that as well
                val secondaryCode = latestCreds?.codiceIstanza?.takeIf { it.isNotBlank() && it != primaryCode }
                    ?: cleanPratica.takeIf { it.isNotBlank() && it != primaryCode }
                if (!polResult.isSuccess && secondaryCode != null) {
                    val secondTry = repository.checkPoliziaStatus(secondaryCode)
                    if (secondTry.isSuccess) {
                        polResult = secondTry
                    }
                }

                if (polResult.isSuccess && polResult.statusCode != AppRepository.StatusCode.NOT_FOUND) {
                    successCount++
                    statusParts.add("Questura: ${polResult.statusText}")
                    detailParts.add("Polizia di Stato: ${polResult.details}")

                    if (oldPoliziaStatus != null && oldPoliziaStatus != "No checks yet" && oldPoliziaStatus != polResult.statusText) {
                        com.example.util.NotificationHelper.showStatusNotification(
                            getApplication(),
                            "Stato Polizia di Stato Aggiornato",
                            "La tua ricevuta ($primaryCode) ha cambiato stato: ${polResult.statusText}"
                        )
                    }
                } else {
                    errorCount++
                    val polInfo = "Archivio Questura (Polizia di Stato): ${polResult.statusText}"
                    if (successCount > 0) {
                        // Do not fail the overall check if Portale Immigrazione succeeded; include Polizia status as info
                        detailParts.add(polInfo)
                    } else {
                        errorParts.add("${polResult.statusText}: ${polResult.details}")
                    }
                }
            }

            if (successCount > 0) {
                _checkState.value = CheckUiState.Success(
                    statusText = statusParts.joinToString(" • "),
                    detailsText = detailParts.joinToString("\n\n")
                )
            } else if (errorCount > 0) {
                _checkState.value = CheckUiState.Error(
                    errorParts.joinToString("\n\n").ifBlank { "Nessun risultato trovato nei portali." }
                )
            } else {
                _checkState.value = CheckUiState.Error("Nessun controllo effettuato")
            }
        }
    }

    fun updateLoggedStatus(
        poliziaStatus: String?,
        portaleStatus: String?,
        codiceIstanza: String? = null,
        dettaglioAnomalie: String? = null,
        convocazioneDate: String? = null,
        convocazioneLocation: String? = null,
        convocazioneMotivo: String? = null
    ) {
        viewModelScope.launch {
            val currentCreds = repository.getCredentialsDirect() ?: UserCredentials()
            var updated = currentCreds
            var statusChanged = false
            var finalStatusText = ""
            var portalType = ""
            var details = ""

            if (poliziaStatus != null && currentCreds.lastPoliziaStatus != poliziaStatus) {
                updated = updated.copy(
                    lastPoliziaStatus = poliziaStatus,
                    lastCheckedTime = System.currentTimeMillis()
                )
                statusChanged = true
                finalStatusText = poliziaStatus
                portalType = "POLIZIA"
                details = "Verificato tramite Portale Web Polizia di Stato"
            }
            if (portaleStatus != null) {
                val changed = currentCreds.lastPortaleStatus != portaleStatus ||
                    (!codiceIstanza.isNullOrBlank() && currentCreds.codiceIstanza != codiceIstanza)
                updated = updated.copy(
                    lastPortaleStatus = portaleStatus,
                    lastCheckedTime = System.currentTimeMillis(),
                    codiceIstanza = codiceIstanza?.ifBlank { currentCreds.codiceIstanza } ?: currentCreds.codiceIstanza,
                    dettaglioAnomalie = dettaglioAnomalie?.ifBlank { currentCreds.dettaglioAnomalie } ?: currentCreds.dettaglioAnomalie,
                    convocazioneDate = convocazioneDate?.ifBlank { currentCreds.convocazioneDate } ?: currentCreds.convocazioneDate,
                    convocazioneLocation = convocazioneLocation?.ifBlank { currentCreds.convocazioneLocation } ?: currentCreds.convocazioneLocation,
                    convocazioneMotivo = convocazioneMotivo?.ifBlank { currentCreds.convocazioneMotivo } ?: currentCreds.convocazioneMotivo
                )
                if (changed) {
                    statusChanged = true
                    finalStatusText = portaleStatus
                    portalType = "PORTALE"
                    details = buildString {
                        append("Verificato tramite Portale Immigrazione")
                        if (!codiceIstanza.isNullOrBlank()) append(" • Istanza: $codiceIstanza")
                        if (!convocazioneDate.isNullOrBlank()) append(" • Convocazione: $convocazioneDate")
                    }
                }
            }

            if (statusChanged) {
                repository.saveCredentials(updated)
                val history = com.example.data.StatusHistory(
                    portalType = portalType,
                    statusText = finalStatusText,
                    isSuccess = !finalStatusText.contains("Non presente", ignoreCase = true),
                    details = details
                )
                repository.insertHistory(history)
            } else if (poliziaStatus != null || portaleStatus != null) {
                repository.saveCredentials(updated.copy(lastCheckedTime = System.currentTimeMillis()))
            }
        }
    }

    fun toggleGuideBookmark(guideId: String) {
        viewModelScope.launch {
            val current = bookmarkedGuideIds.value
            val isBookmarked = current.contains(guideId)
            repository.toggleBookmark(guideId, !isBookmarked)
        }
    }

    fun getGuideChecklistStates(guideId: String): Flow<List<com.example.data.DocumentChecklistState>> {
        return repository.getChecklistStatesFlow(guideId)
    }

    fun toggleGuideDocument(guideId: String, documentId: String, isChecked: Boolean) {
        viewModelScope.launch {
            repository.saveChecklistState(guideId, documentId, isChecked)
        }
    }

    fun resetCheckState() {
        _checkState.value = CheckUiState.Idle
    }

    fun clearCheckHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun deleteProfileAndReset() {
        viewModelScope.launch {
            val emptyCreds = UserCredentials(
                fullName = "",
                userId = "",
                password = "",
                numeroAssicurata = "",
                numeroPratica = "",
                lastCheckedTime = 0L,
                lastPoliziaStatus = "No checks yet",
                lastPortaleStatus = "No checks yet",
                codiceIstanza = "",
                codiceOlogramma = "",
                dettaglioAnomalie = "",
                convocazioneDate = "",
                convocazioneLocation = "",
                convocazioneMotivo = "",
                selectedQuesturaId = null,
                selectedProvinceCode = null,
                selectedPermitType = null
            )
            repository.saveCredentials(emptyCreds)
            repository.clearHistory()
            repository.clearAllSofiaData()

            _onboardingCompleted.value = false
            _selectedProvince.value = "Milano"
            _appointmentDate.value = ""
            _appointmentTime.value = ""
            _appointmentLocation.value = "Questura di Milano"
            _checkedDocs.value = emptySet()

            prefs.edit()
                .putBoolean("onboarding_completed", false)
                .putString("selected_province", "Milano")
                .putString("appointment_date", "")
                .putString("appointment_time", "")
                .putString("appointment_location", "Questura di Milano")
                .putStringSet("checked_docs", emptySet())
                .apply()
        }
    }
}

sealed interface CheckUiState {
    object Idle : CheckUiState
    object Loading : CheckUiState
    data class Success(val statusText: String, val detailsText: String) : CheckUiState
    data class Error(val errorMessage: String) : CheckUiState
}
