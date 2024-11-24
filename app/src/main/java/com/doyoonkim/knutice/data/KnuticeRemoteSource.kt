package com.doyoonkim.knutice.data

import android.util.Log
import com.doyoonkim.knutice.model.ApiDeviceTokenRequest
import com.doyoonkim.knutice.model.DeviceTokenRequest
import com.doyoonkim.knutice.model.NoticeCategory
import com.doyoonkim.knutice.model.NoticesPerPage
import com.doyoonkim.knutice.model.TopThreeNotices
import com.doyoonkim.knutice.model.ValidateTokenResult
import com.doyoonkim.knutice.BuildConfig
import com.doyoonkim.knutice.model.Notice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import javax.inject.Inject


class KnuticeRemoteSource @Inject constructor() {

    private val knuticeService = Retrofit.Builder()
        .baseUrl(BuildConfig.API_ROOT)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

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

    suspend fun getFullNoticeContent(url: String): Deferred<String> =
        CoroutineScope(Dispatchers.IO).async {
            Jsoup.connect(url)
                .get()
                .getElementsByClass("bbs-view-content bbs-view-content-skin05")
                .text() ?: "Unable to receive full notice content"
        }

    fun validateToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                knuticeService.create(KnuticeService::class.java).validateToken(
                    ApiDeviceTokenRequest(body = DeviceTokenRequest(token))
                ).run {
                    if (this.result?.resultCode == 200) Log.d("KnuticeServer", "Token saved.")
                    else Log.d("KnuticeServer", "Failed to save token")
                }
            } catch (e: Exception) {
                Log.d("KnuticeServer", "Failed to validate token\nREASON:${e.message}")
            }
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

    @Headers("Content-Type: application/json")
    @POST("/open-api/token")
    suspend fun validateToken(
        @Body requestBody: ApiDeviceTokenRequest
    ): ValidateTokenResult

}
