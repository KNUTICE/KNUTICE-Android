package com.doyoonkim.notification.fcm

import android.util.Log
import com.doyoonkim.domain.usecases.ValidateDeviceToken
import com.doyoonkim.model.requestBody.DeviceTokenBody
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class TokenHandler @Inject constructor(
    private val validateDeviceToken: ValidateDeviceToken
) {
    private val TAG = this.javaClass.name

    fun handleCurrentTokenRequest() {
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
                Log.d(TAG, "Start validating Token")
                validateDeviceToken(
                    DeviceTokenBody(fcmToken = registrationToken)
                ).collectLatest { result ->
                    if (result) Log.d(TAG, "Validation Successful")
                    else Log.d(TAG, "Unable to validate")
                }
            }
        })
    }
}