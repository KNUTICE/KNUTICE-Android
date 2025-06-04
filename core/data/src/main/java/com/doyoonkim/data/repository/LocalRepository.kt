package com.doyoonkim.data.repository

import android.util.Log
import com.doyoonkim.data.model.Bookmark
import com.doyoonkim.data.model.NoticeEntity
import com.doyoonkim.data.room.LocalDatabase
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.flow.flow

class LocalRepository constructor(
    // Inject Local Database from the app module (planned)
    private val localSource: LocalDatabase
) {
    private val TAG = "LocalRepository"

    // CRUD
    fun createBookmark(bookmark: BookmarkVO) = flow {
        runCatching {
            localSource.getDao().createBookmark(bookmark.toBookmark())
        }.onFailure { throw it }.fold(
            onSuccess = { emit(true) },
            onFailure = { it.printLog().also { emit(false) } }
        )
    }

    fun createBookmark(bookmark: BookmarkVO, targetNotice: NoticeVO) = flow {
        runCatching {
            localSource.getDao().run {
                // Insert Notice Entity First
                createNoticeEntity(targetNotice.toNoticeEntity())
                // Insert Bookmark Entity next.
                this.createBookmark(bookmark.toBookmark())
            }
        }.onFailure { throw it }.fold(
            onSuccess = { emit(true) },
            onFailure = { it.printLog().also { emit(false) } }
        )
    }

    fun updateBookmark(bookmark: Bookmark) = flow {
        runCatching {
            localSource.getDao().updateBookmark(bookmark)
        }.onFailure { throw it }.fold(
            onSuccess = { emit(true) },
            onFailure = { it.printLog().also { emit(false) } }
        )
    }

    fun queryAllBookmarks() = flow {
        runCatching {
            localSource.getDao().getAllBookmarks()
        }.onFailure { throw it }.fold(
            onSuccess = { emit(it.toListOfBookmarkVO()) },
            onFailure = { it.printLog().also { emit(emptyList<BookmarkVO>()) } }
        )
    }

    fun queryNoticeById(nttId: Int) = flow {
        runCatching {
            localSource.getDao().getNoticeByNttId(nttId)
        }.onFailure { throw it }.fold(
            onSuccess = { emit(it.toNoticeVO()) },
            onFailure = { it.printLog().also { emit(null) } }
        )
    }

    fun queryBookmarkByNttId(nttId: Int) = flow {
        runCatching {
            localSource.getDao().getBookmarkByNttId(nttId)
                ?: throw NoSuchElementException("Bookmark Not Found")
        }.onFailure { throw it }.fold(
            onSuccess = {
                emit(it.toBookmarkVO())
            },
            onFailure = { emit(null) }
        )
    }

    fun requestBookmarkDeletion(bookmark: BookmarkVO) = flow {
        runCatching {
            localSource.getDao().deleteBookmark(bookmark.toBookmark())
        }.onFailure { throw it }.fold(
            onSuccess = { emit(true) },
            onFailure = { emit(false) }
        )
    }

    fun requestNoticeDeletion(notice: NoticeVO) = flow {
        runCatching {
            localSource.getDao().deleteNoticeEntity(notice.toNoticeEntity())
        }.onFailure { throw it }.fold(
            onSuccess = { emit(true) },
            onFailure = { emit(false) }
        )
    }


    private fun Throwable.printLog() =
        Log.d(TAG, "Failed to receive data\nREASON: ${this.stackTraceToString()}")

    private fun NoticeVO.toNoticeEntity() =
        NoticeEntity(
            noticeEntityId = 0,
            nttId = this.nttId,
            title = this.title,
            url = this.url,
            imageUrl = this.imageUrl ?: "",
            departName = this.departName,
            timestamp = this.timestamp
        )

    private fun NoticeEntity.toNoticeVO() =
        NoticeVO(
            entityId = this.noticeEntityId,
            nttId = this.nttId,
            title = this.title,
            url = this.url,
            imageUrl = this.imageUrl,
            departName = this.departName,
            timestamp = this.timestamp
        )

    private fun BookmarkVO.toBookmark() =
        Bookmark(
            bookmarkId = this.bookmarkId,
            nttId = this.targetNoticeNttId,
            isScheduled = this.isScheduled,
            reminderSchedule = this.reminderSchedule,
            note = this.bookmarkNote
        )

    private fun Bookmark.toBookmarkVO() =
        BookmarkVO(
            bookmarkId = this.bookmarkId,
            targetNoticeNttId = this.nttId,
            isScheduled = this.isScheduled,
            reminderSchedule = this.reminderSchedule,
            bookmarkNote = this.note
        )

    private fun List<Bookmark>.toListOfBookmarkVO() =
        this.map { it.toBookmarkVO() }
}