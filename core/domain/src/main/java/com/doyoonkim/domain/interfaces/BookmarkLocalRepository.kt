package com.doyoonkim.domain.interfaces

import com.doyoonkim.domain.SortOption
import com.doyoonkim.model.BookmarkAsListElementVO
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.flow.Flow

interface BookmarkLocalRepository {
    fun createBookmark(bookmark: BookmarkVO): Flow<Boolean>

    fun createBookmark(bookmark: BookmarkVO, targetNotice: NoticeVO): Flow<Boolean>

    fun updateBookmark(bookmark: BookmarkVO): Flow<Boolean>

    fun queryAllBookmarks(): Flow<List<BookmarkVO>?>

    fun queryBookmarkSorted(size: Int, pageNumber: Int, option: SortOption): Flow<List<BookmarkAsListElementVO>?>

    fun queryBookmarkByNttId(nttId: Int): Flow<BookmarkVO?>

    fun requestBookmarkDeletion(bookmark: BookmarkVO): Flow<Boolean>
}