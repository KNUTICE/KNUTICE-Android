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
import com.doyoonkim.domain.ImageRepository
import com.doyoonkim.domain.RemoteRepository
import com.doyoonkim.model.NoticeCategory
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import kotlin.random.Random

class PushNotificationHandler @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val imageRepository: ImageRepository,
    private val bitMapHandler: BitmapHandler,
    private val context: Context
) {
    private val TAG = "PushNotificationHandler"

    // HardCoded CoroutineScope for Testing
    private val job = SupervisorJob()     // Variable for manual Cancellation
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

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

    fun inactivateCoroutineScope() {
        job.cancel()
        Log.d(TAG, "Coroutine Active Status: ${coroutineScope.isActive}")
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun RemoteMessage.toPushNotification() {

        // Create Pending Intent (For access push notification while the app is in foreground)
        val nttId = this@toPushNotification.data["nttId"]
        val url = this@toPushNotification.data["contentUrl"]
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
            setContentText(context.getString(R.string.text_push_to_notice))
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

            coroutineScope.launch {
                Log.d(TAG, "START FETCHING NOTICE")
                nttId?.let {
                    val notice = async {
                        runCatching {
                            withTimeout(5000L) {
                                remoteRepository.queryNoticeById(it.toInt())
                                    .firstOrNull()
                            }
                        }
                    }
                    notice.await().fold(
                        onSuccess = { nullable ->
                            nullable?.let { vo ->
                                Log.d(TAG, "RECEIVED ${vo.toString()}")
                                notificationBuilder.apply {
                                    setContentTitle(localizedTitle(vo.noticeName))
                                    setContentText(vo.title)
                                }

                                vo.imageUrl?.let { url ->
                                    val bitmapImage = async {
                                        runCatching {
                                            withTimeout(5000L) {
                                                imageRepository.getImageByteArrayFromUrl(url)?.let { b ->
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
                            }
                        },
                        onFailure = {
                            Log.d(TAG, "Unable to get Notice.\nREASON: ${it.stackTrace}")
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
                NoticeCategory.JOB_NEWS.name -> getString(R.string.job_news)
                else -> null
            }?.let {
                "${getString(R.string.push_title_new)} " +
                        "$it ${getString(R.string.push_title_arrived)}"
            } ?: "${getString(R.string.push_title_new)} ${getString(R.string.push_title_arrived)}"
        }
    }
}