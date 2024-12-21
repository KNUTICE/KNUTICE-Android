package com.doyoonkim.knutice.domain

import com.doyoonkim.knutice.data.NoticeLocalRepository
import com.doyoonkim.knutice.model.Notice
import com.doyoonkim.knutice.model.NoticeCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FetchNoticesPerPageInCategory @Inject constructor(
    private val repository: NoticeLocalRepository
) : FetchListOfNotices {

    override fun getNoticesPerPage(category: NoticeCategory, lastNttId: Int): Flow<List<Notice>> {
        return repository.getNoticesByCategoryPerPage(category, lastNttId).map {
            it.body.toNotice()
        }
    }

    override fun getNoticesByKeyword(keyword: String): Flow<List<Notice>> {
        TODO("Does not required to be implemented.")
    }

}