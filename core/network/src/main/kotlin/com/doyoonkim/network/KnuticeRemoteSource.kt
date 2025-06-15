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
class KnuticeRemoteSource @Inject constructor() {
    private val TAG = "KnuticeRemoteSource"

    // Consider providing this instance via DI.
    private val knuticeService = Retrofit.Builder()
        .baseUrl(BuildConfig.API_LIVE)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Should be later migrated to DataStore.
    var validatedToken: String = ""

    suspend fun getTopThreeNotices() = runCatching {
            knuticeService.create(KnuticeService::class.java).getTopThreeNotices()
        }.onFailure { throw it }

    suspend fun getNoticesPerPage(category: NoticeCategory, lastNttId: Int?) = runCatching {
            knuticeService.create(KnuticeService::class.java).getNoticesPerPage(category, lastNttId)
        }.onFailure { throw it }

    suspend fun getNoticeById(nttId: Int) = runCatching {
            knuticeService.create(KnuticeService::class.java).getNoticeById(nttId.toString())
        }.onFailure { throw it }


    suspend fun getNoticesByKeyword(keyword: String) = runCatching {
            knuticeService.create(KnuticeService::class.java).getNoticesByKeyword(keyword)
        }.onFailure { throw it }

    suspend fun getTopicSubscriptionStatus() = runCatching {
            if (validatedToken.isBlank()) throw Exception("No validated token found")
            else {
                knuticeService.create(KnuticeService::class.java).getTopicSubscriptionStatus(validatedToken)
            }
        }.onFailure { throw it }

    suspend fun validateToken(request: DeviceTokenRequest) = runCatching {
            knuticeService.create(KnuticeService::class.java).validateToken(request)
        }.onFailure { throw it }

    fun updateValidatedToken(fcmToken: String) {
        validatedToken = fcmToken
    }

    suspend fun submitUserReport(request: UserReportRequest) = runCatching {
            knuticeService.create(KnuticeService::class.java).submitUserReport(request)
        }.onFailure { throw it }

    suspend fun submitTopicSubscriptionPreferences(request: TopicSubscriptionPreferencesRequest) =
        runCatching {
            knuticeService.create(KnuticeService::class.java).submitTopicSubscriptionPreferences(request)
        }.onFailure { throw it }
}