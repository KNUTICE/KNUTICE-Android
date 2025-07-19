package com.doyoonkim.knutice.di.util

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import com.doyoonkim.common.di.ApplicationContext
import com.doyoonkim.knutice.di.components.AppComponent

interface SystemServices {
    @ApplicationContext fun applicationContext(): Context
    fun sharedPreferences(): SharedPreferences
    fun alarmManager(): AlarmManager
    fun notificationManager(): NotificationManager
}

class DefaultSystemService(
    private val appComponent: AppComponent
) : SystemServices {

    @ApplicationContext
    override fun applicationContext(): Context = appComponent.applicationContext()

    override fun sharedPreferences(): SharedPreferences = appComponent.sharedPreference()

    override fun alarmManager(): AlarmManager = appComponent.alarmManager()

    override fun notificationManager(): NotificationManager = appComponent.notificationManager()

}