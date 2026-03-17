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
import com.doyoonkim.domain.interfaces.AsyncNoticeWidgetTaskScheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AsyncNoticeWidgetTaskSchedulerImpl @Inject constructor(
    private val workManager: WorkManager
) : AsyncNoticeWidgetTaskScheduler {
    companion object {
        private const val PERIODIC_WORK_KEY = "PERIODIC_WORK_KEY"
        private const val ONETIME_WORK_KEY = "ONETIME_WORK_KEY"
    }

    override fun schedulePeriodicTask() {
        val periodicTask = PeriodicWorkRequestBuilder<KnuticeWidgetSync>(
            6, TimeUnit.HOURS
        ).setBackoffCriteria(
            backoffPolicy = BackoffPolicy.EXPONENTIAL,
            backoffDelay = 30,
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
        val noticeWidgetTask = OneTimeWorkRequestBuilder<KnuticeWidgetSync>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 10,
                timeUnit = TimeUnit.MINUTES
            )
            .build()

        workManager.enqueueUniqueWork(
            uniqueWorkName = ONETIME_WORK_KEY,
            existingWorkPolicy = ExistingWorkPolicy.REPLACE,
            request = noticeWidgetTask
        )
    }
}