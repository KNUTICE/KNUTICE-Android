package com.doyoonkim.knutice.alarm

import android.app.Notification
import android.app.NotificationManager
import android.os.Build
import com.doyoonkim.knutice.model.Bookmark

abstract class Notifier (
    private val notificationManager: NotificationManager
) {
    abstract val channelId: String
    abstract val channelName: String
    abstract val notificationId: Int

    fun showNotification(note: String) {
        // Use existing notification channel
        val notification = buildNotification(note)
        notificationManager.notify(
            notificationId,
            notification
        )
    }

    abstract fun buildNotification(note: String): Notification
}