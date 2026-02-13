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
import com.doyoonkim.model.NoticeCategory
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

    override suspend fun doWork(): Result {
        Log.d(TAG, "Worker Start")
         // Overall Logic: Fetch -> Debounce -> Validate -> Update -> Complete
         // Get Current Widget Category Configuration
         val widgetCategory = appPreferences.getWidgetCategory()

        // If widgetCategory == null -> Set Widget State to Onboarding
        if (widgetCategory.isNullOrEmpty()) return Result.failure().also { Log.d(TAG, "No selected widget category found.") }

        return try {
            // Get Top Three Notices.
            val notices = remoteRepository.queryTopThreeNotices(widgetCategory)

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
            updateWidgetState(widgetCategory, widgetNotices)
            Result.success()
        } catch (e: Exception) {
            Log.d(TAG, "Exception Thrown: ${e.message}")
            // Exception Caught. Terminate Worker.
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    // widget state update
    private suspend fun updateWidgetState(category: String, notices: List<WidgetNoticeVO>) {
        // Update Widget Status by updating local cache.
        // All instance of KnuticeWidget would be affected.
        val widgetManager = GlanceAppWidgetManager(appContext)

        // Retrieve all glance instance of KnuticeWidget.
        val glanceIds = widgetManager.getGlanceIds(KnuticeWidget::class.java)

        glanceIds.forEach { glanceId ->
            // Ensure Thread-safe write to Preference (DataStore)
            updateAppWidgetState(
                context = appContext,
                glanceId = glanceId
            ) { preference ->
                // Serialize to Json
                val stateJson = Json.encodeToString(
                    WidgetState(
                        category = category,
                        notices = notices,
                        lastUpdated = System.currentTimeMillis()
                    )
                )
                // Update state to Pref (Local Cache)
                preference[WidgetKey.PREF_STATE_KEY] = stateJson
            }
        }
        // Force Update Glance (Batch Update) (via Inter-Process Communication (IPC) call)
        KnuticeWidget().updateAll(appContext)
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