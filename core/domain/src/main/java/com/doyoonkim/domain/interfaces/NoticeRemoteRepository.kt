package com.doyoonkim.domain.interfaces

import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.model.TopThreeNoticeVO
import kotlinx.coroutines.flow.Flow

interface NoticeRemoteRepository {
    fun queryTopThreeNotices(): Flow<TopThreeNoticeVO?>

    fun queryNoticesPerPage(category: NoticeCategory, lastNttId: Int?): Flow<List<NoticeVO>?>

    fun queryNoticeById(nttId: Int): Flow<NoticeVO?>

    fun queryNoticesByKeyword(keyword: String): Flow<List<NoticeVO>?>
}