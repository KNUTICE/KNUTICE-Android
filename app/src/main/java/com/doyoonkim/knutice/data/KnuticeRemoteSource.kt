package com.doyoonkim.knutice.data

import android.util.Log
import com.doyoonkim.knutice.model.ApiDeviceTokenRequest
import com.doyoonkim.knutice.model.DeviceTokenRequest
import com.doyoonkim.knutice.model.NoticeCategory
import com.doyoonkim.knutice.model.NoticesPerPage
import com.doyoonkim.knutice.model.TopThreeNotices
import com.doyoonkim.knutice.model.ApiPostResult
import com.doyoonkim.knutice.BuildConfig
import com.doyoonkim.knutice.model.ApiReportRequest
import com.doyoonkim.knutice.model.ApiTopicSubscriptionRequest
import com.doyoonkim.knutice.model.ManageTopicRequest
import com.doyoonkim.knutice.model.ReportRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.jsoup.Jsoup
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KnuticeRemoteSource @Inject constructor() {

    private val knuticeService = Retrofit.Builder()
        .baseUrl(BuildConfig.API_ROOT)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // TODO: Should relocate this variable.
    private var validatedToken: String = ""

    suspend fun getTopThreeNotice(category: NoticeCategory, size: Int): NoticesPerPage {
        return knuticeService.create(KnuticeService::class.java).run {
            this.getTopThreeNotice(category, size)
        }
    }

    suspend fun getNoticeListPerPage(category: NoticeCategory, lastNttId: Int): NoticesPerPage {
        Log.d("KnuticeRemoteSource", "Start retrofit service")
        return knuticeService.create(KnuticeService::class.java).run {
            if (lastNttId == 0) {
                this.getFirstPageOfNotice(category)
            } else {
                this.getNoticeListPerPage(category, lastNttId)
            }
        }
    }

    suspend fun queryNoticesByKeyword(keyword: String): NoticesPerPage {
        Log.d("KnuticeRemoteSource", "Start retrofit service (Querying Notices...)")
        return knuticeService.create(KnuticeService::class.java).queryNoticeByKeyword(keyword)
    }

    suspend fun getFullNoticeContent(url: String): Deferred<String> =
        CoroutineScope(Dispatchers.IO).async {
            Jsoup.connect(url)
                .get()
                .getElementsByClass("bbs-view-content bbs-view-content-skin05")
                .text() ?: "Unable to receive full notice content"
        }

    suspend fun validateToken(token: String): Result<Boolean> {
        Log.d("KnuticeRemoteSource", "Token Provided: $token")
        try {
            knuticeService.create(KnuticeService::class.java).validateToken(
                ApiDeviceTokenRequest(body = DeviceTokenRequest(token))
            ).run {
                if (this.result?.resultCode == 200) {
                    Log.d("KnuticeServer", "Token saved.")
                    validatedToken = token
                    return Result.success(true)
                } else {
                    Log.d("KnuticeServer", "Failed to save token")
                    return Result.success(false)
                }
            }
        } catch (e: Exception) {
            Log.d("KnuticeServer", "Failed to validate token\nREASON:${e.message}")
            return Result.failure(e)
        }
    }

    suspend fun submitUserReport(report: ReportRequest): Result<Boolean> {
        Log.d("KnuticeRemoteSource", "ValidatedToken: $validatedToken")
        try {
            knuticeService.create(KnuticeService::class.java).submitUserReport(
                ApiReportRequest(body = report.copy(token = validatedToken))
            ).run {
                if (this.result?.resultCode == 200) {
                    Log.d("KnuticeServer", "User report has been submitted successfully.\n${this.body?.message}")
                    return Result.success(true)
                } else {
                    Log.d("KnuticeServer", "Failed to submit user report\n${this.body?.message}")
                    return Result.success(false)
                }
            }
        } catch (e: Exception) {
            Log.d("KnuticeServer", "Failed to submit user report.\nREASON: ${e.message}")
            return Result.failure(e)
        }
    }

    suspend fun submitTopicSubscriptionPreference(topic: NoticeCategory, status: Boolean): Result<Boolean> {
        Log.d("KnuticeRemoteSource", "Update Topic Subscription Preference")
        try {
            knuticeService.create(KnuticeService::class.java).submitTopicSubscriptionPreference(
                ApiTopicSubscriptionRequest(
                    body = ManageTopicRequest(validatedToken, topic.name, status)
                )
            ).run {
                if (this.result?.resultCode == 200) {
                    Log.d("KnuticeServer", "Topic preference has been updated.\n${this.body}")
                    return Result.success(true)
                } else {
                    Log.d("KnuticeServer", "Failed to update topic preference.\n${this.body?.message}")
                    return Result.success(false)
                }
            }
        } catch (e: Exception) {
            Log.d("KnuticeServer", "Failed to submit user report. \nREASON: ${e.message}")
            return Result.failure(e)
        }
    }

}

interface KnuticeService {

    @GET("/open-api/notice")
    suspend fun getTopThreeNotice(): TopThreeNotices

    @GET("/open-api/notice/list")
    suspend fun getTopThreeNotice(
        @Query("noticeName") category: NoticeCategory,
        @Query("size") size: Int
    ): NoticesPerPage

    @GET("/open-api/notice/list")
    suspend fun getNoticeListPerPage(
        @Query("noticeName") category: NoticeCategory,
        @Query("nttId") lastNttId: Int
    ): NoticesPerPage

    @GET("/open-api/notice/list")
    suspend fun getFirstPageOfNotice(
        @Query("noticeName") category: NoticeCategory
    ): NoticesPerPage

    @GET("/open-api/search")
    suspend fun queryNoticeByKeyword(
        @Query("keyword") keyword: String
    ): NoticesPerPage

    @Headers("Content-Type: application/json")
    @POST("/open-api/token")
    suspend fun validateToken(
        @Body requestBody: ApiDeviceTokenRequest
    ): ApiPostResult

    @Headers("Content-Type: application/json")
    @POST("/open-api/report")
    suspend fun submitUserReport(
        @Body requestBody: ApiReportRequest
    ): ApiPostResult

    @Headers("Content-Type: application/json")
    @POST("/open-api/token/topic")
    suspend fun submitTopicSubscriptionPreference(
        @Body requestBody: ApiTopicSubscriptionRequest
    ): ApiPostResult

}
