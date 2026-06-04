package com.doyoonkim.data.repository.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.doyoonkim.domain.interfaces.AppSubscriptionPreferenceRepository
import javax.inject.Inject

class AppSubscriptionPreferenceRepositoryImpl @Inject constructor(
    private val appPref: SharedPreferences
) : AppSubscriptionPreferenceRepository {

    companion object {
        private const val TAG = "AppSubscriptionPreferenceRepositoryImpl"

        // Major Subscription Status
        private const val SUBSCRIBED_MAJOR = "SUBSCRIBED_MAJOR"
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

}