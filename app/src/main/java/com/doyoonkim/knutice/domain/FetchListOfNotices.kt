package com.doyoonkim.knutice.domain

import com.doyoonkim.knutice.model.Notice
import com.doyoonkim.knutice.model.NoticeCategory
import kotlinx.coroutines.flow.Flow

interface FetchListOfNotices {

    fun getNoticesPerPage(category: NoticeCategory, lastNttId: Int): Flow<List<Notice>>

}