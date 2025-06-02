package com.doyoonkim.notification.local

import android.app.Notification
import android.app.NotificationManager

abstract class Notifier(
    private val notificationManager: NotificationManager
) {
    abstract val channelId: String
    abstract val channelName: String
    abstract val notificationId: Int

    fun showNotification(content: String) {
        // Use existing notification channel: KNUTICE-IN-APP-Channel
        val notification = buildNotification(content)
        notificationManager.notify(
            notificationId,
            notification
        )
    }

    abstract fun buildNotification(content: String): Notification
}