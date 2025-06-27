package com.doyoonkim.domain

import com.doyoonkim.model.BookmarkAsListElementVO
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.flow.Flow

// Dependency Inversion
interface LocalRepository {
    fun createBookmark(bookmark: BookmarkVO): Flow<Boolean>

    fun createBookmark(bookmark: BookmarkVO, targetNotice: NoticeVO): Flow<Boolean>

    fun updateBookmark(bookmark: BookmarkVO): Flow<Boolean>

    fun queryAllBookmarks(size: Int, pageNumber: Int): Flow<BookmarkVO?>

    fun queryBookmarkSorted(size: Int, pageNumber: Int, option: SortOption): Flow<BookmarkAsListElementVO?>

    fun queryNoticeById(nttId: Int): Flow<NoticeVO?>

    fun queryBookmarkByNttId(nttId: Int): Flow<BookmarkVO?>

    fun requestBookmarkDeletion(bookmark: BookmarkVO): Flow<Boolean>

    fun requestNoticeDeletion(notice: NoticeVO): Flow<Boolean>
}