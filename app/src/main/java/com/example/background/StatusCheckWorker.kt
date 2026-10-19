package com.example.background

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.AppRepository
import com.example.util.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StatusCheckWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val repository = AppRepository(applicationContext)
        val credentials = repository.getCredentialsDirect()

        if (credentials == null) {
            return@withContext Result.success()
        }

        // 1. Check Polizia di Stato Status
        val poliziaCode = when {
            credentials.numeroAssicurata.isNotBlank() -> credentials.numeroAssicurata
            credentials.numeroPratica.isNotBlank() -> credentials.numeroPratica
            else -> ""
        }

        if (poliziaCode.isNotBlank()) {
            val result = repository.checkPoliziaStatus(poliziaCode)
            if (result.isSuccess) {
                val oldStatus = credentials.lastPoliziaStatus
                val newStatus = result.statusText

                val isStatusChanged = oldStatus.isNotBlank() && oldStatus != "No checks yet" && oldStatus != newStatus
                val isReadyNow = newStatus.contains("pronto", ignoreCase = true) || result.statusCode == AppRepository.StatusCode.READY

                // If status changed or permit is ready, notify immediately!
                if (isStatusChanged || isReadyNow) {
                    NotificationHelper.showStatusNotification(
                        applicationContext,
                        "Permesso di Soggiorno: Nuovo Stato",
                        "La tua ricevuta ($poliziaCode): $newStatus"
                    )
                }
            }
        }

        // 2. Check Portale Immigrazione Status
        if (credentials.userId.isNotBlank() && credentials.password.isNotBlank()) {
            val result = repository.checkPortaleStatus(credentials.userId, credentials.password)
            if (result.isSuccess) {
                val updatedCreds = repository.getCredentialsDirect()
                val oldStatus = credentials.lastPortaleStatus
                val newStatus = updatedCreds?.lastPortaleStatus ?: result.statusText

                val isStatusChanged = oldStatus.isNotBlank() && oldStatus != "No checks yet" && oldStatus != newStatus

                if (isStatusChanged) {
                    NotificationHelper.showStatusNotification(
                        applicationContext,
                        "Portale Immigrazione: Aggiornamento",
                        "Novità sul portale per l'account ${credentials.userId}: $newStatus"
                    )
                }
            }
        }

        return@withContext Result.success()
    }
}
