package com.doyoonkim.infrastructure.di

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.doyoonkim.common.analytics.AnalyticsLogger
import com.doyoonkim.infrastructure.firebase.FirebaseAnalyticsLogger
import com.doyoonkim.model.di.ApplicationContext
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object FirebaseAnalyticsModule {

    // Provides Logger
    @RequiresPermission(allOf = [Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WAKE_LOCK])
    @Provides
    @Singleton
    fun providesAnalyticsLogger(@ApplicationContext context: Context): AnalyticsLogger {
        val analytics = FirebaseAnalytics.getInstance(context)
        return FirebaseAnalyticsLogger(analytics)
    }
}
