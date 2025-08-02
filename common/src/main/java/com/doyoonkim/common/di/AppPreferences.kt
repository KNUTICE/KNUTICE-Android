package com.doyoonkim.common.di

import android.content.SharedPreferences
import javax.inject.Inject
import androidx.core.content.edit

class AppPreferences @Inject constructor(
    private val appPref: SharedPreferences
) {
    private val DB_SYNC_STATUS = "DB_SYNC_STATUS"
    private val DB_SYNC_PARTIAL_FAIL = "DB_SYNC_PARTIAL_FAIL"

    private val DB_SYNC_1_2_STATUS = "DB_SYNC_1_2_STATUS"
    private val DB_SYNC_2_3_STATUS = "DB_SYNC_2_3_STATUS"

    fun isDatabaseSyncCompleted(): Boolean {
        // If sync 2_3 is required, return
        // appPref.getBoolean(DB_SYNC_1_2_STATUS, false) && appPref.getBoolean(DB_SYNC_2_3_STATUS, false)

        return with(appPref) {
            getBoolean(DB_SYNC_1_2_STATUS, false)
                    && getBoolean(DB_SYNC_2_3_STATUS, false)
        }
    }

    fun isPartialFailedDuringDatabaseSync() = appPref.getBoolean(DB_SYNC_PARTIAL_FAIL, false)

    fun setDatabaseSyncStatus(status: Boolean) =
        appPref.edit { putBoolean(DB_SYNC_STATUS, status) }

    fun setSyncStatus_1_2(status: Boolean) =
        appPref.edit { putBoolean(DB_SYNC_1_2_STATUS, status) }

    fun setSyncStatus_2_3(status: Boolean) =
        appPref.edit { putBoolean(DB_SYNC_2_3_STATUS, status) }

    fun setDatabaseSyncPartialFailedStatus(status: Boolean) =
        appPref.edit { putBoolean(DB_SYNC_PARTIAL_FAIL, status) }

}