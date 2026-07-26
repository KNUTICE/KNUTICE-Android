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
import com.doyoonkim.model.di.ApplicationContext
import com.doyoonkim.widget.carrel.KnuticeCarrelRoomStatusWidget
import com.doyoonkim.widget.model.CarrelWidgetState
import com.doyoonkim.widget.model.WidgetKey
import com.doyoonkim.widget.util.WidgetStateUpdater
import javax.inject.Inject

class KnuticeCarrelWidgetSync(
    private val appContext: Context,
    workerParam: WorkerParameters,
    private val stateUpdater: WidgetStateUpdater,
    private val repository: CarrelStatusRemoteRepository
) : CoroutineWorker(appContext, workerParam) {
    companion object {
        private const val TAG = "KnuticeCarrelWidgetSync"
    }

    override suspend fun doWork(): Result {
        // Fetch Current status
        Log.d(TAG, "Carrel Widget Worker Start")
        setWidgetLoading() // Set Widget in Loading State.

        return repository.getCarrelRoomStatus()
            .fold(
                onSuccess = { status ->
                    Log.d(TAG, "$status")
                    stateUpdater.updateCarrelWidgetState(CarrelWidgetState(status))
                    Result.success()
                },
                onFailure = { reason ->
                    if (runAttemptCount < 3) {
                        Result.retry()
                    } else {
                        Result.failure().also { Log.d(TAG, "Error: ${reason.stackTraceToString()}") }
                    }
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

    // Factory
    class Factory @Inject constructor(
        @ApplicationContext private val context: Context,
        private val stateUpdater: WidgetStateUpdater,
        private val repository: CarrelStatusRemoteRepository
    ) : IntermediateWorkerFactory {
        override fun create(params: WorkerParameters): ListenableWorker {
            return KnuticeCarrelWidgetSync(
                context,
                params,
                stateUpdater,
                repository
            )
        }
    }
}
