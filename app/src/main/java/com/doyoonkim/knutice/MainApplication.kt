package com.doyoonkim.knutice

import android.app.Application
import com.doyoonkim.knutice.fcm.PushNotificationHandler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication() : Application() {
    @Inject lateinit var notificationHandler: PushNotificationHandler

    override fun onCreate() {
        super.onCreate()
        notificationHandler.requestCurrentToken()
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}