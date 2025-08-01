package com.doyoonkim.knutice.di.modules

import android.content.Context
import com.doyoonkim.common.di.ApplicationContext
import com.doyoonkim.knutice.analytics.AnalyticsLogger
import com.doyoonkim.knutice.analytics.FirebaseAnalyticsLogger
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class FirebaseAnalyticsModule {

    companion object {
        @Provides
        fun providesAnalyticsInstance(@ApplicationContext context: Context) = FirebaseAnalytics.getInstance(context)
    }

    @Binds
    abstract fun bindsFirebaseAnalyticsLogger(
        impl: FirebaseAnalyticsLogger
    ): AnalyticsLogger

}