package com.doyoonkim.knutice.di.modules

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.work.WorkManager
import com.doyoonkim.model.di.ApplicationContext
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
        // Only one instance of the SharedPreferences object is returned to any callers for the same name
        context.applicationContext.getSharedPreferences("app_pref", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun providesAlarmManager(@ApplicationContext context: Context) : AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // WorkManager
    @Provides
    @Singleton
    fun providesWorkManager(@ApplicationContext context: Context) =
        WorkManager.getInstance(context)

}