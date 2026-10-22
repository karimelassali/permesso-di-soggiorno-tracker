package com.example.background

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

enum class CheckInterval(val hours: Long, val titleIt: String, val titleEn: String) {
    HOURS_6(6, "Ogni 6 Ore", "Every 6 Hours"),
    HOURS_12(12, "Ogni 12 Ore", "Every 12 Hours"),
    DAILY(24, "Giornaliero (Consigliato)", "Daily (Recommended)"),
    WEEKLY(168, "Settimanale (7 Giorni)", "Weekly (7 Days)"),
    MONTHLY(720, "Mensile (30 Giorni)", "Monthly (30 Days)");

    companion object {
        fun fromHours(hours: Long): CheckInterval =
            entries.find { it.hours == hours } ?: DAILY
    }
}

object WorkScheduler {
    private const val WORK_NAME = "PermessoDailyStatusCheck"
    const val PREF_INTERVAL_HOURS = "pref_sync_interval_hours"

    fun getSavedInterval(context: Context): CheckInterval {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val hours = prefs.getLong(PREF_INTERVAL_HOURS, 24L)
        return CheckInterval.fromHours(hours)
    }

    fun saveIntervalAndReschedule(context: Context, interval: CheckInterval) {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        prefs.edit().putLong(PREF_INTERVAL_HOURS, interval.hours).apply()
        scheduleStatusCheck(context, interval.hours)
    }

    fun scheduleDailyCheck(context: Context) {
        val currentInterval = getSavedInterval(context)
        scheduleStatusCheck(context, currentInterval.hours)
    }

    fun scheduleStatusCheck(context: Context, intervalHours: Long) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val flexHours = (intervalHours / 10).coerceAtLeast(1)

        val periodicRequest = PeriodicWorkRequestBuilder<StatusCheckWorker>(
            intervalHours, TimeUnit.HOURS,
            flexHours, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        try {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                periodicRequest
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Enqueues an immediate one-time background check worker to run in the background.
     */
    fun runImmediateCheck(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val oneTimeWork = androidx.work.OneTimeWorkRequestBuilder<StatusCheckWorker>()
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context).enqueue(oneTimeWork)
    }
}
