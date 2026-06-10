package com.doyoonkim.widget.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.doyoonkim.model.di.ApplicationContext
import com.doyoonkim.common.worker.IntermediateWorkerFactory
import com.doyoonkim.domain.interfaces.AppSubscriptionPreferenceRepository
import com.doyoonkim.domain.interfaces.AppWidgetPreferenceRepository
import com.doyoonkim.domain.interfaces.NoticeRemoteRepository
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.model.WidgetCategoryPolicy
import com.doyoonkim.widget.model.WidgetNoticeVO
import com.doyoonkim.widget.model.WidgetState
import com.doyoonkim.widget.util.WidgetStateUpdater
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class KnuticeWidgetSync(
    appContext: Context,
    workerParam: WorkerParameters,
    private val appWidgetPreference: AppWidgetPreferenceRepository,
    private val appSubscriptionPreference: AppSubscriptionPreferenceRepository,
    private val stateUpdater: WidgetStateUpdater,
    private val remoteRepository: NoticeRemoteRepository
) : CoroutineWorker(appContext, workerParam) {
    private val TAG = "KnuticeWidgetSync"
    private lateinit var category: String

    override suspend fun doWork(): Result {
        Log.d(TAG, "Worker Start")
         // Overall Logic: Fetch -> Debounce -> Validate -> Update -> Complete
         // Get Current Widget Category Policy
         val widgetCategoryPolicy = appWidgetPreference.getWidgetCategoryPolicy()

        // If widgetCategory == null -> Set Widget State to Onboarding
        if (widgetCategoryPolicy is WidgetCategoryPolicy.Unconfigured)
            return Result.failure().also { Log.d(TAG, "No selected widget category found.") }

        return try {
            // Get Top Three Notices, based on the given policy
            val notices = fetchTopThreeNotice(widgetCategoryPolicy)

            Log.d(TAG, "Received: ${notices.toString()}")
            // Unable to fetch notices (receive null or empty)
            if (notices.isNullOrEmpty()) {
                // Do not perform update/Sync --> Retry with Backoff policy
                return if (runAttemptCount < 3) Result.retry() else Result.failure()
            }

            // Type Conversion: NoticeVO -> WidgetNoticeVO
            val widgetNotices = notices.map { vo ->
                WidgetNoticeVO(
                    id = vo.nttId,
                    title = vo.title,
                    info = "[${vo.departName}] ${vo.timestamp}",
                    contentUrl = vo.url
                )
            }

            // Update Widget State (by update cached notices)
            stateUpdater.updateNoticeWidgetState(WidgetState(category, widgetNotices))
            Result.success()
        } catch (e: Exception) {
            Log.d(TAG, "Exception Thrown: ${e.message}")
            // Exception Caught. Terminate Worker.
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    // Fetch Corresponding Data
    private suspend fun fetchTopThreeNotice(policy: WidgetCategoryPolicy): List<NoticeVO>? {
        when (policy) {
            is WidgetCategoryPolicy.Main -> {
                category = policy.categoryKey
                return remoteRepository.queryTopThreeNotices(policy.categoryKey)
            }
            is WidgetCategoryPolicy.Major -> {
                // Get current subscription status.
                val subscribedMajor = appSubscriptionPreference.getSubscribedMajor().first()

                return if (subscribedMajor.isNotEmpty()) {
                    category = subscribedMajor.first()
                    remoteRepository.queryTopThreeNotices(category)
                } else {
                    null
                }
            }
            else -> { return null }
        }
    }

    // Factory
    class Factory @Inject constructor(
        @ApplicationContext private val context: Context,
        private val appWidgetPreferences: AppWidgetPreferenceRepository,
        private val appSubscriptionPreference: AppSubscriptionPreferenceRepository,
        private val stateUpdater: WidgetStateUpdater,
        private val remoteRepository: NoticeRemoteRepository
    ) : IntermediateWorkerFactory {
        override fun create(params: WorkerParameters): ListenableWorker {
            return KnuticeWidgetSync(
                context,
                params,
                appWidgetPreferences,
                appSubscriptionPreference,
                stateUpdater,
                remoteRepository
            )
        }
    }
}