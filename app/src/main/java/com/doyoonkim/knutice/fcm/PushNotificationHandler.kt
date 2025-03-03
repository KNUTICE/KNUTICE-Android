package com.doyoonkim.knutice.fcm

import android.Manifest
import android.app.Notification
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.doyoonkim.knutice.data.KnuticeRemoteSource
import com.doyoonkim.knutice.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

class PushNotificationHandler @Inject constructor() : FirebaseMessagingService() {
    @Inject lateinit var remoteSource: KnuticeRemoteSource
    private val TAG = "PushNotificationHandler"

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // POST request to send FCM Token to the Server.
        Log.d(TAG, "Received Token: ${token.toString()}")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "Message data payload: ${message.notification}")

        if (message.data.isNotEmpty()) {
            Log.d(TAG, "Message Data Payload: ${message.data}")
        }

        message.notification?.let {
            Log.d(TAG, "Body: ${it.body}")
            message.toPushNotification()
        }
    }

    private fun RemoteMessage.toPushNotification() {
        val notificationId = Random(System.currentTimeMillis().toInt()).nextInt()
        // Utilize channel already created by FCM as default
        val notificationBuilder = NotificationCompat.Builder(
            applicationContext, getString(R.string.inapp_notification_channel_id)
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(Icon.createWithResource(applicationContext, R.mipmap.ic_launcher))
            .setContentTitle(getString(R.string.new_notice))
            .setContentText(this@toPushNotification.notification?.body ?: "No message body")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("NotificationHandler", "Permission Denied")
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(notificationId, notificationBuilder.build())
        }
    }

    fun requestCurrentToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d(TAG, "Incomplete task: ${task.exception}")
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val registrationToken = task.result
            Log.d(TAG, "Received Token: $registrationToken")

            // POST request to upload current token to the web server.
            CoroutineScope(Dispatchers.IO).launch {
                remoteSource.validateToken(registrationToken)
            }
        })
    }

}