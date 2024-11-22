package com.doyoonkim.knutice.domain

import com.doyoonkim.knutice.data.NoticeLocalRepository
import com.doyoonkim.knutice.model.Notice
import com.doyoonkim.knutice.model.RawNoticeData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class FetchTopThreeNoticeByCategory @Inject constructor (
    private val repository: NoticeLocalRepository
): FetchTopThreeNotice {

    override fun fetchTopThreeGeneralNotice(): Flow<TopThreeInCategory> {
        return repository.getTopThreeNotice().map {
            if (it.body != null) {
                val latestGeneralNotices = it.body!!.latestThreeGeneralNews.toNotice()
                TopThreeInCategory(
                    isSuccessful = true,
                    notice1 = latestGeneralNotices[0],
                    notice2 = latestGeneralNotices[1],
                    notice3 = latestGeneralNotices[2]
                )
            } else {
                TopThreeInCategory(
                    isSuccessful = false
                )
            }
        }
    }

    override fun fetchTopThreeAcademicNotice(): Flow<TopThreeInCategory> {
        return repository.getTopThreeNotice().map {
            if (it.body != null) {
                val latestAcademicNotices = it.body!!.latestThreeAcademicNews.toNotice()
                TopThreeInCategory(
                    isSuccessful = true,
                    notice1 = latestAcademicNotices[0],
                    notice2 = latestAcademicNotices[1],
                    notice3 = latestAcademicNotices[2]
                )
            } else {
                TopThreeInCategory(
                    isSuccessful = false
                )
            }
        }
    }

    override fun fetchTopThreeScholarshipNotice(): Flow<TopThreeInCategory> {
        return repository.getTopThreeNotice().map {
            if (it.body != null) {
                val latestScholarshipNotices = it.body!!.latestThreeScholarshipNews.toNotice()
                TopThreeInCategory(
                    isSuccessful = true,
                    notice1 = latestScholarshipNotices[0],
                    notice2 = latestScholarshipNotices[1],
                    notice3 = latestScholarshipNotices[2]
                )
            } else {
                TopThreeInCategory(
                    isSuccessful = false
                )
            }
        }
    }

    override fun fetchTopThreeEventNotice(): Flow<TopThreeInCategory> {
        return repository.getTopThreeNotice().map {
            if (it.body != null) {
                val latestEventNotices = it.body!!.latestThreeEventNews.toNotice()
                TopThreeInCategory(
                    isSuccessful = true,
                    notice1 = latestEventNotices[0],
                    notice2 = latestEventNotices[1],
                    notice3 = latestEventNotices[2]
                )
            } else {
                TopThreeInCategory(
                    isSuccessful = false
                )
            }
        }
    }

    override fun fetchAllTopThreeNotice(): Flow<List<TopThreeInCategory>> {
        TODO("Not yet implemented")
    }

    private fun ArrayList<RawNoticeData>.toNotice(): List<Notice> {
        return List<Notice>(3) { index ->
            Notice(
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