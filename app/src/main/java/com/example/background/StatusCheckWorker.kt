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

                // If status changed, notify the user!
                if (oldStatus != "No checks yet" && oldStatus != newStatus) {
                    NotificationHelper.showStatusNotification(
                        applicationContext,
                        "Stato Polizia di Stato Aggiornato",
                        "La tua ricevuta ($poliziaCode) ha cambiato stato: $newStatus"
                    )
                }
            }
        }

        // 2. Check Portale Immigrazione Status
        if (credentials.userId.isNotBlank() && credentials.password.isNotBlank()) {
            val result = repository.checkPortaleStatus(credentials.userId, credentials.password)
            if (result.isSuccess) {
                // Fetch the updated credentials to make sure we compare correctly if they changed
                val updatedCreds = repository.getCredentialsDirect()
                val oldStatus = credentials.lastPortaleStatus
                val newStatus = updatedCreds?.lastPortaleStatus ?: result.statusText

                // If status changed, notify the user!
                if (oldStatus != "No checks yet" && oldStatus != newStatus) {
                    NotificationHelper.showStatusNotification(
                        applicationContext,
                        "Aggiornamento Portale Immigrazione",
                        "Novità sul portale per l'account ${credentials.userId}: $newStatus"
                    )
                }
            }
        }

        return@withContext Result.success()
    }
}
