package com.doyoonkim.common.di

import android.content.SharedPreferences
import javax.inject.Inject
import androidx.core.content.edit

class AppPreferences @Inject constructor(
    private val appPref: SharedPreferences
) {
    private val DB_SYNC_STATUS = "DB_SYNC_STATUS"
    private val DB_SYNC_PARTIAL_FAIL = "DB_SYNC_PARTIAL_FAIL"

    fun isDatabaseSyncCompleted() = appPref.getBoolean(DB_SYNC_STATUS, false)

    fun isPartialFailedDuringDatabaseSync() = appPref.getBoolean(DB_SYNC_PARTIAL_FAIL, false)

    fun setDatabaseSyncStatus(status: Boolean) =
        appPref.edit { putBoolean(DB_SYNC_STATUS, status) }

    fun setDatabaseSyncPartialFailedStatus(status: Boolean) =
        appPref.edit { putBoolean(DB_SYNC_PARTIAL_FAIL, status) }

}