package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.NoticeRemoteRepository
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.model.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

interface FetchNoticesByKeyword {
    operator fun invoke (keyword: String): Flow<List<NoticeVO>>
}

class FetchNoticesByKeywordImpl @Inject constructor(
    private val remoteRepository: NoticeRemoteRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FetchNoticesByKeyword {

    override operator fun invoke(keyword: String) =
        remoteRepository.queryNoticesByKeyword(keyword).transform { result ->
            result?.let { emit(it) }
        }.catch {
            /* Internal Error. Consume values, and never emit values. */
        }.flowOn(ioDispatcher)
}