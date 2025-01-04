package com.doyoonkim.knutice

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.doyoonkim.knutice.fcm.PushNotificationHandler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication() : Application() {
    @Inject lateinit var notificationHandler: PushNotificationHandler

    override fun onCreate() {
        super.onCreate()
        // Create channel group
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).run {
            createNotificationChannel(
                getString(R.string.inapp_notification_channel_id),
                getString(R.string.inapp_notificaiton_channel_name),
                getString(R.string.inapp_notification_channel_description)
            )
        }
        notificationHandler.requestCurrentToken()
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    private fun createNotificationChannel(id: String, name: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                id,
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