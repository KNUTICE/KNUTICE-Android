package com.doyoonkim.notification.local

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.doyoonkim.common.R
import kotlin.random.Random

class RunnerNotifier(
    private val notificationManager: NotificationManager,
    private val context: Context
) : Notifier(notificationManager) {
    override val channelId: String
        get() = context.getString(R.string.inapp_notification_channel_id)
    override val channelName: String
        get() = context.getString(R.string.inapp_notificaiton_channel_name)
    override val notificationId: Int
        get() = Random(System.currentTimeMillis()).nextInt()

    override fun buildNotification(content: String, uriString: String): Notification {
        // Create Intent
        val deeplinkIntent = Intent(
            Intent.ACTION_VIEW,
            uriString.toUri()
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }.run {
            PendingIntent.getActivity(
                context,
                0,
                this,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        // Custom-define notification builder.
        return NotificationCompat.Builder(context, channelId)
            .setContentTitle(context.getString(R.string.text_reminder_title))
            .setContentText(content)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(deeplinkIntent)
            .setAutoCancel(true)
            .build()
    }
}