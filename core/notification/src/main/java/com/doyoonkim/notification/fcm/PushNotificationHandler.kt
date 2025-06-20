package com.doyoonkim.notification.fcm

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.doyoonkim.common.BitmapHandler
import com.doyoonkim.common.R
import com.doyoonkim.domain.ImageRepository
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import kotlin.random.Random


class PushNotificationHandler @Inject constructor(
    private val imageRepository: ImageRepository,
    private val bitMapHandler: BitmapHandler,
    private val context: Context
) {
    private val TAG = "PushNotificationHandler"

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun handleReceivedMessage(message: RemoteMessage) {
        // When the app is in background or killed, Data Payload would be delivered once the user
        // clicks the system tray.
        Log.d(TAG, "Message data payload: ${message.notification}")

        if (message.data.isNotEmpty()) {
            Log.d(TAG, "Message Data Payload: ${message.data}")     // message.data: Map<String!, String!>

            message.toPushNotification()


            // Apply "Do not disturb" option. (Temporarily save the message and deliver after the core time is end.
            // Use Local Database (Room?)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun RemoteMessage.toPushNotification() {

        // Create Pending Intent (For access push notification while the app is in foreground)
        // TODO: Migrate to Deep Link.
        val nttId = this@toPushNotification.data["nttId"]
        val url = this@toPushNotification.data["contentUrl"]
        val imageUrl = this@toPushNotification.data["contentImage"]
        val fabVisible = true

        // Deeplink featured by Jetpack Navigation won't work because notification payload consumes custom-defined deeplink intent using ACTION_VIEW
        val deeplinkIntent = Intent(
            Intent.ACTION_VIEW,
            "knutice://service/noticeDetail/$nttId/${Uri.encode(url)}/$fabVisible".toUri()
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            deeplinkIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationId = Random(System.currentTimeMillis().toInt()).nextInt()
        // Utilize channel already created by FCM as default
        val notificationBuilder = NotificationCompat.Builder(
            context, context.getString(R.string.inapp_notification_channel_id)
        ).apply {
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle(context.getString(R.string.new_notice))
            setContentText(this@toPushNotification.data["contentTitle"] ?: "No message body.")
            setContentIntent(pendingIntent)
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
            setAutoCancel(true)
        }


        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
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

            // HardCoded CoroutineScope for Testing
            val completionMarker = Job()     // Variable for manual Cancellation
            imageUrl?.let { url ->
                CoroutineScope(Dispatchers.IO + completionMarker).launch {
                    val bitmapImage = async {
                        runCatching {
                            withTimeout(5000L) {
                                imageRepository.getImageByteArrayFromUrl(url)?.let { b ->
                                    bitMapHandler.decodeByteArray(b)
                                }
                            }
                        }.onFailure {
                            Log.d(TAG, "Unable to retrieve bitmap image\nREASON: ${it.stackTrace}")
                        }
                    }

                    bitmapImage.await().fold(
                        onSuccess = { result ->
                            result?.let {
                                notificationBuilder.apply {
                                    setStyle(
                                        NotificationCompat.BigPictureStyle()
                                            .bigPicture(it)
                                    )
                                }
                            }
                        },
                        onFailure = {
                            Log.d(TAG, "No Bitmap Image to be added\nREASON: ${it.stackTrace}")
                        }
                    ).also {
                        notify(notificationId, notificationBuilder.build())
                        completionMarker.complete()
                    }
                }
            }
        }
    }
}