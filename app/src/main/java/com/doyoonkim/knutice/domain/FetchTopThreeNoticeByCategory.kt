package com.doyoonkim.knutice.domain

import android.util.Log
import com.doyoonkim.knutice.model.NoticeRemoteRepository
import com.doyoonkim.knutice.model.TopThreeNotices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class FetchTopThreeNoticeByCategory @Inject constructor (
    private val remoteRepository: NoticeRemoteRepository
) {

    private suspend fun fetchTopThreeNotices(category: NoticeCategory): Flow<TopThreeInCategory> {
        Log.d("FetchTopThreeNoticeByCategory", "Start fetching")
        return remoteRepository.getTopThreeNotice().map { response ->
            if (response?.body != null) {
                val result = response.body
                val noticeList =  when (category) {
                    NoticeCategory.GENERAL -> {
                        result!!.latestThreeGeneralNews.toNotice()
                    }
                    NoticeCategory.ACADEMIC -> {
                        result!!.latestThreeAcademicNews.toNotice()
                    }
                    NoticeCategory.SCHOLARSHIP -> {
                        result!!.latestThreeScholarshipNews.toNotice()
                    }
                    NoticeCategory.EVENT -> {
                        result!!.latestThreeEventNews.toNotice()
                    }
                }

                TopThreeInCategory(
                    isSuccessful = true,
                    notice1 = noticeList[0],
                    notice2 = noticeList[1],
                    notice3 = noticeList[2]
                )

            } else {
                TopThreeInCategory(
                    isSuccessful = false
                )
            }

        }
    }

    private fun ArrayList<TopThreeNotices.Body.LatestThreeNews>.toNotice(): List<Notice> {
        return List<Notice>(3) { index ->
            Notice(
                title = this[index].title ?: "",
                url = this[index].contentUrl ?: "",
                departName = this[index].departName ?: "",
                timestamp = this[index].registeredAt ?: ""
            )
        }
    }

    suspend operator fun invoke(category: NoticeCategory) = fetchTopThreeNotices(category)

}

enum class NoticeCategory { GENERAL, ACADEMIC, SCHOLARSHIP, EVENT }

data class TopThreeInCategory(
    val isSuccessful: Boolean,
    val notice1: Notice? = null,
    val notice2: Notice? = null,
    val notice3: Notice? = null
)

data class Notice(
    val title: String = "",
    val url: String = "",
    val departName: String = "",
    val timestamp: String = ""
)