package com.doyoonkim.notification.task

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.doyoonkim.common.di.ApplicationContext
import com.doyoonkim.common.di.TokenHandler
import com.doyoonkim.model.TokenStatus
import com.doyoonkim.common.worker.IntermediateWorkerFactory
import javax.inject.Inject

class PeriodicTokenRegistration(
    appContext: Context,
    workerParams: WorkerParameters,
    private val tokenHandler: TokenHandler
): CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("Token Worker", "Start Token Worker")

        val registrationResult = tokenHandler.invoke()
        return when(registrationResult) {
            TokenStatus.SUCCESS -> Result.success()
            TokenStatus.RETRY -> Result.retry()
            TokenStatus.FAILURE -> Result.failure()
        }
    }

    class Factory @Inject constructor(
        @ApplicationContext private val context: Context,
        private val tokenHandler: TokenHandler
    ): IntermediateWorkerFactory {
        override fun create(params: WorkerParameters): PeriodicTokenRegistration {
            return PeriodicTokenRegistration(context, params, tokenHandler)
        }

    }
}