package com.doyoonkim.knutice

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.getSystemService
import com.doyoonkim.knutice.fcm.PushNotificationHandler
import com.example.knutice.R
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication() : Application() {
    @Inject lateinit var notificationHandler: PushNotificationHandler

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        notificationHandler.requestCurrentToken()
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.inapp_notificaiton_channel_name)
            val description = getString(R.string.inapp_notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                getString(R.string.inapp_notification_channel_id),
                name,
                importance
            ).apply {
                this.description = description
            }

            // Register Custom-defined notification channel
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}