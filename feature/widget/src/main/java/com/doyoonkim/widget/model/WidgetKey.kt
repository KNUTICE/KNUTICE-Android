package com.doyoonkim.widget.model

import androidx.datastore.preferences.core.stringPreferencesKey

object WidgetKey {
    // Key to access Widget's Preference (DataStore)
    val NOTICE_WIDGET_PREF_STATE_KEY = stringPreferencesKey("NOTICE_WIDGET_PREF_STATE_KEY")
}