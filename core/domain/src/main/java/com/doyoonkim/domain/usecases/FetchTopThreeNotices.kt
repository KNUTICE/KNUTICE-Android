package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.NoticeRemoteRepository
import com.doyoonkim.model.TopThreeNoticeVO
import com.doyoonkim.model.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

interface FetchTopThreeNotices {
    operator fun invoke(): Flow<Result<TopThreeNoticeVO>>
}

class FetchTopThreeNoticesImpl @Inject constructor(
    private val remoteRepository: NoticeRemoteRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FetchTopThreeNotices {

    override operator fun invoke() =
        remoteRepository.queryTopThreeNotices().transform { result ->
            result?.let {
                emit(Result.success(it))
            } ?: emit(Result.failure(NoSuchElementException()))
        }.catch {
            /* Internal Error. */
            emit(Result.failure(it))
        }.flowOn(ioDispatcher)

}