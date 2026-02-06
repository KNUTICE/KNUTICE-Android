package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.NoticeRemoteRepository
import com.doyoonkim.domain.util.MarkdownParser
import com.doyoonkim.model.MarkdownString
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.model.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface FetchNoticeById {
    operator fun invoke(nttId: Int): Flow<Result<NoticeVO>>
    suspend fun getNoticeSummary(nttId: Int): Result<List<MarkdownString>>
}

class FetchNoticeByIdImpl @Inject constructor(
    private val remoteRepository: NoticeRemoteRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FetchNoticeById {

    override operator fun invoke(nttId: Int) =
        remoteRepository.queryNoticeById(nttId).transform { result ->
            result?.let {
                emit(Result.success(it))
            } ?: emit(Result.failure(NoSuchElementException()))
        }.catch {
            /* Internal Error. */
            emit(Result.failure(it))
        }.flowOn(ioDispatcher)

    override suspend fun getNoticeSummary(nttId: Int): Result<List<MarkdownString>> {
        return remoteRepository.getNoticeSummary(nttId).runCatching {
            withContext(Dispatchers.Default) {
                async {
                    MarkdownParser.parseRawString(this@runCatching)
                }.await()
            }
        }
    }
}