package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.RemoteRepository
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

interface FetchNoticeById {
    operator fun invoke(nttId: Int): Flow<NoticeVO>
}

class FetchNoticeByIdImpl @Inject constructor(
    private val remoteRepository: RemoteRepository
) : FetchNoticeById {

    override operator fun invoke(nttId: Int) =
        remoteRepository.queryNoticeById(nttId).transform { result ->
            result?.let { emit(it) }
        }.catch {
            /* Internal Error. Consume values, and never emit values. */
        }
}