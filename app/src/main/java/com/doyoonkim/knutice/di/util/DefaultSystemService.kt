package com.doyoonkim.knutice.di.util

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.work.WorkManager
import com.doyoonkim.common.analytics.AnalyticsLogger
import com.doyoonkim.domain.interfaces.AppDatabasePreferenceRepository
import com.doyoonkim.domain.interfaces.AppSubscriptionPreferenceRepository
import com.doyoonkim.domain.interfaces.AppTokenPreferenceRepository
import com.doyoonkim.domain.interfaces.AppWidgetPreferenceRepository
import com.doyoonkim.domain.interfaces.BookmarkLocalRepository
import com.doyoonkim.domain.interfaces.LocalWidgetCacheRepository
import com.doyoonkim.domain.interfaces.NoticeLocalRepository
import com.doyoonkim.model.di.ApplicationContext
import com.doyoonkim.domain.interfaces.abtest.FirebaseRemoteConfigRepository
import com.doyoonkim.model.di.DefaultDispatcher
import com.doyoonkim.model.di.IoDispatcher
import com.doyoonkim.network.retrofit.KnuticeService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

interface SystemServices {
    // Application Context
    @ApplicationContext fun applicationContext(): Context
    // SharedPreference (AppPreference)
    fun sharedPreferences(): SharedPreferences
    // AlarmManager
    fun alarmManager(): AlarmManager
    // Notification Manager
    fun notificationManager(): NotificationManager
    // WorkManager
    fun workManager(): WorkManager

    // Dispatchers
    @IoDispatcher fun ioDispatcher(): CoroutineDispatcher
    @DefaultDispatcher fun defaultDispatcher(): CoroutineDispatcher

    // Application Scope Coroutine
    fun applicationScope(): CoroutineScope
}

interface NetworkProvider {
    fun knuticeService(): KnuticeService
}

interface LocalStorageProvider {
    fun localNoticeRepository(): NoticeLocalRepository
    fun localBookmarkRepository(): BookmarkLocalRepository
}

interface LocalCacheProvider {
    fun localWidgetCacheRepository(): LocalWidgetCacheRepository
}

interface LocalPreferenceProvider {
    fun localAppDatabasePreferenceRepository(): AppDatabasePreferenceRepository
    fun localAppSubscriptionPreferenceRepository(): AppSubscriptionPreferenceRepository
    fun localAppTokenPreferenceRepository(): AppTokenPreferenceRepository
    fun localAppWidgetPreferenceRepository(): AppWidgetPreferenceRepository
}

interface FirebaseInfrastructureProvider {
    fun analyticsLogger(): AnalyticsLogger
    fun remoteConfigRepository(): FirebaseRemoteConfigRepository
}