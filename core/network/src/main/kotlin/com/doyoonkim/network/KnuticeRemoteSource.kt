package com.doyoonkim.network

import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.network.model.DeviceTokenRequest
import com.doyoonkim.network.model.TopicSubscriptionPreferencesRequest
import com.doyoonkim.network.model.UserReportRequest
import com.doyoonkim.network.retrofit.KnuticeService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author kimdoyoon
 * Created 6/3/25 at 12:27 AM
 */

// This class should be provided/injected as Singleton Instance.
@Singleton
class KnuticeRemoteSource @Inject constructor(
    private val knuticeApi: KnuticeService
) {
    private val TAG = "KnuticeRemoteSource"

    // Should be later migrated to DataStore.
    var validatedToken: String = ""

    suspend fun getTopThreeNotices() = runCatching {
            knuticeApi.getTopThreeNotices()
        }

    suspend fun getNoticesPerPage(category: NoticeCategory, lastNttId: Int?) = runCatching {
            knuticeApi.getNoticesPerPage(category, lastNttId)
        }

    suspend fun getNoticeById(nttId: Int) = runCatching {
            knuticeApi.getNoticeById(nttId.toString())
        }


    suspend fun getNoticesByKeyword(keyword: String) = runCatching {
            knuticeApi.getNoticesByKeyword(keyword)
        }

    suspend fun getTopicSubscriptionStatus() = runCatching {
            if (validatedToken.isBlank()) throw Exception("No validated token found")
            else {
                knuticeApi.getTopicSubscriptionStatus(validatedToken)
            }
        }

    suspend fun getAllTips() = runCatching {
        knuticeApi.getAllTips(deviceType = "AOS")
    }

    suspend fun validateToken(request: DeviceTokenRequest) = runCatching {
            knuticeApi.validateToken(request)
        }

    fun updateValidatedToken(fcmToken: String) {
        validatedToken = fcmToken
    }

    suspend fun submitUserReport(request: UserReportRequest) = runCatching {
            knuticeApi.submitUserReport(request)
        }

    suspend fun submitTopicSubscriptionPreferences(request: TopicSubscriptionPreferencesRequest) =
        runCatching {
            knuticeApi.submitTopicSubscriptionPreferences(request)
        }
}