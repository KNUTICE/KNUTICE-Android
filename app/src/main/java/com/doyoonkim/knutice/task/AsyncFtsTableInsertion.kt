package com.doyoonkim.knutice.task

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.doyoonkim.model.di.ApplicationContext
import com.doyoonkim.domain.interfaces.BookmarkLocalRepository
import com.doyoonkim.domain.usecases.InsertPendingFtsEntries
import com.doyoonkim.common.worker.IntermediateWorkerFactory
import kotlinx.coroutines.flow.first
import javax.inject.Inject

// Task
class AsyncFtsTableInsertion(
    appContext: Context,
    workerParams: WorkerParameters,
    private val localRepository: BookmarkLocalRepository,
    private val insertPendingFtsEntries: InsertPendingFtsEntries
): CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val batchResult = localRepository.queryPendingBookmarkFtsBatched(50)

        // If there is no pending entities, terminate Scheduled work.
        if (batchResult.isEmpty()) return Result.success()

        Log.d("AsyncTask", "${batchResult.toString()}")

        try {
            val processedId = mutableListOf<Int>()
            for (e in batchResult) {
                val insertionResult = insertPendingFtsEntries.execute(e)
                if (insertionResult) {
                    processedId.add(e.stagingId)
                }
            }
            val result = localRepository.removePendingBookmarkFtsEntry(processedId).first()
            if (!result) return Result.retry()

        } catch (e: Exception) {
            Log.d("AsyncTask", "Unable to process: ${e.toString()}")
            return Result.retry()
        }
        return if (localRepository.queryPendingBookmarkFtsBatched(1).isNotEmpty()) {
            Result.retry()
        } else {
            Result.success()
        }
    }


    class Factory @Inject constructor(
        @ApplicationContext private val context: Context,
        private val localRepository: BookmarkLocalRepository,
        private val usecase: InsertPendingFtsEntries
    ): IntermediateWorkerFactory {
        override fun create(params: WorkerParameters): ListenableWorker {
            return AsyncFtsTableInsertion(context, params, localRepository, usecase)
        }
    }
}