package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.NoticeRemoteRepository
import com.doyoonkim.model.TopThreeNoticeVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

interface FetchTopThreeNotices {
    operator fun invoke(): Flow<TopThreeNoticeVO>
}

class FetchTopThreeNoticesImpl @Inject constructor(
    private val remoteRepository: NoticeRemoteRepository
) : FetchTopThreeNotices {

    override operator fun invoke(): Flow<TopThreeNoticeVO> =
        remoteRepository.queryTopThreeNotices().transform { result ->
            result?.let { emit(it) }
        }.catch {
            /* Internal Error. Consume values, and never emit values. */
        }

}