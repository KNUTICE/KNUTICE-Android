package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.TipRemoteRepository
import com.doyoonkim.model.TipVO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

interface FetchTips {
    operator fun invoke(): Flow<List<TipVO>>
}

class FetchTipsImpl @Inject constructor(
    private val remoteRepository: TipRemoteRepository
) : FetchTips {
    override fun invoke() =
        remoteRepository.queryAllTips().transform { nullable ->
            nullable?.let {
                emit(it)
            }
        }.catch {
            /* INTERNAL ERROR, EMIT NOTHING */
        }.flowOn(Dispatchers.IO)
}