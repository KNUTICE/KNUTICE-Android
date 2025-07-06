package com.doyoonkim.data.repository

import android.util.Log
import com.doyoonkim.domain.interfaces.NoticeRemoteRepository
import com.doyoonkim.domain.interfaces.TipRemoteRepository
import com.doyoonkim.domain.interfaces.TokenRemoteRepository
import com.doyoonkim.domain.interfaces.TopicSubscriptionRemoteRepository
import com.doyoonkim.domain.interfaces.UserReportRemoteRepository
import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.model.requestBody.DeviceTokenBody
import com.doyoonkim.model.requestBody.TopicSubscriptionPreferencesBody
import com.doyoonkim.model.requestBody.UserReportBody
import com.doyoonkim.network.KnuticeRemoteSource
import com.doyoonkim.network.model.DeviceTokenRequest
import com.doyoonkim.network.model.TopicSubscriptionPreferencesRequest
import com.doyoonkim.network.model.UserReportRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import model.NetworkResult
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor(
    private val remoteSource: KnuticeRemoteSource
) : NoticeRemoteRepository,
    TokenRemoteRepository,
    TopicSubscriptionRemoteRepository,
    UserReportRemoteRepository,
    TipRemoteRepository
{
    private val TAG = "RemoteRepositoryImpl"

    override fun queryTopThreeNotices() = flow {
        remoteSource.getTopThreeNotices().fold(
            onSuccess = {
                if (it.result?.resultCode == 200) emit(it.body?.toVO())
                else it.result.printLog().also { emit(null) }
            },
            onFailure = {
                it.printLog()
                emit(null)
            }
        )
    }.flowOn(Dispatchers.IO)

    override fun queryNoticesPerPage(category: NoticeCategory, lastNttId: Int?) = flow {
        remoteSource.getNoticesPerPage(category, lastNttId).fold(
            onSuccess = {
                if (it.result?.resultCode == 200) emit(it.body?.map { it.toVO() })
                else it.result.printLog().also { emit(null) }
            },
            onFailure = {
                it.printLog()
                emit(null)
            }
        )
    }

    override fun queryNoticeById(nttId: Int) = flow {
        remoteSource.getNoticeById(nttId).fold(
            onSuccess = {
                Log.d(TAG, "Received Result: ${it.toString()}")
                if (it.result?.resultCode == 200) emit(it.body?.toVO())
                else it.result.printLog().also { emit(null) }
            },
            onFailure = {
                it.printLog()
                emit(null)
            }
        )
    }

    override fun queryNoticesByKeyword(keyword: String) = flow {
        remoteSource.getNoticesByKeyword(keyword).fold(
            onSuccess = {
                if (it.result?.resultCode == 200) emit(it.body?.map { it.toVO() })
                else it.result.printLog().also { emit(null) }
            },
            onFailure = {
                it.printLog()
                emit(null)
            }
        )
    }

    override fun queryTopicSubscriptionStatus() = flow {
        remoteSource.getTopicSubscriptionStatus().fold(
            onSuccess = {
                if (it.result?.resultCode == 200) emit(it.body?.toVO())
                else {
                    if (it.body != null) emit(it.body?.toVO())
                    else it.result.printLog().also { emit(null) }
                }
            },
            onFailure = {
                it.printLog()
                emit(null)
            }
        )
    }

    override fun queryAllTips() = flow {
        remoteSource.getAllTips().fold(
            onSuccess = {
                if (it.result?.resultCode == 200) {
                    emit(it.body?.map { dto -> dto.toVO() })
                } else it.result.printLog().also { emit(null) }
            },
            onFailure = {
                it.printLog()
                emit(null)
            }
        )
    }

    override fun requestTokenValidation(body: DeviceTokenBody) = flow {
        remoteSource.validateToken(
            DeviceTokenRequest(body = body)
        ).fold(
            onSuccess = {
                if (it.result?.resultCode == 200) emit(true).also {
                    remoteSource.updateValidatedToken(body.fcmToken)
                }
                else it.result.printLog().also { emit(false) }
            },
            onFailure = {
                it.printLog()
                emit(false)
            }
        )
    }

    override fun requestUpdateValidatedToken(fcmToken: String) {
        remoteSource.updateValidatedToken(fcmToken)
    }

    override fun requestUserReportSubmission(body: UserReportBody) = flow {
        remoteSource.submitUserReport(
            // Should be revised.
            UserReportRequest(body = body.copy(fcmToken = remoteSource.validatedToken))
        ).fold(
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

    override fun requestTopicSubscriptionPreferencesSubmission(
        body: TopicSubscriptionPreferencesBody
    ) = flow {
        remoteSource.run {
            submitTopicSubscriptionPreferences(
                TopicSubscriptionPreferencesRequest(body = body.copy(fcmToken = this.validatedToken))
            )
        }.fold(
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
        Log.d(TAG, "Failed to receive data (${this?.resultCode})" +
                "\nREASON:${this?.resultMessage}")
}