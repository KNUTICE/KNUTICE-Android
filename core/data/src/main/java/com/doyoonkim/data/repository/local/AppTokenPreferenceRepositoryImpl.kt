package com.doyoonkim.data.repository.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.doyoonkim.domain.interfaces.AppTokenPreferenceRepository
import javax.inject.Inject

class AppTokenPreferenceRepositoryImpl @Inject constructor(
    private val appPref: SharedPreferences
) : AppTokenPreferenceRepository {

    companion object {
        private const val TAG = "AppTokenPreferenceRepositoryImpl"

        // Token Caching
        private const val DEVICE_TOKEN = "DEVICE_TOKEN"
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
}
