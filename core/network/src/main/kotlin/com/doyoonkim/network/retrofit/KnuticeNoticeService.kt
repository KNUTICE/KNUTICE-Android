package com.doyoonkim.network.retrofit

import model.NoticeByIdResult
import model.NoticeSummaryResult
import model.NoticesPerPageResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val entry = "open-api"
private const val version = "v2"
private const val service = "notices"
private const val path = "$entry/$version/$service"

interface KnuticeNoticeService {

    @GET(path)
    suspend fun getNoticesPerPage(
        @Query("topicId") topicId: Int,
        @Query("keyword") keyword: String? = null,
        @Query("size") size: Int,
        @Query("nttId") lastNttId: Int? = null
    ): NoticesPerPageResult

    @GET("$path/{nttId}")
    suspend fun getNoticeById(
        @Path("nttId") nttId: Int
    ): NoticeByIdResult

    @GET("$entry/v1/$service/summary/{nttId}")
    suspend fun getNoticesSummary(
        @Path("nttId") nttId: Int
    ): NoticeSummaryResult
}
