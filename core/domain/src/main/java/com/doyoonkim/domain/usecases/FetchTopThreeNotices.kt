package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.NoticeRemoteRepository
import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.model.TopThreeNoticeVO
import com.doyoonkim.model.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

interface FetchTopThreeNotices {
    suspend operator fun invoke(): Result<TopThreeNoticeVO>
}

class FetchTopThreeNoticesImpl @Inject constructor(
    private val remoteRepository: NoticeRemoteRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FetchTopThreeNotices {

    override suspend operator fun invoke(): Result<TopThreeNoticeVO> {
       return runCatching {
            remoteRepository.run {
                TopThreeNoticeVO(
                    general = queryTopThreeNotices(NoticeCategory.GENERAL_NEWS) ?: emptyList(),
                    scholarship = queryTopThreeNotices(NoticeCategory.SCHOLARSHIP_NEWS) ?: emptyList(),
                    event = queryTopThreeNotices(NoticeCategory.EVENT_NEWS) ?: emptyList(),
                    academic = queryTopThreeNotices(NoticeCategory.ACADEMIC_NEWS) ?: emptyList(),
                    employment = queryTopThreeNotices(NoticeCategory.EMPLOYMENT_NEWS) ?: emptyList()
                )
            }
        }
    }

}