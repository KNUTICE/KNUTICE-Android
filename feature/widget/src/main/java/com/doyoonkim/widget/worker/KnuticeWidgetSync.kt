package com.doyoonkim.widget.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.doyoonkim.common.di.AppPreferences
import com.doyoonkim.common.di.ApplicationContext
import com.doyoonkim.common.worker.IntermediateWorkerFactory
import com.doyoonkim.domain.interfaces.NoticeRemoteRepository
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.model.WidgetCategoryPolicy
import com.doyoonkim.widget.model.WidgetKey
import com.doyoonkim.widget.model.WidgetNoticeVO
import com.doyoonkim.widget.model.WidgetState
import com.doyoonkim.widget.notices.KnuticeWidget
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class KnuticeWidgetSync(
    private val appContext: Context,
    workerParam: WorkerParameters,
    private val appPreferences: AppPreferences,
    private val remoteRepository: NoticeRemoteRepository
) : CoroutineWorker(appContext, workerParam) {
    private val TAG = "KnuticeWidgetSync"
    private lateinit var category: String

    override suspend fun doWork(): Result {
        Log.d(TAG, "Worker Start")
         // Overall Logic: Fetch -> Debounce -> Validate -> Update -> Complete
         // Get Current Widget Category Policy
         val widgetCategoryPolicy = appPreferences.getWidgetCategoryPolicy()

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
            updateWidgetState(category, widgetNotices)
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
                val subscribedMajor = appPreferences.getSubscribedMajor() ?: return null
                category = subscribedMajor
                return remoteRepository.queryTopThreeNotices(subscribedMajor)
            }
            else -> { return null }
        }
    }

    // widget state update
    private suspend fun updateWidgetState(category: String, notices: List<WidgetNoticeVO>) {
        // Update Widget Status by updating local cache.
        // All instance of KnuticeWidget would be affected.
        val widgetManager = GlanceAppWidgetManager(appContext)

        // Retrieve all glance instance of KnuticeWidget.
        val glanceIds = widgetManager.getGlanceIds(KnuticeWidget::class.java)

        // State Serialization. Serialize it to Json.
        val currentState = WidgetState(category = category, notices = notices)

        // Flag Variable
        var isStateChanged = false

        glanceIds.forEach { glanceId ->
            // Ensure Thread-safe write to Preference (DataStore)
            updateAppWidgetState(
                context = appContext,
                glanceId = glanceId
            ) { preference ->

                // Retrieve current state saved in DataStore. (Defensive Json Parsing)
                val previousState = try {
                    preference[WidgetKey.NOTICE_WIDGET_PREF_STATE_KEY]?.run {
                        Json.decodeFromString<WidgetState>(this)
                            .copy(lastUpdated = 0L)
                    }
                } catch (e: Exception) {
                    // Prevent potential exception when deserialize saved Json after the potential
                    // structural changes of WidgetState
                    null
                }
                // If there's no state change, terminate function and don't update widget.
                if (previousState == currentState) return@updateAppWidgetState

                val stateJson = Json.encodeToString(
                    currentState.copy(lastUpdated = System.currentTimeMillis())
                )

                // Update state to Pref (Local Cache)
                preference[WidgetKey.NOTICE_WIDGET_PREF_STATE_KEY] = stateJson
                isStateChanged = true
            }
        }

        if (isStateChanged) {
            // Force Update Glance (Batch Update) (via Inter-Process Communication (IPC) call)
            KnuticeWidget().updateAll(appContext)
        } else {
            Log.d("KnuticeWidgetSync", "No state changes. Update Widget suppressed")
        }
    }

    // Factory
    class Factory @Inject constructor(
        @ApplicationContext private val context: Context,
        private val appPreferences: AppPreferences,
        private val remoteRepository: NoticeRemoteRepository
    ) : IntermediateWorkerFactory {
        override fun create(params: WorkerParameters): ListenableWorker {
            return KnuticeWidgetSync(
                context,
                params,
                appPreferences,
                remoteRepository
            )
        }
    }
}