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

    // UI Interactive state for manual check
    private val _checkState = MutableStateFlow<CheckUiState>(CheckUiState.Idle)
    val checkState: StateFlow<CheckUiState> = _checkState.asStateFlow()

    // Form fields editable state
    val formFullName = MutableStateFlow("Karim El Assali")
    val formUserId = MutableStateFlow("550000133317")
    val formPassword = MutableStateFlow("055987476941")
    val formNumeroAssicurata = MutableStateFlow("055987476941")
    val formNumeroPratica = MutableStateFlow("")

    init {
        // Initialize form fields when credentials flow emits
        viewModelScope.launch {
            credentials.collect { creds ->
                if (creds != null) {
                    formFullName.value = creds.fullName
                    formUserId.value = creds.userId
                    formPassword.value = creds.password
                    formNumeroAssicurata.value = creds.numeroAssicurata
                    formNumeroPratica.value = creds.numeroPratica
                } else {
                    // Pre-populate DB with default credentials if empty
                    repository.saveCredentials(UserCredentials())
                }
            }
        }
        
        // Start background worker
        WorkScheduler.scheduleDailyCheck(application)
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
            if (poliziaStatus != null) {
                updated = updated.copy(
                    lastPoliziaStatus = poliziaStatus,
                    lastCheckedTime = System.currentTimeMillis()
                )
            }
            if (portaleStatus != null) {
                updated = updated.copy(
                    lastPortaleStatus = portaleStatus,
                    lastCheckedTime = System.currentTimeMillis()
                )
            }
            if (poliziaStatus != null || portaleStatus != null) {
                repository.saveCredentials(updated)
            }
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
