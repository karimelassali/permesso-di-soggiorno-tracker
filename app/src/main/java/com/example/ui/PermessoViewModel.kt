package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.background.WorkScheduler
import com.example.background.CheckInterval
import com.example.data.AppRepository
import com.example.data.SavedCredentialVaultEntry
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

    val vaultEntries: StateFlow<List<SavedCredentialVaultEntry>> = repository.vaultEntriesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isBiometricSessionUnlocked = MutableStateFlow(false)
    val isBiometricSessionUnlocked: StateFlow<Boolean> = _isBiometricSessionUnlocked.asStateFlow()

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

    private val _selectedProvince = MutableStateFlow(prefs.getString("selected_province", "Roma") ?: "Roma")
    val selectedProvince: StateFlow<String> = _selectedProvince.asStateFlow()

    private val _appointmentDate = MutableStateFlow(prefs.getString("appointment_date", "") ?: "")
    val appointmentDate: StateFlow<String> = _appointmentDate.asStateFlow()

    private val _appointmentTime = MutableStateFlow(prefs.getString("appointment_time", "") ?: "")
    val appointmentTime: StateFlow<String> = _appointmentTime.asStateFlow()

    private val _appointmentLocation = MutableStateFlow(prefs.getString("appointment_location", "") ?: "")
    val appointmentLocation: StateFlow<String> = _appointmentLocation.asStateFlow()

    // Extended dynamic profile fields (SharedPreferences backed)
    private val _passportNumber = MutableStateFlow(prefs.getString("passport_number", "") ?: "")
    val passportNumber: StateFlow<String> = _passportNumber.asStateFlow()

    private val _passportExpiry = MutableStateFlow(prefs.getString("passport_expiry", "") ?: "")
    val passportExpiry: StateFlow<String> = _passportExpiry.asStateFlow()

    private val _hospitalityProtocol = MutableStateFlow(prefs.getString("hospitality_protocol", "") ?: "")
    val hospitalityProtocol: StateFlow<String> = _hospitalityProtocol.asStateFlow()

    private val _municipalityName = MutableStateFlow(prefs.getString("municipality_name", "") ?: "")
    val municipalityName: StateFlow<String> = _municipalityName.asStateFlow()

    private val _receiptSubmissionDate = MutableStateFlow(prefs.getString("receipt_submission_date", "") ?: "")
    val receiptSubmissionDate: StateFlow<String> = _receiptSubmissionDate.asStateFlow()

    private val _isInstitutionalDemoMode = MutableStateFlow(prefs.getBoolean("institutional_demo_mode", false))
    val isInstitutionalDemoMode: StateFlow<Boolean> = _isInstitutionalDemoMode.asStateFlow()

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("apple_dark_mode", false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isBackgroundSyncEnabled = MutableStateFlow(prefs.getBoolean("bg_sync_enabled", true))
    val isBackgroundSyncEnabled: StateFlow<Boolean> = _isBackgroundSyncEnabled.asStateFlow()

    // 60-second anti-DDoS / rate-limit cooldown state (persisted across restarts)
    private var lastCheckTimestampMillis: Long = prefs.getLong("last_check_timestamp_millis", 0L)
    private val _cooldownSecondsRemaining = MutableStateFlow(0)
    val cooldownSecondsRemaining: StateFlow<Int> = _cooldownSecondsRemaining.asStateFlow()
    private var cooldownJob: kotlinx.coroutines.Job? = null

    private val _checkedDocs = MutableStateFlow<Set<String>>(
        prefs.getStringSet("checked_docs", emptySet()) ?: emptySet()
    )
    val checkedDocs: StateFlow<Set<String>> = _checkedDocs.asStateFlow()

    private val _syncInterval = MutableStateFlow(WorkScheduler.getSavedInterval(application))
    val syncInterval: StateFlow<CheckInterval> = _syncInterval.asStateFlow()

    fun updateSyncInterval(newInterval: CheckInterval) {
        _syncInterval.value = newInterval
        WorkScheduler.saveIntervalAndReschedule(getApplication(), newInterval)
    }

    fun applyScannedReceipt(data: com.example.util.ReceiptData) {
        if (data.fullName.isNotBlank()) formFullName.value = data.fullName
        if (data.userId.isNotBlank()) formUserId.value = data.userId
        if (data.password.isNotBlank()) {
            formPassword.value = data.password
            if (formNumeroAssicurata.value.isBlank()) {
                formNumeroAssicurata.value = data.numeroAssicurata.ifBlank { data.password.replace("-", "").replace(" ", "").trim() }
            }
        }
        if (data.numeroAssicurata.isNotBlank() && formNumeroAssicurata.value.isBlank()) {
            formNumeroAssicurata.value = data.numeroAssicurata
        }
        if (data.numeroPratica.isNotBlank()) {
            formNumeroPratica.value = data.numeroPratica
        }
    }

    init {
        // Resume any active 60-second cooldown across app restarts
        val now = System.currentTimeMillis()
        if (lastCheckTimestampMillis > 0L) {
            val elapsedSeconds = ((now - lastCheckTimestampMillis) / 1000L).toInt()
            if (elapsedSeconds in 0 until COOLDOWN_DURATION_SECONDS) {
                startCooldownTicker(COOLDOWN_DURATION_SECONDS - elapsedSeconds)
            }
        }

        viewModelScope.launch {
            val existing = repository.getCredentialsDirect()
            if (existing != null) {
                formFullName.value = existing.fullName
                formUserId.value = existing.userId
                formPassword.value = existing.password
                formNumeroAssicurata.value = existing.numeroAssicurata
                formNumeroPratica.value = existing.numeroPratica
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

    fun setInstitutionalDemoMode(enabled: Boolean) {
        _isInstitutionalDemoMode.value = enabled
        prefs.edit().putBoolean("institutional_demo_mode", enabled).apply()
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        prefs.edit().putBoolean("apple_dark_mode", enabled).apply()
    }

    fun setBackgroundSyncEnabled(enabled: Boolean) {
        _isBackgroundSyncEnabled.value = enabled
        prefs.edit().putBoolean("bg_sync_enabled", enabled).apply()
        if (enabled) {
            WorkScheduler.scheduleDailyCheck(getApplication())
        }
    }

    fun updateExtendedProfile(
        passportNum: String = _passportNumber.value,
        passportExp: String = _passportExpiry.value,
        hospProtocol: String = _hospitalityProtocol.value,
        municipality: String = _municipalityName.value,
        submissionDate: String = _receiptSubmissionDate.value
    ) {
        _passportNumber.value = passportNum.trim()
        _passportExpiry.value = passportExp.trim()
        _hospitalityProtocol.value = hospProtocol.trim()
        _municipalityName.value = municipality.trim()
        _receiptSubmissionDate.value = submissionDate.trim()
        prefs.edit()
            .putString("passport_number", _passportNumber.value)
            .putString("passport_expiry", _passportExpiry.value)
            .putString("hospitality_protocol", _hospitalityProtocol.value)
            .putString("municipality_name", _municipalityName.value)
            .putString("receipt_submission_date", _receiptSubmissionDate.value)
            .apply()
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

    fun setBiometricProtectionEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setBiometricProtectionEnabled(enabled)
            if (enabled) {
                _isBiometricSessionUnlocked.value = true
                repository.recordBiometricAuthentication()
            } else {
                _isBiometricSessionUnlocked.value = false
            }
        }
    }

    fun unlockWithBiometrics() {
        _isBiometricSessionUnlocked.value = true
        viewModelScope.launch {
            repository.recordBiometricAuthentication()
        }
    }

    fun lockBiometricSession() {
        _isBiometricSessionUnlocked.value = false
    }

    fun saveCurrentCredentialsToVault(customLabel: String = "") {
        viewModelScope.launch {
            val cleanUserId = formUserId.value.replace("-", "").replace(" ", "").trim()
            val cleanPassword = formPassword.value.replace("-", "").replace(" ", "").trim()
            val cleanAssicurata = formNumeroAssicurata.value.replace("-", "").replace(" ", "").trim()
                .ifBlank { if (cleanPassword.length == 12 && cleanPassword.all { it.isDigit() }) cleanPassword else "" }
            val cleanPratica = formNumeroPratica.value.replace("-", "").replace(" ", "").trim()
            val cleanName = formFullName.value.trim()

            if (cleanUserId.isBlank() && cleanPassword.isBlank() && cleanAssicurata.isBlank() && cleanPratica.isBlank()) {
                return@launch
            }

            val label = customLabel.trim().ifBlank {
                if (cleanName.isNotBlank()) "Dossier • $cleanName" else "Pratica Mod. 22A"
            }

            val existingEntries = repository.getVaultEntriesDirect()
            val matching = existingEntries.find {
                (cleanAssicurata.isNotBlank() && it.numeroAssicurata == cleanAssicurata) ||
                (cleanUserId.isNotBlank() && it.userId == cleanUserId) ||
                (cleanPratica.isNotBlank() && it.numeroPratica == cleanPratica)
            }

            val entry = SavedCredentialVaultEntry(
                entryId = matching?.entryId ?: 0L,
                label = label,
                fullName = cleanName,
                userId = cleanUserId,
                password = cleanPassword,
                numeroAssicurata = cleanAssicurata,
                numeroPratica = cleanPratica,
                selectedProvince = _selectedProvince.value,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveVaultEntry(entry)
        }
    }

    fun applyVaultEntryToActiveCredentials(entry: SavedCredentialVaultEntry) {
        viewModelScope.launch {
            _isBiometricSessionUnlocked.value = true
            formFullName.value = entry.fullName
            formUserId.value = entry.userId
            formPassword.value = entry.password
            formNumeroAssicurata.value = entry.numeroAssicurata
            formNumeroPratica.value = entry.numeroPratica
            if (entry.selectedProvince.isNotBlank()) {
                _selectedProvince.value = entry.selectedProvince
                prefs.edit().putString("selected_province", entry.selectedProvince).apply()
            }

            val currentCreds = repository.getCredentialsDirect() ?: UserCredentials()
            val updated = currentCreds.copy(
                fullName = entry.fullName,
                userId = entry.userId,
                password = entry.password,
                numeroAssicurata = entry.numeroAssicurata,
                numeroPratica = entry.numeroPratica,
                lastBiometricAuthTimestamp = System.currentTimeMillis()
            )
            repository.saveCredentials(updated)
        }
    }

    fun deleteVaultEntry(entryId: Long) {
        viewModelScope.launch {
            repository.deleteVaultEntry(entryId)
        }
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
            saveCurrentCredentialsToVault()
            WorkScheduler.scheduleDailyCheck(getApplication())

            // Automatically run a live check against the portals with the newly saved credentials
            checkStatusNow()
        }
    }

    private fun startCooldownTicker(initialSeconds: Int = COOLDOWN_DURATION_SECONDS) {
        cooldownJob?.cancel()
        _cooldownSecondsRemaining.value = initialSeconds.coerceAtLeast(0)
        cooldownJob = viewModelScope.launch {
            while (_cooldownSecondsRemaining.value > 0) {
                kotlinx.coroutines.delay(1000L)
                _cooldownSecondsRemaining.value = (_cooldownSecondsRemaining.value - 1).coerceAtLeast(0)
            }
        }
    }

    fun checkStatusNow() {
        if (_cooldownSecondsRemaining.value > 0) {
            return
        }

        val demoActive = _isInstitutionalDemoMode.value
        val cleanUserId = formUserId.value.replace("-", "").replace(" ", "").trim()
            .ifBlank { if (demoActive) DEMO_USER_ID else "" }
        val cleanPassword = formPassword.value.replace("-", "").replace(" ", "").trim()
            .ifBlank { if (demoActive) DEMO_ASSICURATA else "" }
        val cleanAssicurata = formNumeroAssicurata.value.replace("-", "").replace(" ", "").trim()
            .ifBlank { if (demoActive) DEMO_ASSICURATA else "" }
        val cleanPratica = formNumeroPratica.value.replace("-", "").replace(" ", "").trim()
            .ifBlank { if (demoActive) DEMO_ISTANZA else "" }

        val hasPortale = cleanUserId.isNotBlank() && cleanPassword.isNotBlank()
        val hasPolizia = cleanAssicurata.isNotBlank() || cleanPratica.isNotBlank() || (cleanPassword.length == 12 && cleanPassword.all { it.isDigit() })

        if (!hasPolizia && !hasPortale) {
            _checkState.value = CheckUiState.Error("Si prega di salvare prima un codice di ricevuta valido o le credenziali del portale.")
            return
        }

        // Record timestamp and start 60-second anti-DDoS cooldown ticker
        lastCheckTimestampMillis = System.currentTimeMillis()
        prefs.edit().putLong("last_check_timestamp_millis", lastCheckTimestampMillis).apply()
        startCooldownTicker(COOLDOWN_DURATION_SECONDS)

        _checkState.value = CheckUiState.Loading

        viewModelScope.launch {
            try {
                var successCount = 0
                var portalUnreachableCount = 0
                var errorCount = 0
                val statusParts = mutableListOf<String>()
                val detailParts = mutableListOf<String>()
                val errorParts = mutableListOf<String>()

                // 1. Check Portale Immigrazione (authenticated Dettaglio Istanza)
                if (hasPortale) {
                    val oldCreds = repository.getCredentialsDirect()
                    val result = repository.checkPortaleStatus(cleanUserId, cleanPassword)
                    if (result.isSuccess &&
                        result.statusCode != AppRepository.StatusCode.NOT_FOUND &&
                        result.statusCode != AppRepository.StatusCode.PORTAL_UNREACHABLE
                    ) {
                        successCount++
                        statusParts.add("Portale: ${result.statusText}")
                        detailParts.add(result.details)

                        // Automatically sync Convocazione appointment details if present in Portale Immigrazione
                        if (result.convocazioneDate.isNotBlank()) {
                            val timeRegex = Regex("""(\d{1,2}:\d{2}(?::\d{2})?)""")
                            val extractedTime = timeRegex.find(result.convocazioneDate)?.groupValues?.getOrNull(1) ?: ""
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
                        }

                        val oldStatus = oldCreds?.lastPortaleStatus
                        if (oldStatus != null && oldStatus != "No checks yet" && oldStatus != result.statusText) {
                            com.example.util.NotificationHelper.showStatusNotification(
                                getApplication(),
                                "Aggiornamento Portale Immigrazione",
                                "Stato pratica (${cleanUserId}): ${result.statusText}"
                            )
                        }
                    } else if (result.statusCode == AppRepository.StatusCode.PORTAL_UNREACHABLE) {
                        portalUnreachableCount++
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

                    if (polResult.isSuccess &&
                        polResult.statusCode != AppRepository.StatusCode.NOT_FOUND &&
                        polResult.statusCode != AppRepository.StatusCode.PORTAL_UNREACHABLE
                    ) {
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
                    } else if (polResult.statusCode == AppRepository.StatusCode.PORTAL_UNREACHABLE) {
                        portalUnreachableCount++
                        if (successCount > 0) {
                            detailParts.add("Archivio Polizia di Stato: ${AppRepository.PORTAL_UNREACHABLE_MSG}")
                        }
                    } else if (polResult.statusCode == AppRepository.StatusCode.NOT_FOUND) {
                        successCount++
                        statusParts.add("Questura: ${polResult.statusText}")
                        detailParts.add("Polizia di Stato: ${polResult.details}")
                    } else {
                        errorCount++
                        val polInfo = "Archivio Questura (Polizia di Stato): ${polResult.statusText}"
                        if (successCount > 0) {
                            detailParts.add(polInfo)
                        } else {
                            errorParts.add("${polResult.statusText}: ${polResult.details}")
                        }
                    }
                }

                when {
                    successCount > 0 -> {
                        _checkState.value = CheckUiState.Success(
                            statusText = statusParts.joinToString(" • "),
                            detailsText = detailParts.joinToString("\n\n")
                        )
                    }
                    errorCount > 0 -> {
                        _checkState.value = CheckUiState.Error(
                            errorMessage = errorParts.joinToString("\n\n").ifBlank { "Credenziali o codice non riconosciuti. Verifica i dati inseriti." }
                        )
                    }
                    portalUnreachableCount > 0 -> {
                        _checkState.value = CheckUiState.PortalUnreachable(
                            message = AppRepository.PORTAL_UNREACHABLE_MSG
                        )
                    }
                    else -> {
                        _checkState.value = CheckUiState.Error(
                            errorMessage = "Nessun dato trovato per i parametri specificati. Controlla User ID o Password."
                        )
                    }
                }
            } catch (e: Exception) {
                _checkState.value = CheckUiState.Error(
                    errorMessage = e.localizedMessage ?: "Errore durante la verifica dello stato."
                )
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
            repository.clearAllVaultEntries()
            repository.clearHistory()
            repository.clearAllSofiaData()

            _isBiometricSessionUnlocked.value = false
            _onboardingCompleted.value = false
            _selectedProvince.value = "Roma"
            _appointmentDate.value = ""
            _appointmentTime.value = ""
            _appointmentLocation.value = ""
            _passportNumber.value = ""
            _passportExpiry.value = ""
            _hospitalityProtocol.value = ""
            _municipalityName.value = ""
            _receiptSubmissionDate.value = ""
            _isInstitutionalDemoMode.value = false
            _checkedDocs.value = emptySet()
            cooldownJob?.cancel()
            _cooldownSecondsRemaining.value = 0
            lastCheckTimestampMillis = 0L

            prefs.edit()
                .putBoolean("onboarding_completed", false)
                .putString("selected_province", "Roma")
                .putString("appointment_date", "")
                .putString("appointment_time", "")
                .putString("appointment_location", "")
                .putString("passport_number", "")
                .putString("passport_expiry", "")
                .putString("hospitality_protocol", "")
                .putString("municipality_name", "")
                .putString("receipt_submission_date", "")
                .putBoolean("institutional_demo_mode", false)
                .putLong("last_check_timestamp_millis", 0L)
                .putStringSet("checked_docs", emptySet())
                .apply()
        }
    }

    companion object {
        const val COOLDOWN_DURATION_SECONDS = 60
        const val DEMO_FULL_NAME = "MARIO ROSSI"
        const val DEMO_PASSPORT = "AA0000000"
        const val DEMO_PASSPORT_EXPIRY = "01/01/2030"
        const val DEMO_ASSICURATA = "050000000000"
        const val DEMO_ISTANZA = "00XX00000000"
        const val DEMO_USER_ID = "000000000000"
        const val DEMO_COMUNE = "Comune di Esempio"
        const val DEMO_HOSPITALITY_PROT = "Prot. 00/2026"
        const val DEMO_SUBMISSION_DATE = "01/01/2026"
    }
}

sealed interface CheckUiState {
    object Idle : CheckUiState
    object Loading : CheckUiState
    data class Success(val statusText: String, val detailsText: String) : CheckUiState
    data class PortalUnreachable(
        val message: String = AppRepository.PORTAL_UNREACHABLE_MSG
    ) : CheckUiState
    data class Error(val errorMessage: String) : CheckUiState
}
