package com.doyoonkim.knutice.fcm

import android.util.Log
import com.doyoonkim.knutice.data.KnuticeRemoteSource
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import javax.inject.Inject

class PushNotificationHandler @Inject constructor(
    private val remoteSource: KnuticeRemoteSource
) : FirebaseMessagingService() {
    private val TAG = "PushNotificationHandler"

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // POST request to send FCM Token to the Server.
        Log.d(TAG, "Received Token: ${token.toString()}")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "Message data payload: ${message.data}")

        if (message.data.isNotEmpty()) {
            Log.d(TAG, "Message Data Payload: ${message.data}")
        }

        message.notification?.let {
            Log.d(TAG, "Body: ${it.body}")
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
            remoteSource.validateToken(registrationToken)
        })
    }

}