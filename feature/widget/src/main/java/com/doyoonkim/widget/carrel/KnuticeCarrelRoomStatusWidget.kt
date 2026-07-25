package com.doyoonkim.widget.carrel

import android.content.Context
import android.util.Log
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.doyoonkim.widget.carrel.components.CarrelWidgetContainer
import com.doyoonkim.widget.model.CarrelWidgetState
import com.doyoonkim.widget.model.WidgetKey
import com.doyoonkim.widget.notices.components.WidgetPlaceholder
import com.doyoonkim.widget.theme.KnuticeWidgetTheme
import kotlinx.serialization.json.Json

class KnuticeCarrelRoomStatusWidget : GlanceAppWidget() {
    private val TAG = "KnuticeCarrelRoomStatusWidget"

    // Get Preference Instance (DataStore)
    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            // Retrieve Widget State
            val preferences = currentState<Preferences>()
            val stateJson = preferences[WidgetKey.CARREL_WIDGET_PREF_STATE_KEY]

            // Deserialize
            val carrelWidgetState = stateJson?.let {
                // Potential Deserialization Error Handling
                try {
                    Json.decodeFromString<CarrelWidgetState>(it)
                } catch (e: Exception) {
                    // Set Default State (empty list)
                    CarrelWidgetState()
                }
            } ?: CarrelWidgetState()

            val isLoading = preferences[WidgetKey.CARREL_WIDGET_PREF_LOADING_STATE_KEY] ?: false
            val lastSync = preferences[WidgetKey.CARREL_WIDGET_PREF_LAST_SYNC_KEY] ?: System.currentTimeMillis()

            Log.d(TAG, "state: $carrelWidgetState, loading?: $isLoading, LastSync: $lastSync")

            GlanceTheme(colors = KnuticeWidgetTheme.colors) {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .cornerRadius(15.dp)
                        .background(GlanceTheme.colors.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (carrelWidgetState.status.isEmpty()) {
                        WidgetPlaceholder(
                            modifier = GlanceModifier.fillMaxSize(),
                            titleText = "열람실 현황을 가져올 수 없어요",
                            textColor = GlanceTheme.colors.primary,
                            containerColor = GlanceTheme.colors.primaryContainer
                        ) { }
                    } else {
                        CarrelWidgetContainer(
                            state = carrelWidgetState,
                            isLoading = isLoading,
                            lastSync = lastSync
                        )
                    }
                }
            }
        }
    }
}
