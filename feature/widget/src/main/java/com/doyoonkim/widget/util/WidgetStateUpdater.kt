package com.doyoonkim.widget.util

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import com.doyoonkim.model.di.ApplicationContext
import com.doyoonkim.widget.carrel.KnuticeCarrelRoomStatusWidget
import com.doyoonkim.widget.model.CarrelWidgetState
import com.doyoonkim.widget.model.WidgetKey
import com.doyoonkim.widget.model.WidgetState
import com.doyoonkim.widget.notices.KnuticeWidget
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WidgetStateUpdater @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val TAG = "WidgetStateUpdater"

        private val json = Json {
            encodeDefaults = false
            ignoreUnknownKeys = true
        }
    }

    suspend fun updateNoticeWidgetState(state: WidgetState) {
        // Update Widget Status by updating local cache.
        // All instance of KnuticeWidget would be affected.
        val widgetManager = GlanceAppWidgetManager(context)

        // Retrieve all glance instance of KnuticeWidget.
        val glanceIds = widgetManager.getGlanceIds(KnuticeWidget::class.java)

        // Process heavy IO task asynchronously.
        coroutineScope {
            glanceIds.map { glanceId ->
                // Ensure Thread-safe write to Preference (DataStore)
                async(Dispatchers.IO) {
                    updateAppWidgetState(
                        context = context,
                        glanceId = glanceId
                    ) { preference ->

                        val syncTimestamp = System.currentTimeMillis()
                        preference[WidgetKey.NOTICE_WIDGET_LAST_SYNC_KEY] = syncTimestamp

                        // Retrieve current state saved in DataStore. (Defensive Json Parsing)
                        val previousState = try {
                            preference[WidgetKey.NOTICE_WIDGET_PREF_STATE_KEY]?.run {
                                json.decodeFromString<WidgetState>(this)
                                    .copy(lastUpdated = 0L)
                            }
                        } catch (e: Exception) {
                            // Properly propagate potential CancellationException under Coroutine Lifecycle.
                            if (e is CancellationException) throw e
                            // Prevent potential exception when deserialize saved Json after the potential
                            // structural changes of WidgetState
                            null
                        }

                        if (previousState != state) {
                            Log.d(TAG, "Core data has been changed. Apply changes to payload.")
                            val stateJson = Json.encodeToString(
                                state.copy(lastUpdated = System.currentTimeMillis())
                            )
                            // Update state to Pref (Local Cache)
                            preference[WidgetKey.NOTICE_WIDGET_PREF_STATE_KEY] = stateJson
                        } else {
                            Log.d(TAG, "Core Data Identical. Update Metadata only")
                        }
                    }
                }
            }.awaitAll()
        }

        // Force Update Glance (Batch Update) (via Inter-Process Communication (IPC) call)
        KnuticeWidget().updateAll(context)
    }

    suspend fun updateCarrelWidgetState(state: CarrelWidgetState) {
        // Current Status
        val currentStatus = state.status.joinToString { "${it.name}:${it.occupied}" }

        // Widget manager
        val widgetManager = GlanceAppWidgetManager(context)
        // Retrieve all CarrelWidget instance
        val glanceId = widgetManager.getGlanceIds(KnuticeCarrelRoomStatusWidget::class.java)

        // Process heavy IO task asynchronously.
        coroutineScope {
            glanceId.map { glanceId ->
                // Ensure Thread-safe write to Preference (DataStore)
                async(Dispatchers.IO) {
                    updateAppWidgetState(
                        context = context,
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
                            // Update state to Pref
                            preferences[WidgetKey.CARREL_WIDGET_PREF_STATE_KEY] =
                                json.encodeToString(state)
                            // Update Cached Value
                            preferences[WidgetKey.CARREL_WIDGET_PREF_CACHE_KEY] = currentStatus
                        } else {
                            Log.d(TAG, "Core Data Identical. Only Metadata would be updated.")
                        }
                    }
                }
            }.awaitAll()
        }

        // Force Update Glance (Batch Update) (via Inter-Process Communication (IPC) call)
        KnuticeCarrelRoomStatusWidget().updateAll(context)
    }
}