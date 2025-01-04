package com.doyoonkim.knutice.domain

import com.doyoonkim.knutice.model.Notice
import com.doyoonkim.knutice.model.NoticeCategory
import com.doyoonkim.knutice.model.RawNoticeData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface FetchListOfNotices {

    fun getNoticesPerPage(category: NoticeCategory, lastNttId: Int): Flow<List<Notice>>

    fun getNoticesByKeyword(keyword: String): Flow<List<Notice>>

    fun ArrayList<RawNoticeData>.toNotice(): List<Notice> {
        return List<Notice>(this.size) { index ->
            Notice(
                nttId = this[index].nttId ?: -1,
                title = this[index].title ?: "Unknown",
                url = this[index].contentUrl ?: "Unknown",
                imageUrl = this[index].contentImage ?: "Unknown",
                departName = this[index].departName ?: "Unknown",
                timestamp = this[index].registeredAt ?: "Unknown"
            )
        }
    }

}