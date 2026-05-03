package com.doyoonkim.notification.fcm

import android.util.Log
import com.doyoonkim.common.di.TokenHandler
import com.doyoonkim.domain.interfaces.AppTokenPreferenceRepository
import com.doyoonkim.domain.usecases.ValidateDeviceToken
import com.doyoonkim.model.TokenStatus
import com.doyoonkim.model.requestBody.DeviceTokenBody
import com.doyoonkim.model.requestBody.TokenUpdateBody
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TokenHandlerImpl @Inject constructor(
    private val validateDeviceToken: ValidateDeviceToken,
    private val appPreferences: AppTokenPreferenceRepository
) : TokenHandler {
    private val TAG = this.javaClass.name

    override suspend fun invoke(t: String?): TokenStatus {
        val token = t ?: getToken()
        val cached = appPreferences.getCachedToken()

        if (token == null)  {
            // Firebase Service Unavailable
            return TokenStatus.RETRY
        }

        if (token != cached) appPreferences.updateDeviceToken(token)

        if (cached.isNullOrBlank()) {
            Log.d("TokenHandler", "Token $token would be registered")
                return registerNewToken(token)
        } else {
            Log.d("TokenHandler", "Token $cached would be replaced with $token")
                return updateRegisteredToken(cached, token)
        }
    }

    override suspend fun registerNewToken(newToken: String) =
        validateDeviceToken.register(
            DeviceTokenBody(
                fcmToken = newToken
            )
        )

    override suspend fun updateRegisteredToken(oldToken: String, newToken: String) =
        validateDeviceToken.update(
            TokenUpdateBody(
                oldFcmToken = oldToken,
                newFcmToken = newToken
            )
        )

    override suspend fun validation(): Boolean {
        val token = getToken()
        val cached = appPreferences.getCachedToken()

        Log.d(TAG, "Received: $token")
        Log.d(TAG, "Cached: $cached")

        if (token == null) return false

        return token == cached
    }

    // Access Token with Exception Handling
    private suspend fun getToken(): String? {
        return try {
            Firebase.messaging.token.await()
        } catch (e: Exception) {
            // Unable to access to Token (IPC Failure; Firebase Service Not Available.)
            Log.e("TokenHandler", "Unable to access Token ${e.localizedMessage}")
            null
        }
    }
}