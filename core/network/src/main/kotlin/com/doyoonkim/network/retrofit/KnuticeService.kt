package com.doyoonkim.network.retrofit

import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.network.model.DeviceTokenRequest
import com.doyoonkim.network.model.TopicSubscriptionPreferencesRequest
import com.doyoonkim.network.model.UserReportRequest
import model.NoticeByIdResult
import model.NoticesByKeywordResult
import model.NoticesPerPageResult
import model.PostResult
import model.TipResult
import model.TopThreeNoticeResults
import model.TopicSubscriptionPreferencesResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @author kimdoyoon
 * Created 6/2/25 at 10:58 PM
 */

interface KnuticeService {
    @GET("open-api/notice")
    suspend fun getTopThreeNotices(): TopThreeNoticeResults

    @GET("open-api/notice/list")
    suspend fun getNoticesPerPage(
        @Query("noticeName") category: NoticeCategory,
        @Query("nttId") lastNttId: Int? = null
    ): NoticesPerPageResult

    @GET("open-api/notice/{nttId}")
    suspend fun getNoticeById(
        @Path("nttId") nttId: String
    ): NoticeByIdResult

    @GET("open-api/search")
    suspend fun getNoticesByKeyword(
        @Query("keyword") keyword: String
    ): NoticesByKeywordResult

    @GET("open-api/topic")
    suspend fun getTopicSubscriptionStatus(
        @Header("fcmToken") token: String
    ): TopicSubscriptionPreferencesResult

    @GET("open-api/tips")
    suspend fun getAllTips(
        @Query("deviceType") deviceType: String
    ): TipResult

    @Headers("Content-Type: application/json")
    @POST("open-api/fcm")
    suspend fun validateToken(
        @Body request: DeviceTokenRequest
    ): PostResult

    @Headers("Content-Type: application/json")
    @POST("open-api/report")
    suspend fun submitUserReport(
        @Body request: UserReportRequest
    ): PostResult

    @Headers("Content-Type: application/json")
    @POST("open-api/topic")
    suspend fun submitTopicSubscriptionPreferences(
        @Body request: TopicSubscriptionPreferencesRequest
    ): PostResult

}