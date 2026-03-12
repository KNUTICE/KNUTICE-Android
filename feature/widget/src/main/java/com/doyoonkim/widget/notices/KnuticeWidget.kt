package com.doyoonkim.widget.notices

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.fillMaxSize
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.doyoonkim.widget.model.WidgetKey
import com.doyoonkim.widget.model.WidgetState
import com.doyoonkim.widget.theme.KnuticeWidgetTheme
import kotlinx.serialization.json.Json

class KnuticeWidget : GlanceAppWidget() {
    private val TAG = "KnuticeWidget"

    // Get Preference Instance (DataStore)
    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            // UiState
            val preference = currentState<Preferences>()
            val stateJson = preference[WidgetKey.NOTICE_WIDGET_PREF_STATE_KEY]
            val lastUpdated = preference[WidgetKey.NOTICE_WIDGET_LAST_SYNC_KEY] ?: System.currentTimeMillis()

            // Retrieve Saved State from the preference (Local Cache/DataStore)
            val widgetState = stateJson?.let {
                try {
                    // Deserialize
                    Json.Default.decodeFromString<WidgetState>(it)
                } catch (e: Exception) {
                    // Exception --> Empty State
                    WidgetState()
                }
            } ?: WidgetState()

            // Render UI with widgetState.
            GlanceTheme(colors = KnuticeWidgetTheme.colors) {
                NoticeWidgetContents(
                    modifier = GlanceModifier.fillMaxSize(),
                    lastUpdated = lastUpdated,
                    state = widgetState
                )
            }
        }
    }
}