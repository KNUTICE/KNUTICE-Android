package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.LocalRepository
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

interface FetchNoticeByIdFromLocal {
    operator fun invoke(nttId: Int): Flow<NoticeVO>
}

class FetchNoticeByIdFromLocalImpl @Inject constructor(
    private val localRepository: LocalRepository
) : FetchNoticeByIdFromLocal {

    override operator fun invoke(nttId: Int) =
        localRepository.queryNoticeById(nttId).transform { result ->
            result?.let { emit(it) }
        }

}