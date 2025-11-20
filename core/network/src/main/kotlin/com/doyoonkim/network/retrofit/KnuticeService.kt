package com.doyoonkim.network.retrofit

import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.network.model.FcmTokenSaveRequest
import com.doyoonkim.network.model.FcmTokenUpdateRequest
import com.doyoonkim.network.model.ReportSaveRequest
import com.doyoonkim.network.model.TopicUpdateRequest
import model.NoticeByIdResult
import model.NoticesByKeywordResult
import model.NoticesPerPageResult
import model.PatchResult
import model.PostResult
import model.TipResult
import model.TopThreeNoticeResults
import model.TopicSubscriptionPreferencesResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @author kimdoyoon
 * Created 6/2/25 at 10:58 PM
 */

interface KnuticeService {
    // To be removed.
    @GET("open-api/notices/latest")
    suspend fun getTopThreeNotices(): TopThreeNoticeResults

    @GET("open-api/v1/notices")
    suspend fun getNoticesPerPage(
        @Query("topic") category: NoticeCategory,
        @Query("size") size: Int,
        @Query("nttId") lastNttId: Int? = null
    ): NoticesPerPageResult

    @GET("open-api/v1/notices/{nttId}")
    suspend fun getNoticeById(
        @Path("nttId") nttId: String
    ): NoticeByIdResult

    @GET("open-api/v1/notices")
    suspend fun getNoticesByKeyword(
        @Query("keyword") keyword: String,
        @Query("nttId") lastNttId: Int? = null
    ): NoticesByKeywordResult

    @GET("open-api/v1/topics")
    suspend fun getTopicSubscriptionStatus(
        @Header("fcmToken") token: String,
        @Query("type") topicType: String
    ): TopicSubscriptionPreferencesResult

    @GET("open-api/v1/tips")
    suspend fun getAllTips(
        @Query("deviceType") deviceType: String
    ): TipResult

    @Headers("Content-Type: application/json")
    @POST("open-api/v1/fcm-tokens")
    suspend fun validateToken(
        @Header("fcmToken") token: String,
        @Body request: FcmTokenSaveRequest
    ): PostResult

    @Headers("Content-Type: application/json")
    @PATCH("open-api/v1/fcm-tokens")
    suspend fun updateFcmToken(
        @Header("fcmToken") token: String,
        @Body request: FcmTokenUpdateRequest
    ): PatchResult

    @Headers("Content-Type: application/json")
    @POST("open-api/v1/reports")
    suspend fun submitUserReport(
        @Header("fcmToken") token: String,
        @Body request: ReportSaveRequest
    ): PostResult

    @Headers("Content-Type: application/json")
    @PATCH("open-api/v1/topics")
    suspend fun submitTopicSubscriptionPreferences(
        @Header("fcmToken") token: String,
        @Query("type") type: String,
        @Body request: TopicUpdateRequest
    ): PostResult
}