package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.background.WorkScheduler
import com.example.data.AppRepository
import com.example.data.UserCredentials
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow
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
    val formFullName = MutableStateFlow("Karim El Assali")
    val formUserId = MutableStateFlow("550000133317")
    val formPassword = MutableStateFlow("055987476941")
    val formNumeroAssicurata = MutableStateFlow("055987476941")
    val formNumeroPratica = MutableStateFlow("")

    // PREMIUM EXTRA FEATURES STATES (Shared Preferences Backed)
    private val prefs = application.getSharedPreferences("permesso_extra_prefs", android.content.Context.MODE_PRIVATE)

    private val _selectedLanguage = MutableStateFlow(prefs.getString("selected_language", "it") ?: "it")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _onboardingCompleted = MutableStateFlow(prefs.getBoolean("onboarding_completed", false))
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    private val _selectedProvince = MutableStateFlow(prefs.getString("selected_province", "Milano") ?: "Milano")
    val selectedProvince: StateFlow<String> = _selectedProvince.asStateFlow()

    private val _appointmentDate = MutableStateFlow(prefs.getString("appointment_date", "") ?: "")
    val appointmentDate: StateFlow<String> = _appointmentDate.asStateFlow()

    private val _appointmentTime = MutableStateFlow(prefs.getString("appointment_time", "") ?: "")
    val appointmentTime: StateFlow<String> = _appointmentTime.asStateFlow()

    private val _appointmentLocation = MutableStateFlow(prefs.getString("appointment_location", "Questura di Milano") ?: "Questura di Milano")
    val appointmentLocation: StateFlow<String> = _appointmentLocation.asStateFlow()

    private val _checkedDocs = MutableStateFlow<Set<String>>(
        prefs.getStringSet("checked_docs", emptySet()) ?: emptySet()
    )
    val checkedDocs: StateFlow<Set<String>> = _checkedDocs.asStateFlow()

    init {
        viewModelScope.launch {
            // Check if there are existing credentials in DB first directly
            val existing = repository.getCredentialsDirect()
            if (existing == null) {
                // Pre-populate only if completely empty in DB
                repository.saveCredentials(UserCredentials())
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
            val currentCreds = credentials.value ?: UserCredentials()
            val updated = currentCreds.copy(
                fullName = formFullName.value,
                userId = formUserId.value,
                password = formPassword.value,
                numeroAssicurata = formNumeroAssicurata.value,
                numeroPratica = formNumeroPratica.value
            )
            repository.saveCredentials(updated)
            // Trigger background scheduler
            WorkScheduler.scheduleDailyCheck(getApplication())
        }
    }

    fun checkStatusNow() {
        val hasPolizia = formNumeroAssicurata.value.isNotBlank() || formNumeroPratica.value.isNotBlank()
        val hasPortale = formUserId.value.isNotBlank() && formPassword.value.isNotBlank()

        if (!hasPolizia && !hasPortale) {
            _checkState.value = CheckUiState.Error("Si prega di salvare prima un codice di ricevuta valido o le credenziali del portale.")
            return
        }

        _checkState.value = CheckUiState.Loading

        viewModelScope.launch {
            var successCount = 0
            var errorCount = 0
            var statusMessage = ""
            var detailsMessage = ""

            // 1. Check Polizia di Stato
            if (hasPolizia) {
                val code = if (formNumeroAssicurata.value.isNotBlank()) formNumeroAssicurata.value else formNumeroPratica.value
                val oldCreds = repository.getCredentialsDirect()
                val result = repository.checkPoliziaStatus(code)
                if (result.isSuccess) {
                    successCount++
                    statusMessage = result.statusText
                    detailsMessage = result.details
                    
                    // Trigger notification on change
                    val oldStatus = oldCreds?.lastPoliziaStatus
                    if (oldStatus != "No checks yet" && oldStatus != result.statusText) {
                        com.example.util.NotificationHelper.showStatusNotification(
                            getApplication(),
                            "Stato Polizia di Stato Aggiornato",
                            "La tua ricevuta ($code) ha cambiato stato: ${result.statusText}"
                        )
                    }
                } else {
                    errorCount++
                    statusMessage = result.statusText
                }
            }

            // 2. Check Portale Immigrazione
            if (hasPortale) {
                val oldCreds = repository.getCredentialsDirect()
                val result = repository.checkPortaleStatus(formUserId.value, formPassword.value)
                if (result.isSuccess) {
                    successCount++
                    if (statusMessage.isEmpty()) {
                        statusMessage = result.statusText
                        detailsMessage = result.details
                    } else {
                        statusMessage += " • " + result.statusText
                        detailsMessage += "\n\n" + result.details
                    }

                    // Trigger notification on change
                    val oldStatus = oldCreds?.lastPortaleStatus
                    if (oldStatus != "No checks yet" && oldStatus != result.statusText) {
                        com.example.util.NotificationHelper.showStatusNotification(
                            getApplication(),
                            "Aggiornamento Portale Immigrazione",
                            "Novità sul portale per l'account ${formUserId.value}: ${result.statusText}"
                        )
                    }
                } else {
                    errorCount++
                    if (statusMessage.isEmpty()) {
                        statusMessage = result.statusText
                    }
                }
            }

            if (successCount > 0) {
                _checkState.value = CheckUiState.Success(statusMessage, detailsMessage)
            } else {
                _checkState.value = CheckUiState.Error(statusMessage)
            }
        }
    }

    fun updateLoggedStatus(poliziaStatus: String?, portaleStatus: String?) {
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
            if (portaleStatus != null && currentCreds.lastPortaleStatus != portaleStatus) {
                updated = updated.copy(
                    lastPortaleStatus = portaleStatus,
                    lastCheckedTime = System.currentTimeMillis()
                )
                statusChanged = true
                finalStatusText = portaleStatus
                portalType = "PORTALE"
                details = "Verificato tramite Portale Web Portale Immigrazione"
            }

            if (statusChanged) {
                repository.saveCredentials(updated)
                // Insert a history record to keep track
                val history = com.example.data.StatusHistory(
                    portalType = portalType,
                    statusText = finalStatusText,
                    isSuccess = true,
                    details = details
                )
                repository.insertHistory(history)
            } else if (poliziaStatus != null || portaleStatus != null) {
                // Just update the last checked time so we know a check was made
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
}

sealed interface CheckUiState {
    object Idle : CheckUiState
    object Loading : CheckUiState
    data class Success(val statusText: String, val detailsText: String) : CheckUiState
    data class Error(val errorMessage: String) : CheckUiState
}
