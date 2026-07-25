package com.doyoonkim.data.repository.remote

import android.util.Log
import com.doyoonkim.domain.interfaces.AppTokenPreferenceRepository
import com.doyoonkim.domain.interfaces.CarrelStatusRemoteRepository
import com.doyoonkim.model.CarrelRoomStatusVO
import com.doyoonkim.network.KnuticeRemoteSource
import com.doyoonkim.network.util.NetworkResultUtil
import model.Metadata
import javax.inject.Inject

class CampusRemoteRepository @Inject constructor(
    private val remoteSource: KnuticeRemoteSource,
    private val appPreference: AppTokenPreferenceRepository
) : CarrelStatusRemoteRepository {
    companion object {
        private val TAG = "CampusRemoteRepository"
    }

    override suspend fun getCarrelRoomStatus(): Result<List<CarrelRoomStatusVO>> {
        remoteSource.getCarrelRoomStatus(
            appPreference.getCachedToken()
        ).fold(
            onSuccess = { response ->
                if (response.result == null) {
                    Log.d(TAG, "Unable to receive data").also {
                        return Result.failure(Exception("Unable to receive data"))
                    }
                }

                if (response.result?.resultCode == 200) {
                    return Result.success(response.body?.map { dto -> dto.toVO() } ?: emptyList())
                } else {
                    response.result?.printLog()
                    return Result.failure(NetworkResultUtil.resultCodeValidator(response.result))
                }
            },
            onFailure = {
                it.printLog()
                return Result.failure(it)
            }
        )
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
