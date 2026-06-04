package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.NoticeRemoteRepository
import com.doyoonkim.model.MajorCategory
import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.model.TopThreeNoticeVO
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.NoSuchElementException
import javax.inject.Inject

interface FetchTopThreeNotices {
    suspend operator fun invoke(): Result<TopThreeNoticeVO>
    suspend fun getMajorNotices(category: MajorCategory): Result<List<NoticeVO>>
}

class FetchTopThreeNoticesImpl @Inject constructor(
    private val remoteRepository: NoticeRemoteRepository,
    private val checkRecentNoticeUseCase: CheckRecentNotice
) : FetchTopThreeNotices {

    override suspend operator fun invoke(): Result<TopThreeNoticeVO> {
       return runCatching {
            remoteRepository.run {
                coroutineScope {
                    val general = async {
                        queryTopThreeNotices(NoticeCategory.GENERAL_NEWS.name)
                    }
                    val scholarship = async {
                        queryTopThreeNotices(NoticeCategory.SCHOLARSHIP_NEWS.name)
                    }
                    val academic = async {
                        queryTopThreeNotices(NoticeCategory.ACADEMIC_NEWS.name)
                    }
                    val event = async {
                        queryTopThreeNotices(NoticeCategory.EVENT_NEWS.name)
                    }
                    val employment = async {
                        queryTopThreeNotices(NoticeCategory.EMPLOYMENT_NEWS.name)
                    }

                    TopThreeNoticeVO(
                        general = checkRecentNoticeUseCase(general.await() ?: emptyList()),
                        academic = checkRecentNoticeUseCase(academic.await() ?: emptyList()),
                        scholarship = checkRecentNoticeUseCase(scholarship.await() ?: emptyList()),
                        event = checkRecentNoticeUseCase(event.await() ?: emptyList()),
                        employment = checkRecentNoticeUseCase(employment.await() ?: emptyList())
                    )
                }
            }
        }
    }

    override suspend fun getMajorNotices(category: MajorCategory): Result<List<NoticeVO>> {
        return remoteRepository.queryTopThreeNotices(category.name)?.let {
            Result.success(it)
        } ?: Result.failure(NoSuchElementException("Unable to fetch the notices"))
    }

}