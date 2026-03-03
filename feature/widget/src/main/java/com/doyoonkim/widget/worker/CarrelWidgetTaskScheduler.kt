package com.doyoonkim.widget.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

// Bypass Dagger's Dependency tree for Scheduler Instance only.
// Kotlin-wise Singleton Instance would be provided.
object CarrelWidgetTaskScheduler {
    fun schedule(context: Context) {
        // Apply Debounce (since the manual scheduling is enabled.)
        val carrelStatusTask = OneTimeWorkRequestBuilder<KnuticeCarrelWidgetSync>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 30,
                timeUnit = TimeUnit.SECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            uniqueWorkName = "Carrel Room Status Widget Sync",
            existingWorkPolicy = ExistingWorkPolicy.REPLACE,        // Enable debounce.
            request = carrelStatusTask
        )
    }
}