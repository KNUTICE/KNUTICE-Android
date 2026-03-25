package com.doyoonkim.data.repository.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.doyoonkim.domain.interfaces.AppPreferenceRepository
import com.doyoonkim.model.WidgetCategoryPolicy
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AppPreferenceRepositoryImpl @Inject constructor(
    private val appPref: SharedPreferences
) : AppPreferenceRepository {
    private val DB_SYNC_STATUS = "DB_SYNC_STATUS"
    private val DB_SYNC_PARTIAL_FAIL = "DB_SYNC_PARTIAL_FAIL"

    private val DB_SYNC_1_2_STATUS = "DB_SYNC_1_2_STATUS"
    private val DB_SYNC_2_3_STATUS = "DB_SYNC_2_3_STATUS"

    // Token Caching
    private val DEVICE_TOKEN = "DEVICE_TOKEN"

    // Major Subscription Status
    private val SUBSCRIBED_MAJOR = "SUBSCRIBED_MAJOR"

    // Widget Configuration (Category Selection)
    private val WIDGET_CATEGORY = "WIDGET_CATEGORY"

    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        classDiscriminator = "type"
    }

    /**
     * updateDeviceToken
     * @param token: Unique FCM token issued to this device.
     */
    override fun updateDeviceToken(token: String) {
        appPref.edit { putString(DEVICE_TOKEN, token) }
    }

    override fun getCachedToken(): String? {
        return appPref.getString(DEVICE_TOKEN, null)
    }

    /**
     * Major Subscription Status
     */
    override fun getSubscribedMajor(): String? {
        return appPref.getString(SUBSCRIBED_MAJOR, null)
    }

    override fun updateSubscribedMajor(newMajor: String) {
        appPref.edit { putString(SUBSCRIBED_MAJOR, newMajor) }
    }

    /**
     * Widget Configuration
     */
    override fun getWidgetCategoryPolicy(): WidgetCategoryPolicy {
        val cachedPolicy = appPref.getString(WIDGET_CATEGORY, null)
            ?: return WidgetCategoryPolicy.Unconfigured

        return json.decodeFromString<WidgetCategoryPolicy>(cachedPolicy)
    }

    override fun updateWidgetCategoryPolicy(policy: WidgetCategoryPolicy) {
        val serializedString = json.encodeToString<WidgetCategoryPolicy>(policy)
        appPref.edit { putString(WIDGET_CATEGORY, serializedString) }
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