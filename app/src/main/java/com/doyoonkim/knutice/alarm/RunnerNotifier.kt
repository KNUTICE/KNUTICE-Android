package com.doyoonkim.knutice.alarm

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.doyoonkim.knutice.R
import kotlin.random.Random

class RunnerNotifier(
    private val notificationManager: NotificationManager,
    private val context: Context,
) : Notifier(notificationManager) {
    override val channelId: String = context.getString(R.string.inapp_notification_channel_id)
    override val channelName: String = context.getString(R.string.inapp_notificaiton_channel_name)
    override val notificationId = Random(System.currentTimeMillis()).nextInt()

    override fun buildNotification(note: String): Notification {
        return NotificationCompat.Builder(context, channelId)
            .setContentTitle(context.getString(R.string.text_reminder_title))
            .setContentText(note)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }
}