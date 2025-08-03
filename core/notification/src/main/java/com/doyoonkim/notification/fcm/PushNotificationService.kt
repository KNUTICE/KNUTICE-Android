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

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        (applicationContext as AppInjectorProvider).appInjector.inject(this)
        super.onMessageReceived(message)
        handlerProvider.get().handleReceivedMessage(message)
    }

    override fun onDestroy() {
        handlerProvider.get().inactivateCoroutineScope()
        super.onDestroy()
    }
}