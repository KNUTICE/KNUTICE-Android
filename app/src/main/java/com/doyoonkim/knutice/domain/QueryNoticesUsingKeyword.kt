package com.doyoonkim.knutice.domain

import com.doyoonkim.knutice.data.NoticeLocalRepository
import com.doyoonkim.knutice.model.Notice
import com.doyoonkim.knutice.model.NoticeCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QueryNoticesUsingKeyword @Inject constructor(
    private val repository: NoticeLocalRepository
): FetchListOfNotices {

    override fun getNoticesPerPage(category: NoticeCategory, lastNttId: Int): Flow<List<Notice>> {
        TODO("Does not required to be implemented.")
    }

    override fun getNoticesByKeyword(keyword: String): Flow<List<Notice>> {
        return repository.queryNoticesByKeyword(keyword).map {
            it.body.toNotice()
        }
    }
}