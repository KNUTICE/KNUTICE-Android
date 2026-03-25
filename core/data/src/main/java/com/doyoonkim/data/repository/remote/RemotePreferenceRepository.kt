package com.doyoonkim.data.repository.remote

import android.util.Log
import com.doyoonkim.domain.interfaces.AppPreferenceRepository
import com.doyoonkim.domain.interfaces.TopicSubscriptionRemoteRepository
import com.doyoonkim.domain.interfaces.UserReportRemoteRepository
import com.doyoonkim.model.TopicType
import com.doyoonkim.model.requestBody.TopicSubscriptionPreferencesBody
import com.doyoonkim.model.requestBody.UserReportBody
import com.doyoonkim.network.KnuticeRemoteSource
import com.doyoonkim.network.model.ReportSaveRequest
import com.doyoonkim.network.model.TopicUpdateRequest
import kotlinx.coroutines.flow.flow
import model.Metadata
import javax.inject.Inject

class RemotePreferenceRepositoryImpl @Inject constructor(
    private val remoteSource: KnuticeRemoteSource,
    private val appPreference: AppPreferenceRepository
) : TopicSubscriptionRemoteRepository, UserReportRemoteRepository {

    private val TAG = "RemotePreferenceRepository"

    override fun queryTopicSubscriptionStatus(topicType: TopicType) = flow {
        remoteSource.getTopicSubscriptionStatus(
            appPreference.getCachedToken(),
            topicType.name
        ).fold(
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

    override fun requestTopicSubscriptionPreferencesSubmission(
        body: TopicSubscriptionPreferencesBody
    ) = flow {
        remoteSource.run {
            submitTopicSubscriptionPreferences(
                token = appPreference.getCachedToken(),
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

    override fun requestUserReportSubmission(body: UserReportBody) = flow {
        remoteSource.submitUserReport(
            appPreference.getCachedToken(),
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

    private fun Throwable.printLog() =
        Log.d(TAG, "Failed to receive data\nREASON: ${this.stackTraceToString()}")

    private fun Metadata?.printLog() =
        Log.d(TAG, "Failed to receive data (${this?.resultCode})" +
                "\nREASON:${this?.resultMessage}")
}