package com.doyoonkim.knutice.model

import android.util.Log
import com.example.knutice.BuildConfig
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import javax.inject.Inject

class NoticeRemoteRepository @Inject constructor() {
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_ROOT)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getTopThreeNotice() = callbackFlow<TopThreeNotices?> {
        Log.d("Remote Source", "Start retrofit service")
        val service = retrofit.create(KnuticeService::class.java)
        val response = service.getTopThreeNotice()

        if (response.result?.resultCode == 200) {
            trySend(response)
        } else {
            trySend(null)
        }

        awaitClose {  }     // Mandatory due to any potential memory leak.
    }

}

interface KnuticeService {

    @GET("/open-api/notice")
    suspend fun getTopThreeNotice(): TopThreeNotices

}

// POJO for receiving raw data from the server.
data class TopThreeNotices(
    @SerializedName("result" ) var result : Result? = Result(),
    @SerializedName("body"   ) var body   : Body?   = Body()
) {
    data class Result(
        @SerializedName("resultCode"        ) var resultCode        : Int?    = null,
        @SerializedName("resultMessage"     ) var resultMessage     : String? = null,
        @SerializedName("resultDescription" ) var resultDescription : String? = null
    )

    data class Body(
        @SerializedName("latestThreeGeneralNews"     ) var latestThreeGeneralNews     : ArrayList<LatestThreeNews>     = arrayListOf(),
        @SerializedName("latestThreeScholarshipNews" ) var latestThreeScholarshipNews : ArrayList<LatestThreeNews> = arrayListOf(),
        @SerializedName("latestThreeEventNews"       ) var latestThreeEventNews       : ArrayList<LatestThreeNews>       = arrayListOf(),
        @SerializedName("latestThreeAcademicNews"    ) var latestThreeAcademicNews    : ArrayList<LatestThreeNews>    = arrayListOf()
    ) {
        data class LatestThreeNews (
            @SerializedName("nttId"        ) var nttId        : Int?    = null,
            @SerializedName("title"        ) var title        : String? = null,
            @SerializedName("contentUrl"   ) var contentUrl   : String? = null,
            @SerializedName("departName"   ) var departName   : String? = null,
            @SerializedName("registeredAt" ) var registeredAt : String? = null
        )

//        data class LatestThreeScholarshipNews (
//            @SerializedName("nttId"        ) var nttId        : Int?    = null,
//            @SerializedName("title"        ) var title        : String? = null,
//            @SerializedName("contentUrl"   ) var contentUrl   : String? = null,
//            @SerializedName("departName"   ) var departName   : String? = null,
//            @SerializedName("registeredAt" ) var registeredAt : String? = null
//        )
//
//        data class LatestThreeEventNews (
//            @SerializedName("nttId"        ) var nttId        : Int?    = null,
//            @SerializedName("title"        ) var title        : String? = null,
//            @SerializedName("contentUrl"   ) var contentUrl   : String? = null,
//            @SerializedName("departName"   ) var departName   : String? = null,
//            @SerializedName("registeredAt" ) var registeredAt : String? = null
//        )
//
//        data class LatestThreeAcademicNews (
//            @SerializedName("nttId"        ) var nttId        : Int?    = null,
//            @SerializedName("title"        ) var title        : String? = null,
//            @SerializedName("contentUrl"   ) var contentUrl   : String? = null,
//            @SerializedName("departName"   ) var departName   : String? = null,
//            @SerializedName("registeredAt" ) var registeredAt : String? = null
//        )
    }
}
