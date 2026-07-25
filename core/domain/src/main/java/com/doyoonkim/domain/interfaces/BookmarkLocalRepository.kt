package com.doyoonkim.domain.interfaces

import com.doyoonkim.domain.SortOption
import com.doyoonkim.model.BookmarkAsListElementVO
import com.doyoonkim.model.BookmarkFtsTargetVO
import com.doyoonkim.model.BookmarkFtsVO
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.model.PendingBookmarkFtsVO
import kotlinx.coroutines.flow.Flow

interface BookmarkLocalRepository {
    suspend fun createBookmark(bookmark: BookmarkVO, targetNotice: NoticeVO, pending: PendingBookmarkFtsVO): Boolean

    suspend fun updateBookmark(bookmark: BookmarkVO, pending: PendingBookmarkFtsVO? = null): Boolean

    fun queryAllBookmarks(): Flow<List<BookmarkVO>?>

    fun queryBookmarkFtsTarget(): Flow<List<BookmarkFtsTargetVO>?>

    fun queryBookmarkSorted(size: Int, pageNumber: Int, option: SortOption): Flow<List<BookmarkAsListElementVO>?>

    fun queryBookmarkByNttId(nttId: Int): Flow<BookmarkVO?>

    fun queryBookmarkByKeyword(keyword: String, size: Int, pageNumber: Int): Flow<List<BookmarkAsListElementVO>?>

    suspend fun requestBookmarkDeletion(bookmark: BookmarkVO, targetNotice: NoticeVO, pending: PendingBookmarkFtsVO): Boolean

    // FTS
    fun createBookmarkFts(ftsEntry: BookmarkFtsVO): Flow<Boolean>

    fun updateBookmarkFts(ftsEntry: BookmarkFtsVO): Flow<Boolean>

    fun deleteBookmarkFts(ftsEntry: BookmarkFtsVO): Flow<Boolean>

    // Pending FTS Async
    suspend fun queryPendingBookmarkFtsBatched(limit: Int): List<PendingBookmarkFtsVO>

    fun removePendingBookmarkFtsEntry(stagingId: List<Int>): Flow<Boolean>
}
