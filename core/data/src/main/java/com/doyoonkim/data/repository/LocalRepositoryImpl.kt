package com.doyoonkim.data.repository

import android.util.Log
import com.doyoonkim.data.model.Bookmark
import com.doyoonkim.data.model.BookmarkAsListElement
import com.doyoonkim.data.model.NoticeEntity
import com.doyoonkim.data.room.MainDatabaseDao
import com.doyoonkim.domain.SortOption
import com.doyoonkim.domain.interfaces.BookmarkLocalRepository
import com.doyoonkim.domain.interfaces.NoticeLocalRepository
import com.doyoonkim.model.BookmarkAsListElementVO
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    // Inject Local Database from the app module (planned)
    private val localDao: MainDatabaseDao
) : BookmarkLocalRepository, NoticeLocalRepository {
    private val TAG = "LocalRepositoryImpl"

    // CRUD
    override fun createBookmark(bookmark: BookmarkVO) = flow {
        runCatching {
            localDao.createBookmark(bookmark.toBookmark())
        }.onFailure { throw it }.fold(
            onSuccess = { emit(true) },
            onFailure = { it.printLog().also { emit(false) } }
        )
    }

    override fun createBookmark(bookmark: BookmarkVO, targetNotice: NoticeVO) = flow {
        runCatching {
            localDao.run {
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

    override fun updateBookmark(bookmark: BookmarkVO) = flow {
        runCatching {
            localDao.updateBookmark(bookmark.toBookmark())
        }.onFailure { throw it }.fold(
            onSuccess = { emit(true) },
            onFailure = { it.printLog().also { emit(false) } }
        )
    }

    override fun updateNoticeEntity(notice: NoticeVO) = flow {
        runCatching {
            localDao.updateNoticeEntity(notice.toNoticeEntity())
        }.onFailure { throw it }.fold(
            onSuccess = { emit(true) },
            onFailure = { it.printLog().also { emit(false) } }
        )
    }

    override fun queryAllBookmarks() = flow {
        runCatching {
            localDao.getAllBookmarks()
        }.onFailure { throw it }.fold(
            onSuccess = {
                Log.d(TAG, "${it.size}")
                emit(it.toListOfBookmarkVO())
            },
            onFailure = { it.printLog().also { emit(null) } }
        )
    }

    override fun queryBookmarkSorted(size: Int, pageNumber: Int, option: SortOption) = flow {
        runCatching {
            when (option) {
                SortOption.ASC_CREATION -> localDao.getBookmarkListSortedNewest(size, pageNumber)
                SortOption.DES_CREATION -> localDao.getBookmarkListSortedOldest(size, pageNumber)
            }
        }.onFailure { throw it }.fold(
            onSuccess = { dto ->
                Log.d(TAG, "${dto.size}")
                emit(dto.map { it.toVO() })
            },
            onFailure = { it.printLog().also { emit(null) } }
        )
    }

    override fun queryNoticeById(nttId: Int) = flow {
        runCatching {
            localDao.getNoticeByNttId(nttId)
        }.onFailure { throw it }.fold(
            onSuccess = {
                if (it != null) emit(it.toNoticeVO())
                else emit(null)
            },
            onFailure = { it.printLog().also { emit(null) } }
        )
    }

    override fun queryBookmarkByNttId(nttId: Int) = flow {
        runCatching {
            localDao.getBookmarkByNttId(nttId)
        }.onFailure { throw it }.fold(
            onSuccess = {
                emit(it?.toBookmarkVO())
            },
            onFailure = { emit(null) }
        )
    }

    override fun requestBookmarkDeletion(bookmark: BookmarkVO) = flow {
        runCatching {
            localDao.deleteBookmark(bookmark.toBookmark())
        }.onFailure { throw it }.fold(
            onSuccess = { emit(true) },
            onFailure = { emit(false) }
        )
    }

    override fun requestNoticeDeletion(notice: NoticeVO) = flow {
        runCatching {
            localDao.deleteNoticeEntity(notice.toNoticeEntity())
        }.onFailure { throw it }.fold(
            onSuccess = { emit(true) },
            onFailure = { emit(false) }
        )
    }


    private fun Throwable.printLog() =
        Log.d(TAG, "Failed to receive data\nREASON: ${this.stackTraceToString()}")

    private fun NoticeVO.toNoticeEntity() =
        NoticeEntity(
            nttId = this.nttId,
            title = this.title,
            url = this.url,
            imageUrl = this.imageUrl ?: "",
            departName = this.departName,
            timestamp = this.timestamp,
            noticeCategory = this.noticeName
        ).run {
            this@toNoticeEntity.entityId?.let { id ->
                this.copy(
                    noticeEntityId = id
                )
            } ?: this
        }

    private fun NoticeEntity.toNoticeVO() =
        NoticeVO(
            entityId = this.noticeEntityId,
            nttId = this.nttId,
            title = this.title,
            url = this.url,
            imageUrl = this.imageUrl,
            departName = this.departName,
            timestamp = this.timestamp,
            noticeName = this.noticeCategory
        )

    private fun BookmarkVO.toBookmark() =
        Bookmark(
            bookmarkId = this.bookmarkId,
            nttId = this.targetNoticeNttId,
            isScheduled = this.isScheduled,
            reminderSchedule = this.reminderSchedule,
            note = this.bookmarkNote,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )

    private fun Bookmark.toBookmarkVO() =
        BookmarkVO(
            bookmarkId = this.bookmarkId,
            targetNoticeNttId = this.nttId,
            isScheduled = this.isScheduled,
            reminderSchedule = this.reminderSchedule,
            bookmarkNote = this.note,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )

    private fun BookmarkAsListElement.toVO() =
        BookmarkAsListElementVO(
            bookmarkId = this.bookmarkId,
            noticeId = this.noticeId,
            noticeTitle = this.noticeTitle,
            noticeCategory = this.noticeCategory,
            isReminderSet = this.isReminderSet,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )

    private fun List<Bookmark>.toListOfBookmarkVO() =
        this.map { it.toBookmarkVO() }
}