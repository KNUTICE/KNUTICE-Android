package com.doyoonkim.data.repository.remote

import android.util.Log
import com.doyoonkim.domain.interfaces.AppTokenPreferenceRepository
import com.doyoonkim.domain.interfaces.TokenRemoteRepository
import com.doyoonkim.model.TokenStatus
import com.doyoonkim.model.requestBody.DeviceTokenBody
import com.doyoonkim.model.requestBody.TokenUpdateBody
import com.doyoonkim.network.KnuticeRemoteSource
import com.doyoonkim.network.model.FcmTokenSaveRequest
import com.doyoonkim.network.model.FcmTokenUpdateRequest
import model.Metadata
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject

class RemoteTokenRepositoryImpl @Inject constructor(
    private val remoteSource: KnuticeRemoteSource,
    private val appPreference: AppTokenPreferenceRepository
) : TokenRemoteRepository {

    private val TAG = "RemoteTokenRepositoryImpl"

    override suspend fun requestUpdateFcmToken(body: TokenUpdateBody): TokenStatus {
        remoteSource.updateDeviceToken(
            cachedToken = appPreference.getCachedToken(),
            newToken = body.newFcmToken,
            request = FcmTokenUpdateRequest(
                oldFcmToken = body.oldFcmToken,
                deviceType = body.deviceType
            )
        ).fold(
            onSuccess = {
                if (it.result?.resultCode == 200) {
                    return TokenStatus.SUCCESS
                } else {
                    it.result.printLog().also { return TokenStatus.RETRY }
                }
            },
            onFailure = {
                it.printLog()
                return when (it) {
                    is ConnectException -> TokenStatus.RETRY
                    is SocketTimeoutException -> TokenStatus.RETRY
                    else -> TokenStatus.FAILURE
                }
            }
        )
        // Default
        return TokenStatus.FAILURE
    }

    override suspend fun requestFcmTokenRegistration(body: DeviceTokenBody): TokenStatus {
        remoteSource.validateToken(
            cachedToken = appPreference.getCachedToken(),
            receivedToken = body.fcmToken,
            request = FcmTokenSaveRequest(body.deviceType)
        ).fold(
            onSuccess = {
                if (it.result?.resultCode == 200) {
                    return TokenStatus.SUCCESS
                } else {
                    it.result.printLog().also { return TokenStatus.RETRY }
                }
            },
            onFailure = {
                it.printLog()
                return when (it) {
                    is ConnectException -> TokenStatus.RETRY
                    is SocketTimeoutException -> TokenStatus.RETRY
                    else -> TokenStatus.FAILURE
                }
            }
        )
        // Default
        return TokenStatus.FAILURE
    }

    private fun Throwable.printLog() =
        Log.d(TAG, "Failed to receive data\nREASON: ${this.stackTraceToString()}")

    private fun Metadata?.printLog() =
        Log.d(
            TAG,
            "Failed to receive data (${this?.resultCode})" +
                "\nREASON:${this?.resultMessage}"
        )
}
