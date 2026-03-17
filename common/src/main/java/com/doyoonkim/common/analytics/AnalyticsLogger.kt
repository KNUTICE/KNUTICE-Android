package com.doyoonkim.common.analytics

import android.os.Bundle


interface AnalyticsLogger {
    fun logEvent(event: String, param: Bundle? = null)
}