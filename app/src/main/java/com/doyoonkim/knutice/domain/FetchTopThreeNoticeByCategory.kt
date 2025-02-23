package com.doyoonkim.knutice.domain

import com.doyoonkim.knutice.data.NoticeLocalRepository
import com.doyoonkim.knutice.model.Notice
import com.doyoonkim.knutice.model.NoticeCategory
import com.doyoonkim.knutice.model.RawNoticeData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class FetchTopThreeNoticeByCategory @Inject constructor (
    private val repository: NoticeLocalRepository
): FetchTopThreeNotice {

    override fun fetchTopThreeGeneralNotice(): Flow<TopThreeInCategory> {
        return flowOf(TopThreeInCategory(false))
    }

    override fun fetchTopThreeAcademicNotice(): Flow<TopThreeInCategory> {
        return flowOf(TopThreeInCategory(false))
    }

    override fun fetchTopThreeScholarshipNotice(): Flow<TopThreeInCategory> {
        return flowOf(TopThreeInCategory(false))
    }

    override fun fetchTopThreeEventNotice(): Flow<TopThreeInCategory> {
        return flowOf(TopThreeInCategory(false))
    }

    override fun fetchAllTopThreeNotice(): Flow<List<TopThreeInCategory>> {
        TODO("Not yet implemented")
    }

    fun getTopThreeNotices(category: NoticeCategory): Flow<TopThreeInCategory> {
        return repository.getTopThreeNotice(category).map {
            if (it.body.isNotEmpty()) {
                val result = it.body.toNotice()
                TopThreeInCategory(
                    isSuccessful = true,
                    notice1 = result[0],
                    notice2 = result[1],
                    notice3 = result[2]
                )
            } else {
                TopThreeInCategory(
                    isSuccessful = false
                )
            }
        }
    }

    private fun ArrayList<RawNoticeData>.toNotice(): List<Notice> {
        return List<Notice>(3) { index ->
            Notice(
                nttId = this[index].nttId ?: -1,
                title = this[index].title ?: "Unknown",
                url = this[index].contentUrl ?: "Unknown",
                departName = this[index].departName ?: "Unknown",
                timestamp = this[index].registeredAt ?: "Unknown",
                imageUrl = this[index].contentImage ?: "Unknown"
            )
        }
    }

}

data class TopThreeInCategory(
    val isSuccessful: Boolean,
    val notice1: Notice? = null,
    val notice2: Notice? = null,
    val notice3: Notice? = null
)