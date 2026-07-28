package com.doyoonkim.knutice.task

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.doyoonkim.domain.interfaces.MajorSubscriptionUpdateTaskScheduler
import com.doyoonkim.model.di.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MajorSubscriptionUpdateSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager
) : MajorSubscriptionUpdateTaskScheduler {
    override fun execute() {
        val onetimeWorkRequest = OneTimeWorkRequestBuilder<MajorSubscriptionUpdate>()
            // let OS handles debounce.
            .setInitialDelay(500, TimeUnit.MILLISECONDS)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()

        workManager.enqueueUniqueWork(
            "Updated Major Subscription Synchronization Work",
            ExistingWorkPolicy.REPLACE,
            onetimeWorkRequest
        )
    }
}
