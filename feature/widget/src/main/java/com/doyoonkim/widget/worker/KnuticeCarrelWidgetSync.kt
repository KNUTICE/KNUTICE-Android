package com.doyoonkim.widget.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.doyoonkim.common.worker.IntermediateWorkerFactory
import com.doyoonkim.domain.interfaces.CarrelStatusRemoteRepository
import com.doyoonkim.model.CarrelRoomStatusVO
import com.doyoonkim.model.di.ApplicationContext
import com.doyoonkim.widget.carrel.KnuticeCarrelRoomStatusWidget
import com.doyoonkim.widget.model.CarrelWidgetState
import com.doyoonkim.widget.model.WidgetKey
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class KnuticeCarrelWidgetSync(
    private val appContext: Context,
    workerParam: WorkerParameters,
    private val repository: CarrelStatusRemoteRepository
): CoroutineWorker(appContext, workerParam) {
    companion object {
        private val TAG = "KnuticeCarrelWidgetSync"
        private val json = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }
    }

    override suspend fun doWork(): Result {
        // Fetch Current status
        Log.d(TAG, "Carrel Widget Worker Start")

        return repository.getCarrelRoomStatus()
            .fold(
                onSuccess = { status ->
                    Log.d(TAG, "${status.toString()}")
                    setWidgetLoading()
                    updateWidgetState(status)
                    Result.success()
                },
                onFailure = {reason ->
                    if (runAttemptCount < 3) Result.retry()
                    else Result.failure().also { Log.d(TAG, "Error: ${reason.stackTraceToString()}") }
                }
            )
    }

    private suspend fun setWidgetLoading() {
        val widgetManager = GlanceAppWidgetManager(appContext)
        val glanceIds = widgetManager.getGlanceIds(KnuticeCarrelRoomStatusWidget::class.java)

        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = appContext,
                glanceId = glanceId
            ) { preferences ->
                preferences[WidgetKey.CARREL_WIDGET_PREF_LOADING_STATE_KEY] = true
            }
        }
        // Widget Loading State update. (Tiny IPC communication)
        KnuticeCarrelRoomStatusWidget().updateAll(appContext)
    }

    private suspend fun updateWidgetState(status: List<CarrelRoomStatusVO>) {
        // Current Status
        val currentStatus = status.joinToString { "${it.name}:${it.occupied}" }

        // Widget manager
        val widgetManager = GlanceAppWidgetManager(appContext)
        // Retrieve all CarrelWidget instance
        val glanceId = widgetManager.getGlanceIds(KnuticeCarrelRoomStatusWidget::class.java)

        glanceId.forEach { glanceId ->
            // Ensure Thread-safe write to Preference (DataStore)
            updateAppWidgetState(
                context = appContext,
                glanceId = glanceId,
            ) { preferences ->
                // Set Loading State and Last Updated Mark
                val syncTimeStamp = System.currentTimeMillis()
                preferences[WidgetKey.CARREL_WIDGET_PREF_LOADING_STATE_KEY] = false
                preferences[WidgetKey.CARREL_WIDGET_PREF_LAST_SYNC_KEY] = syncTimeStamp

                // Get Cached Status
                val cachedStatus = preferences[WidgetKey.CARREL_WIDGET_PREF_CACHE_KEY]
                if (cachedStatus != currentStatus) {
                    Log.d(TAG, "Core data has been changed. Apply changes to Payload")
                    val carrelWidgetState = CarrelWidgetState(
                        status = status
                    )
                    // Update state to Pref
                    preferences[WidgetKey.CARREL_WIDGET_PREF_STATE_KEY] =
                        json.encodeToString(carrelWidgetState)
                    // Update Cached Value
                    preferences[WidgetKey.CARREL_WIDGET_PREF_CACHE_KEY] = currentStatus
                } else {
                    Log.d(TAG, "Core Data Identical. Only Metadata would be updated.")
                }
            }
        }

        // If only Metadata has changed, IPC communication would be very tiny (< 1KB)
        KnuticeCarrelRoomStatusWidget().updateAll(appContext)
    }

    // Factory
    class Factory @Inject constructor(
        @ApplicationContext private val context: Context,
        private val repository: CarrelStatusRemoteRepository
    ): IntermediateWorkerFactory {
        override fun create(params: WorkerParameters): ListenableWorker {
            return KnuticeCarrelWidgetSync(
                context, params, repository
            )
        }

    }

}