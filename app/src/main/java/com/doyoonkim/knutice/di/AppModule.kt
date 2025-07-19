package com.doyoonkim.knutice.di

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import com.doyoonkim.common.di.ApplicationContext
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {

    // Inject ApplicationContext
    @Provides
    @Singleton
    @ApplicationContext
    fun providesApplicationContext(app: Application): Context = app.applicationContext

    @Provides
    @Singleton
    fun providesApplicationPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences("app_pref", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun providesAlarmManager(@ApplicationContext context: Context) : AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

}