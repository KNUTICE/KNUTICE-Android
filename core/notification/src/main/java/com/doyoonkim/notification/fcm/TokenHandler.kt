package com.doyoonkim.notification.fcm

import android.util.Log
import com.doyoonkim.common.di.TokenHandler
import com.doyoonkim.domain.usecases.ValidateDeviceToken
import com.doyoonkim.model.requestBody.DeviceTokenBody
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class TokenHandlerImpl @Inject constructor(
    private val validateDeviceToken: ValidateDeviceToken
) : TokenHandler {
    private val TAG = this.javaClass.name

    override fun handleCurrentTokenRequest() = flow {
        runCatching {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d(TAG, "Incomplete task: ${task.exception}")
                    throw Exception(task.exception?.message)
                }

                // Get new FCM registration token
                val registrationToken = task.result
                Log.d(TAG, "Received Token: $registrationToken")

                // POST request to upload current token to the web server.
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d(TAG, "Start validating Token")
                    validateDeviceToken(DeviceTokenBody(fcmToken = registrationToken))
                        .collectLatest { result ->
                            if (result) Log.d(TAG, "Validation Successful")
                            else throw Exception("Unable to validate")
                        }
                }
            })
        }.fold(
            onSuccess = { emit(true) },
            onFailure = {
                println("Unable to validate: ${it.message}")
                emit(false)
            }
        )
    }
}