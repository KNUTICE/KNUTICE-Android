package com.doyoonkim.knutice.di

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import com.doyoonkim.common.di.ApplicationContext
import com.doyoonkim.common.di.ApplicationScope
import com.doyoonkim.knutice.di.components.MainActivityComponent
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {

    // Inject ApplicationContext
    @Provides
    @ApplicationContext
    fun providesApplicationContext(app: Application): Context = app.applicationContext

    @Provides
    fun providesApplicationPreferences(@ApplicationContext context: Context): SharedPreferences =
        // Only one instance of the SharedPreferences object is returned to any callers for the same name
        context.applicationContext.getSharedPreferences("app_pref", Context.MODE_PRIVATE)

    @Provides
    fun providesAlarmManager(@ApplicationContext context: Context) : AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

}