package com.doyoonkim.notification.fcm

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.doyoonkim.common.BitmapHandler
import com.doyoonkim.common.R
import com.doyoonkim.domain.interfaces.ImageRemoteRepository
import com.doyoonkim.domain.interfaces.NoticeRemoteRepository
import com.doyoonkim.model.NoticeCategory
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import kotlin.random.Random

class PushNotificationHandler @Inject constructor(
    private val remoteRepository: NoticeRemoteRepository,
    private val imageRepository: ImageRemoteRepository,
    private val bitMapHandler: BitmapHandler,
    private val ioDispatcher: CoroutineDispatcher,
    private val context: Context
) {
    private val TAG = "PushNotificationHandler"

    // HardCoded CoroutineScope for Testing
    private val job = SupervisorJob()     // Variable for manual Cancellation
    private val coroutineScope = CoroutineScope(ioDispatcher + job)

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun handleReceivedMessage(message: RemoteMessage) {
        // When the app is in background or killed, Data Payload would be delivered once the user
        // clicks the system tray.
        Log.d(TAG, "Notification payload: ${message.notification}")
        Log.d(TAG, "Data payload: ${message.data}")

        message.toPushNotification()
    }

    fun inactivateCoroutineScope() {
        job.cancel()
        Log.d(TAG, "Coroutine Active Status: ${coroutineScope.isActive}")
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun RemoteMessage.toPushNotification() {
        val notification = this@toPushNotification.notification
        val data = this@toPushNotification.data

        val notificationId = Random(System.currentTimeMillis().toInt()).nextInt()
        // Utilize channel already created by FCM as default
        val notificationBuilder = NotificationCompat.Builder(
            context, context.getString(R.string.inapp_notification_channel_id)
        ).apply {
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle(notification?.title ?: context.getString(R.string.new_notice))
            setContentText(notification?.body ?: context.getString(R.string.text_push_to_notice))
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
            setAutoCancel(true)
        }

        // Create Pending Intent (For access push notification while the app is in foreground)
        val nttId = data["nttId"]
        val url = data["contentUrl"]
        val fabVisible = true

        val uri = if (nttId != null && url != null) {
            "knutice://service/noticeDetail/$nttId/${Uri.encode(url)}/$fabVisible".toUri()
        } else {
            "".toUri()
        }

        // Deeplink featured by Jetpack Navigation won't work because notification payload consumes custom-defined deeplink intent using ACTION_VIEW
        val deeplinkIntent = Intent(
            Intent.ACTION_VIEW,
            uri
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            deeplinkIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        notificationBuilder.apply {
            setContentIntent(pendingIntent)
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

            coroutineScope.launch {
                Log.d(TAG, "START FETCHING IMAGE")
                notification?.imageUrl?.let { uri ->
                    val bitmapImage = async {
                        runCatching {
                            withTimeout(5000L) {
                                imageRepository.getImageByteArrayFromUrl(uri.toString())?.let { b ->
                                    bitMapHandler.decodeByteArray(b)
                                }
                            }
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
                            Log.d(TAG, "Unable to receive image.\n" +
                                    "REASON: ${it.stackTrace}")
                        }
                    )
                }
            }.invokeOnCompletion { notify(notificationId, notificationBuilder.build()) }

        }
    }

    private fun localizedTitle(noticeCategory: String): String {
        return with(context) {
            when (noticeCategory) {
                NoticeCategory.GENERAL_NEWS.name -> getString(R.string.general_news)
                NoticeCategory.ACADEMIC_NEWS.name -> getString(R.string.academic_news)
                NoticeCategory.SCHOLARSHIP_NEWS.name -> getString(R.string.scholarship_news)
                NoticeCategory.EVENT_NEWS.name -> getString(R.string.event_news)
                NoticeCategory.EMPLOYMENT_NEWS.name -> getString(R.string.employment_news)
                else -> null
            }?.let {
                "${getString(R.string.push_title_new)} " +
                        "$it ${getString(R.string.push_title_arrived)}"
            } ?: "${getString(R.string.push_title_new)} ${getString(R.string.push_title_arrived)}"
        }
    }
}