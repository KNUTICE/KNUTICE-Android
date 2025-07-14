package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.TipRemoteRepository
import com.doyoonkim.model.TipVO
import com.doyoonkim.model.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

interface FetchTips {
    operator fun invoke(): Flow<Result<List<TipVO>>>
}

class FetchTipsImpl @Inject constructor(
    private val remoteRepository: TipRemoteRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FetchTips {
    override fun invoke() =
        remoteRepository.queryAllTips().transform { nullable ->
            nullable?.let {
                emit(Result.success(it))
            } ?: emit(Result.failure(NoSuchElementException()))
        }.catch {
            /* INTERNAL ERROR, EMIT NOTHING */
        }.flowOn(ioDispatcher)
}