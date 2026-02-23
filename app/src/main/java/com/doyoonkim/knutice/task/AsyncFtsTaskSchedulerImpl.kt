package com.doyoonkim.knutice.task

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.doyoonkim.model.di.ApplicationContext
import com.doyoonkim.domain.interfaces.AsyncFtsTaskScheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AsyncFtsTaskSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
): AsyncFtsTaskScheduler {
    override fun execute() {
        val task = OneTimeWorkRequestBuilder<AsyncFtsTableInsertion>()
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                5, TimeUnit.SECONDS
            ).build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "Asynchronous FTS Table Insertion",
            ExistingWorkPolicy.REPLACE,
            task
        )
    }

}