package com.doyoonkim.domain.interfaces

import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.flow.Flow

interface NoticeRemoteRepository {
    suspend fun queryTopThreeNotices(category: String): List<NoticeVO>?

    fun queryNoticesPerPage(category: String, lastNttId: Int?): Flow<List<NoticeVO>?>

    fun queryNoticeById(nttId: Int): Flow<NoticeVO?>

    fun queryNoticesByKeyword(keyword: String, lastNttId: Int?): Flow<List<NoticeVO>?>

    suspend fun getNoticeSummary(nttId: Int): String?
}