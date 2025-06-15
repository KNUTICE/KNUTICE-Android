package com.doyoonkim.notification.fcm

import android.Manifest
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import com.doyoonkim.common.di.AppInjectorProvider
import com.google.firebase.messaging.Constants.MessageNotificationKeys
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.Lazy
import javax.inject.Inject

class PushNotificationService : FirebaseMessagingService() {
    @Inject lateinit var handlerProvider: Lazy<PushNotificationHandler>
    private val TAG = "PushNotificationHandler"

    override fun onCreate() {
        super.onCreate()
        // For Field Injection
        (applicationContext as AppInjectorProvider).appInjector.inject(this)
        Log.d(TAG, "Initialized?: ${::handlerProvider.isInitialized}")
        Log.d(TAG, "Called")
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // POST request to send FCM Token to the Server.
        Log.d(TAG, "Received Token: ${token.toString()}")
//        requestCurrentToken()
    }

    // Fix problem: onMessageReceived is not being called when app is in background/cold-start
    override fun handleIntent(intent: Intent?) {
        // Manually remove notification payload (https://medium.com/@jms8732/background에서-onmessagereceived가-호출-안되는-현상에-관하여-7595df624d91)
        val newIntent = intent?.apply {
            val temp = extras?.apply {
                remove(MessageNotificationKeys.ENABLE_NOTIFICATION)
                remove("gcm.notification.e")
            }
            replaceExtras(temp)
        }
        super.handleIntent(newIntent)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        (applicationContext as AppInjectorProvider).appInjector.inject(this)
        super.onMessageReceived(message)
        handlerProvider.get().handleReceivedMessage(message)
    }
}