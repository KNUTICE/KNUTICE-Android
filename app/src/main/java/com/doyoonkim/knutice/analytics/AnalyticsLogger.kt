package com.doyoonkim.knutice.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

interface AnalyticsLogger {
    fun logEvent(event: String, param: Bundle? = null)
}

class FirebaseAnalyticsLogger @Inject constructor(
    private val analytics: FirebaseAnalytics
) : AnalyticsLogger {

    override fun logEvent(event: String, param: Bundle?) {
        analytics.logEvent(event, param)
    }

}