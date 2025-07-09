package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.SortOption
import com.doyoonkim.domain.interfaces.BookmarkLocalRepository
import com.doyoonkim.model.BookmarkAsListElementVO
import com.doyoonkim.model.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

interface FetchAllBookmarks {
    operator fun invoke(size: Int, pageNumber: Int, option: SortOption): Flow<BookmarkAsListElementVO>
}

class FetchAllBookmarksImpl @Inject constructor(
    private val localRepository: BookmarkLocalRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FetchAllBookmarks {

    override operator fun invoke(size: Int, pageNumber: Int, option: SortOption) =
        localRepository.queryBookmarkSorted(
            size, pageNumber, option
        ).transform { element ->
            element?.let {
                emit(it)
            }
        }.catch {
            /* Internal Error. Consume values, and never emit values. */
        }.flowOn(ioDispatcher)

}