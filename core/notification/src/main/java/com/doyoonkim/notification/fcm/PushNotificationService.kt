package com.doyoonkim.notification.fcm

import android.Manifest
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import com.doyoonkim.common.di.AppInjectorProvider
import com.doyoonkim.common.di.TokenHandler
import com.google.firebase.messaging.Constants.MessageNotificationKeys
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import javax.inject.Inject

class PushNotificationService : FirebaseMessagingService() {
    @Inject lateinit var handlerProvider: Lazy<PushNotificationHandler>
    @Inject lateinit var tokenHandler: Lazy<TokenHandler>

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
        (applicationContext as AppInjectorProvider).appInjector.inject(this)
        // POST request to send FCM Token to the Server.
        Log.d(TAG, "onNewToken() call: Received Token: ${token.toString()}")

        // Pending for later version.
//        val supervisorJob = SupervisorJob()
//        CoroutineScope(supervisorJob + Dispatchers.IO).launch {
//            tokenHandler.get().invoke(token)
//        }.invokeOnCompletion {
//            supervisorJob.cancel()
//        }
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