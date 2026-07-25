package com.doyoonkim.widget.worker

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.doyoonkim.domain.interfaces.AsyncCarrelWidgetTaskScheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CarrelWidgetTaskSchedulerImpl @Inject constructor(
    private val workManager: WorkManager
) : AsyncCarrelWidgetTaskScheduler {
    companion object {
        private const val PERIODIC_WORK_KEY = "CARREL_WIDGET_PERIODIC_UPDATE"
        private const val ONETIME_WORK_KEY = "CARREL_WIDGET_ONETIME_UPDATE"
    }

    override fun schedulePeriodicTask() {
        val periodicTask = PeriodicWorkRequestBuilder<KnuticeCarrelWidgetSync>(
            30,
            TimeUnit.MINUTES
        ).setBackoffCriteria(
            backoffPolicy = BackoffPolicy.EXPONENTIAL,
            backoffDelay = 5,
            timeUnit = TimeUnit.MINUTES
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()
        ).build()

        workManager.enqueueUniquePeriodicWork(
            uniqueWorkName = PERIODIC_WORK_KEY,
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
            periodicTask
        )
    }

    override fun unschedulePeriodicTask() {
        workManager.cancelUniqueWork(PERIODIC_WORK_KEY)
    }

    override fun executeImmediateTask() {
        val carrelStatusTask = OneTimeWorkRequestBuilder<KnuticeCarrelWidgetSync>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 30,
                timeUnit = TimeUnit.SECONDS
            ).setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.enqueueUniqueWork(
            uniqueWorkName = ONETIME_WORK_KEY,
            existingWorkPolicy = ExistingWorkPolicy.REPLACE, // Enable debounce.
            request = carrelStatusTask
        )
    }
}
