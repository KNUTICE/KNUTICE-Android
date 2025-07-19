package com.doyoonkim.notification.fcm

import android.util.Log
import com.doyoonkim.common.di.TokenHandler
import com.doyoonkim.domain.usecases.ValidateDeviceToken
import com.doyoonkim.model.requestBody.DeviceTokenBody
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TokenHandlerImpl @Inject constructor(
    private val validateDeviceToken: ValidateDeviceToken
) : TokenHandler {
    private val TAG = this.javaClass.name

    override fun handleCurrentTokenRequest() = flow {
        runCatching {
            val deviceToken = Firebase.messaging.token.await()
            Log.d(TAG, "Received Token: $deviceToken")
            emitAll(validateDeviceToken(DeviceTokenBody(fcmToken = deviceToken)))
        }.onFailure {
            Log.d(TAG, "Failure: ${it.message}")
            emit(false)
        }
    }
}