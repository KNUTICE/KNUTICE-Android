package com.doyoonkim.widget.model

import androidx.datastore.preferences.core.stringPreferencesKey

object WidgetKey {
    // Key to access Widget's Preference (DataStore)
    val PREF_STATE_KEY = stringPreferencesKey("PREF_STATE_KEY")
}