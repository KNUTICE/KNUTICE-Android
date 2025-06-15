package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.RemoteRepository
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

interface FetchNoticesByKeyword {
    operator fun invoke (keyword: String): Flow<List<NoticeVO>>
}

class FetchNoticesByKeywordImpl @Inject constructor(
    private val remoteRepository: RemoteRepository
) : FetchNoticesByKeyword {

    override operator fun invoke(keyword: String) =
        remoteRepository.queryNoticesByKeyword(keyword).transform { result ->
            result?.let { emit(it) }
        }
}