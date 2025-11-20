package com.doyoonkim.domain.interfaces

import com.doyoonkim.domain.SortOption
import com.doyoonkim.model.BookmarkAsListElementVO
import com.doyoonkim.model.BookmarkFtsTargetVO
import com.doyoonkim.model.BookmarkFtsVO
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.flow.Flow

interface BookmarkLocalRepository {
    fun createBookmark(bookmark: BookmarkVO): Flow<Boolean>

    fun createBookmark(bookmark: BookmarkVO, targetNotice: NoticeVO): Flow<Boolean>

    fun updateBookmark(bookmark: BookmarkVO): Flow<Boolean>

    fun queryAllBookmarks(): Flow<List<BookmarkVO>?>

    fun queryBookmarkFtsTarget(): Flow<List<BookmarkFtsTargetVO>?>

    fun queryBookmarkSorted(size: Int, pageNumber: Int, option: SortOption): Flow<List<BookmarkAsListElementVO>?>

    fun queryBookmarkByNttId(nttId: Int): Flow<BookmarkVO?>

    fun queryBookmarkByKeyword(keyword: String, size: Int, pageNumber: Int): Flow<List<BookmarkAsListElementVO>?>

    fun requestBookmarkDeletion(bookmark: BookmarkVO): Flow<Boolean>

    // FTS
    fun createBookmarkFts(ftsEntry: BookmarkFtsVO): Flow<Boolean>

    fun updateBookmarkFts(ftsEntry: BookmarkFtsVO): Flow<Boolean>

    fun deleteBookmarkFts(ftsEntry: BookmarkFtsVO): Flow<Boolean>
}