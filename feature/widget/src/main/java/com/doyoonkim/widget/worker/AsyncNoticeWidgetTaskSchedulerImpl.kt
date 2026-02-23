package com.doyoonkim.widget.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.doyoonkim.model.di.ApplicationContext
import com.doyoonkim.domain.interfaces.AsyncNoticeWidgetTaskScheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AsyncNoticeWidgetTaskSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AsyncNoticeWidgetTaskScheduler {

    override operator fun invoke() {
        // Debounced Task Registration.
        val noticeWidgetTask = OneTimeWorkRequestBuilder<KnuticeWidgetSync>()
            .setInitialDelay(1, TimeUnit.SECONDS)   // Prevent massive work assigned at the same time.
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.LINEAR,
                backoffDelay = 10,
                timeUnit = TimeUnit.MINUTES
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            uniqueWorkName = "Widget Notice Fetch",
            existingWorkPolicy = ExistingWorkPolicy.REPLACE,
            request = noticeWidgetTask
        )
    }
}