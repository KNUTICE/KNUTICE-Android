package com.doyoonkim.network

import android.util.Log
import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.network.model.DeviceTokenRequest
import com.doyoonkim.network.model.TopicSubscriptionPreferencesRequest
import com.doyoonkim.network.model.UserReportRequest
import com.doyoonkim.network.retrofit.KnuticeService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author kimdoyoon
 * Created 6/3/25 at 12:27 AM
 */

// This class should be provided/injected as Singleton Instance.
class KnuticeRemoteSource @Inject constructor(
    private val knuticeApi: KnuticeService,
    private val deviceToken: DeviceToken
) {
    private val TAG = "KnuticeRemoteSource"

    suspend fun getTopThreeNotices() = runCatching {
            knuticeApi.getTopThreeNotices()
        }

    suspend fun getNoticesPerPage(category: NoticeCategory, lastNttId: Int?) = runCatching {
            knuticeApi.getNoticesPerPage(category, lastNttId)
        }

    suspend fun getNoticeById(nttId: Int) = runCatching {
            knuticeApi.getNoticeById(nttId.toString())
        }


    suspend fun getNoticesByKeyword(keyword: String, nttId: Int?) = runCatching {
            Log.d(TAG, "Received Parameter: $keyword $nttId")
            knuticeApi.getNoticesByKeyword(keyword, nttId)
        }

    suspend fun getTopicSubscriptionStatus() = runCatching {
            if (deviceToken.validatedToken().isBlank()) throw Exception("No validated token found")
            else {
                knuticeApi.getTopicSubscriptionStatus(deviceToken.validatedToken())
            }
        }

    suspend fun getAllTips() = runCatching {
        knuticeApi.getAllTips(deviceType = "AOS")
    }

    suspend fun validateToken(request: DeviceTokenRequest) = runCatching {
            knuticeApi.validateToken(request)
        }

    fun updateValidatedToken(fcmToken: String) {
        deviceToken.updateValidatedToken(fcmToken)
    }

    suspend fun submitUserReport(request: UserReportRequest) = runCatching {
        val body = request.body.copy(fcmToken = deviceToken.validatedToken())
        knuticeApi.submitUserReport(request.copy(body = body))
    }

    suspend fun submitTopicSubscriptionPreferences(request: TopicSubscriptionPreferencesRequest) =
        runCatching {
            val body = request.body.copy(fcmToken = deviceToken.validatedToken())
            knuticeApi.submitTopicSubscriptionPreferences(request.copy(body = body))
        }
}