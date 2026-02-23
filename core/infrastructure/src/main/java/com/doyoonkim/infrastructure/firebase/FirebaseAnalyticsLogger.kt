package com.doyoonkim.infrastructure.firebase

import android.os.Bundle
import com.doyoonkim.common.analytics.AnalyticsLogger
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class FirebaseAnalyticsLogger(
    private val analytics: FirebaseAnalytics
) : AnalyticsLogger {

    override fun logEvent(event: String, param: Bundle?) {
        analytics.logEvent(event, param)
    }
}