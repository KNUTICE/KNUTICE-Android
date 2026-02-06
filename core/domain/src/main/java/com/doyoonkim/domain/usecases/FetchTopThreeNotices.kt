package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.NoticeRemoteRepository
import com.doyoonkim.model.MajorCategory
import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.model.TopThreeNoticeVO
import com.doyoonkim.model.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import java.util.NoSuchElementException
import javax.inject.Inject

interface FetchTopThreeNotices {
    suspend operator fun invoke(): Result<TopThreeNoticeVO>
    suspend fun getMajorNotices(category: MajorCategory): Result<List<NoticeVO>>
}

class FetchTopThreeNoticesImpl @Inject constructor(
    private val remoteRepository: NoticeRemoteRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FetchTopThreeNotices {

    override suspend operator fun invoke(): Result<TopThreeNoticeVO> {
       return runCatching {
            remoteRepository.run {
                TopThreeNoticeVO(
                    general = queryTopThreeNotices(NoticeCategory.GENERAL_NEWS.name) ?: emptyList(),
                    scholarship = queryTopThreeNotices(NoticeCategory.SCHOLARSHIP_NEWS.name) ?: emptyList(),
                    event = queryTopThreeNotices(NoticeCategory.EVENT_NEWS.name) ?: emptyList(),
                    academic = queryTopThreeNotices(NoticeCategory.ACADEMIC_NEWS.name) ?: emptyList(),
                    employment = queryTopThreeNotices(NoticeCategory.EMPLOYMENT_NEWS.name) ?: emptyList()
                )
            }
        }
    }

    override suspend fun getMajorNotices(category: MajorCategory): Result<List<NoticeVO>> {
        return remoteRepository.queryTopThreeNotices(category.name)?.let {
            Result.success(it)
        } ?: Result.failure(NoSuchElementException("Unable to fetch the notices"))
    }

}