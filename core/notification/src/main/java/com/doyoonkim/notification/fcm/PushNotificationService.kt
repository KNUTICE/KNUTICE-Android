package com.doyoonkim.notification.fcm

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import com.doyoonkim.common.di.AppInjectorProvider
import com.doyoonkim.common.di.TokenHandler
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.Lazy
import javax.inject.Inject

class PushNotificationService : FirebaseMessagingService() {
    @Inject lateinit var handlerProvider: Lazy<PushNotificationHandler>

    @Inject lateinit var tokenHandler: Lazy<TokenHandler>

    private val TAG = "PushNotificationHandler"

    override fun onCreate() {
        super.onCreate()
        // For Field Injection
        (applicationContext as AppInjectorProvider).appInjector.inject(this)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        (applicationContext as AppInjectorProvider).appInjector.inject(this)
        // POST request to send FCM Token to the Server.
        Log.d(TAG, "onNewToken() call: Received Token: $token")
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
