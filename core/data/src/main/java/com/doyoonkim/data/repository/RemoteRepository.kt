package com.doyoonkim.data.repository

import android.util.Log
import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.network.KnuticeRemoteSource
import com.doyoonkim.network.model.DeviceTokenRequest
import com.doyoonkim.network.model.TopicSubscriptionPreferencesRequest
import com.doyoonkim.network.model.UserReportRequest
import kotlinx.coroutines.flow.flow
import model.NetworkResult

class RemoteRepository constructor(
    private val remoteSource: KnuticeRemoteSource
) {
    private val TAG = "RemoteRepository"

    fun queryTopThreeNotices() = flow {
        remoteSource.getTopThreeNotices().fold(
            onSuccess = {
                if (it.result?.resultCode == 200) emit(it.body)
                else it.result.printLog().also { emit(null) }
            },
            onFailure = {
                it.printLog()
                emit(null)
            }
        )
    }

    fun queryNoticesPerPage(category: NoticeCategory, lastNttId: Int) = flow {
        remoteSource.getNoticesPerPage(category, lastNttId).fold(
            onSuccess = {
                if (it.result?.resultCode == 200) emit(it.body)
                else it.result.printLog().also { emit(null) }
            },
            onFailure = {
                it.printLog()
                emit(null)
            }
        )
    }

    fun queryNoticeById(nttId: Int) = flow {
        remoteSource.getNoticeById(nttId).fold(
            onSuccess = {
                if (it.result?.resultCode == 200) emit(it.body)
                else it.result.printLog().also { emit(null) }
            },
            onFailure = {
                it.printLog()
                emit(null)
            }
        )
    }

    fun queryNoticesByKeyword(keyword: String) = flow {
        remoteSource.getNoticesByKeyword(keyword).fold(
            onSuccess = {
                if (it.result?.resultCode == 200) emit(it.body)
                else it.result.printLog().also { emit(null) }
            },
            onFailure = {
                it.printLog()
                emit(null)
            }
        )
    }

    fun queryTopicSubscriptionStatus() = flow {
        remoteSource.getTopicSubscriptionStatus().fold(
            onSuccess = {
                if (it.result?.resultCode == 200) emit(it.body)
                else it.result.printLog().also { emit(null) }
            },
            onFailure = {
                it.printLog()
                emit(null)
            }
        )
    }

    fun requestTokenValidation(request: DeviceTokenRequest) = flow {
        remoteSource.validateToken(request).fold(
            onSuccess = {
                if (it.result?.resultCode == 200) emit(true)
                else it.result.printLog().also { emit(false) }
            },
            onFailure = {
                it.printLog()
                emit(false)
            }
        )
    }

    fun requestUpdateValidatedToken(fcmToken: String) = flow {
        remoteSource.updateValidatedToken(fcmToken).fold(
            onSuccess = {
                if (it) emit(true)
                else emit(false)
            },
            onFailure = {
                it.printLog()
                emit(false)
            }
        )
    }

    fun requestUserReportSubmission(request: UserReportRequest) = flow {
        remoteSource.submitUserReport(request).fold(
            onSuccess = {
                if (it.result?.resultCode == 200) emit(true)
                else it.result.printLog().also { emit(false) }
            },
            onFailure = {
                it.printLog()
                emit(false)
            }
        )
    }

    fun requestTopicSubscriptionPreferencesSubmission(
        request: TopicSubscriptionPreferencesRequest
    ) = flow {
        remoteSource.submitTopicSubscriptionPreferences(request).fold(
            onSuccess = {
                if (it.result?.resultCode == 200) emit(true)
                else it.result.printLog().also { emit(false) }
            },
            onFailure = {
                it.printLog()
                emit(false)
            }
        )
    }

    private fun Throwable.printLog() =
        Log.d(TAG, "Failed to receive data\nREASON: ${this.stackTraceToString()}")

    private fun NetworkResult?.printLog() =
        Log.d(TAG, "Failed to receive data (${this?.resultCode}" +
                "\nREASON:${this?.resultMessage}")
}