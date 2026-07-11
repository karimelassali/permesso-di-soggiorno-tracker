package com.example.background

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkScheduler {
    private const val WORK_NAME = "PermessoDailyStatusCheck"

    fun scheduleDailyCheck(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicRequest = PeriodicWorkRequestBuilder<StatusCheckWorker>(
            24, TimeUnit.HOURS, // Every day
            2, TimeUnit.HOURS   // Flex interval: allow a 2-hour window for execution flex
        )
            .setConstraints(constraints)
            .build()

        try {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP, // Keep the existing schedule if it's already there
                periodicRequest
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
