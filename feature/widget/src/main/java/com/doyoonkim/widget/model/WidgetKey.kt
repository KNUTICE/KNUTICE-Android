package com.doyoonkim.widget.model

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object WidgetKey {
    // Key to access Widget's Preference (DataStore)
    val NOTICE_WIDGET_PREF_STATE_KEY = stringPreferencesKey("NOTICE_WIDGET_PREF_STATE_KEY")
    val CARREL_WIDGET_PREF_STATE_KEY = stringPreferencesKey("CARREL_WIDGET_PREF_STATE_KEY")
    val CARREL_WIDGET_PREF_LAST_SYNC_KEY = longPreferencesKey("CARREL_WIDGET_PREF_LAST_SYNC_KEY")
    val CARREL_WIDGET_PREF_LOADING_STATE_KEY = booleanPreferencesKey("CARREL_WIDGET_PREF_LOADING_STATE_KEY")
    val CARREL_WIDGET_PREF_CACHE_KEY = stringPreferencesKey("CARREL_WIDGET_PREF_CACHE_KEY")
}