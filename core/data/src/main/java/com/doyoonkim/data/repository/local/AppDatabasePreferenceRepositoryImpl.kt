package com.doyoonkim.data.repository.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.doyoonkim.domain.interfaces.AppDatabasePreferenceRepository
import javax.inject.Inject

class AppDatabasePreferenceRepositoryImpl @Inject constructor(
    private val appPref: SharedPreferences
) : AppDatabasePreferenceRepository {

    companion object {
        // Database Sync
        private const val DB_SYNC_PARTIAL_FAIL = "DB_SYNC_PARTIAL_FAIL"

        private const val DB_SYNC_1_2_STATUS = "DB_SYNC_1_2_STATUS"
        private const val DB_SYNC_2_3_STATUS = "DB_SYNC_2_3_STATUS"
    }


    override fun isDatabaseSyncCompleted(): Boolean {
        // If sync 2_3 is required, return
        // appPref.getBoolean(DB_SYNC_1_2_STATUS, false) && appPref.getBoolean(DB_SYNC_2_3_STATUS, false)

        return with(appPref) {
            getBoolean(DB_SYNC_1_2_STATUS, false)
                    && getBoolean(DB_SYNC_2_3_STATUS, false)
        }
    }

    override fun isPartialFailedDuringDatabaseSync() = appPref.getBoolean(DB_SYNC_PARTIAL_FAIL, false)

    override fun setSyncStatus_1_2(status: Boolean) =
        appPref.edit { putBoolean(DB_SYNC_1_2_STATUS, status) }

    override fun setSyncStatus_2_3(status: Boolean) =
        appPref.edit { putBoolean(DB_SYNC_2_3_STATUS, status) }

    override fun setDatabaseSyncPartialFailedStatus(status: Boolean) =
        appPref.edit { putBoolean(DB_SYNC_PARTIAL_FAIL, status) }

}