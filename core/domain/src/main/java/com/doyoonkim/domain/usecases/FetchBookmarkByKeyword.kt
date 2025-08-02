package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.BookmarkLocalRepository
import com.doyoonkim.model.BookmarkAsListElementVO
import com.doyoonkim.model.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

interface FetchBookmarkByKeyword {
    operator fun invoke(keyword: String, size: Int, pageNumber: Int): Flow<Result<List<BookmarkAsListElementVO>>>
}

class FetchBookmarkByKeywordImpl @Inject constructor(
    private val bookmarkLocalRepository: BookmarkLocalRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FetchBookmarkByKeyword {
    override fun invoke(keyword: String, size: Int, pageNumber: Int) =
        bookmarkLocalRepository.queryBookmarkByKeyword(
            keyword, size, pageNumber
        ).transform { result ->
            result?.let {
                emit(Result.success(it))
            } ?: emit(Result.failure(NoSuchElementException()))
        }.catch { e ->
            emit(Result.failure(e))
        }.flowOn(ioDispatcher)
}