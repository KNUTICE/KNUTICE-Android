package com.doyoonkim.data.repository

import android.util.Log
import com.doyoonkim.domain.interfaces.NoticeRemoteRepository
import com.doyoonkim.domain.interfaces.TipRemoteRepository
import com.doyoonkim.domain.interfaces.TokenRemoteRepository
import com.doyoonkim.domain.interfaces.TopicSubscriptionRemoteRepository
import com.doyoonkim.domain.interfaces.UserReportRemoteRepository
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.model.TipVO
import com.doyoonkim.model.TokenStatus
import com.doyoonkim.model.TopicType
import com.doyoonkim.model.requestBody.DeviceTokenBody
import com.doyoonkim.model.requestBody.TokenUpdateBody
import com.doyoonkim.model.requestBody.TopicSubscriptionPreferencesBody
import com.doyoonkim.model.requestBody.UserReportBody
import com.doyoonkim.network.KnuticeRemoteSource
import com.doyoonkim.network.model.FcmTokenSaveRequest
import com.doyoonkim.network.model.FcmTokenUpdateRequest
import com.doyoonkim.network.model.ReportSaveRequest
import com.doyoonkim.network.model.TopicUpdateRequest
import kotlinx.coroutines.flow.flow
import model.Metadata
import java.net.ConnectException
import java.net.SocketTimeoutException
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

    override suspend fun queryTopThreeNotices(category: String): List<NoticeVO>? {
        remoteSource.getNoticesPerPage(category = category, size = 3).fold(
            onSuccess = {
                if (it.result?.resultCode == 200) return it.body?.map { it.toVO() }
                else it.result?.printLog().also { return emptyList<NoticeVO>() }
            },
            onFailure = {
                it.printLog()
                return null
            }
        )
        return null
    }



    override fun queryNoticesPerPage(category: String, lastNttId: Int?) = flow {
        remoteSource.getNoticesPerPage(category = category, lastNttId = lastNttId).fold(
            onSuccess = {
                if (it.result?.resultCode == 200) emit(it.body?.map { it.toVO() })
                else it.result.printLog().also { emit(emptyList<NoticeVO>()) }
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

    override fun queryNoticesByKeyword(keyword: String, lastNttId: Int?) = flow {
        remoteSource.getNoticesByKeyword(keyword, lastNttId).fold(
            onSuccess = {
                if (it.result?.resultCode == 200) emit(it.body?.map { it.toVO() })
                else it.result.printLog().also { emit(emptyList<NoticeVO>()) }
            },
            onFailure = {
                it.printLog()
                emit(null)
            }
        )
    }

    override suspend fun getNoticeSummary(nttId: Int): String? {
        remoteSource.getNoticeSummary(nttId).fold(
            onSuccess = {
                if (it.result?.resultCode == 200) return it.body?.rawSummary
                else it.result.printLog().also { return null }
            },
            onFailure = {
                it.printLog()
                return null
            }
        )
        return null
    }

    override fun queryTopicSubscriptionStatus(topicType: TopicType) = flow {
        remoteSource.getTopicSubscriptionStatus(topicType.name).fold(
            onSuccess = {
                Log.d(TAG, it.toString())
                if (it.result?.resultCode == 200) emit(it.body?.toVO(topicType))
                else {
                    if (it.body != null) emit(it.body?.toVO(topicType))
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
                } else it.result.printLog().also { emit(emptyList<TipVO>()) }
            },
            onFailure = {
                it.printLog()
                emit(null)
            }
        )
    }

    @Deprecated("Use requestUpdateFcmToken instead.")
    override fun requestUpdateValidatedToken(fcmToken: String) {
        remoteSource.updateValidatedToken(fcmToken)
    }

    override suspend fun requestUpdateFcmToken(body: TokenUpdateBody): TokenStatus {
        remoteSource.updateDeviceToken(
            newToken = body.newFcmToken,
            request = FcmTokenUpdateRequest(
                oldFcmToken = body.oldFcmToken,
                deviceType = body.deviceType
            )
        ).fold(
            onSuccess = {
                if (it.result?.resultCode == 200) return TokenStatus.SUCCESS
                else it.result.printLog().also { return TokenStatus.RETRY }
            },
            onFailure = {
                it.printLog()
                return when(it) {
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
            token = body.fcmToken,
            request = FcmTokenSaveRequest(body.deviceType)
        ).fold(
            onSuccess = {
                if (it.result?.resultCode == 200) return TokenStatus.SUCCESS
                else it.result.printLog().also { return TokenStatus.RETRY }
            },
            onFailure = {
                it.printLog()
                when(it) {
                    is ConnectException -> return TokenStatus.RETRY
                    is SocketTimeoutException -> return TokenStatus.RETRY
                    else -> return TokenStatus.FAILURE
                }
            }
        )
        // Default
        return TokenStatus.FAILURE
    }

    override fun requestUserReportSubmission(body: UserReportBody) = flow {
        remoteSource.submitUserReport(
            ReportSaveRequest(
                content = body.content,
                deviceName = body.deviceName,
                version = body.version
            )
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
                type = body.topicType,
                request = TopicUpdateRequest(
                    topic = body.noticeName,
                    enabled = body.isSubscribed
                )
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

    private fun Metadata?.printLog() =
        Log.d(TAG, "Failed to receive data (${this?.resultCode})" +
                "\nREASON:${this?.resultMessage}")
}