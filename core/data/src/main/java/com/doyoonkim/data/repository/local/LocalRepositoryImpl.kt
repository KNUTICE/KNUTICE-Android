package com.doyoonkim.data.repository.local

import android.util.Log
import androidx.room.withTransaction
import com.doyoonkim.data.model.Bookmark
import com.doyoonkim.data.model.BookmarkAsListElement
import com.doyoonkim.data.model.BookmarkFts
import com.doyoonkim.data.model.NoticeEntity
import com.doyoonkim.data.model.PendingBookmarkFtsAsync
import com.doyoonkim.data.room.LocalDatabase
import com.doyoonkim.domain.SortOption
import com.doyoonkim.domain.interfaces.BookmarkLocalRepository
import com.doyoonkim.domain.interfaces.NoticeLocalRepository
import com.doyoonkim.model.BookmarkAsListElementVO
import com.doyoonkim.model.BookmarkFtsVO
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.model.PendingBookmarkFtsVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val database: LocalDatabase
) : BookmarkLocalRepository, NoticeLocalRepository {
    private val TAG = "LocalRepositoryImpl"
    private val localDao = database.getDao()

    // CRUD
    override suspend fun createBookmark(
        bookmark: BookmarkVO,
        targetNotice: NoticeVO,
        pending: PendingBookmarkFtsVO
    ): Boolean {
        return try {
            // Ensure Atomicity in Sequential DB Operations
            database.withTransaction {
                // Insert Notice Locally
                localDao.createNoticeEntity(targetNotice.toNoticeEntity())
                // Insert Bookmark Entity
                localDao.createBookmark(bookmark.toBookmark())
                // Insert Pending Entity to Staging Table
                localDao.createAsyncFtsEntity(pending.toEntry())

                // Successful Sequential DB Operation.
                true
            }
        } catch (e: Exception) {
            e.printLog()
            false
        }
    }

    override suspend fun updateBookmark(
        bookmark: BookmarkVO,
        pending: PendingBookmarkFtsVO?
    ): Boolean {
        return try {
            database.withTransaction {
                // Update Bookmark Entity
                localDao.updateBookmark(bookmark.toBookmark())
                // Insert Pending Entity for Updated Bookmark
                pending?.let {
                    localDao.createAsyncFtsEntity(it.toEntry())
                }

                // Successful Sequential DB Operation
                true
            }
        } catch (e: Exception) {
            e.printLog()
            false
        }
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

    override fun queryBookmarkFtsTarget() = flow {
        runCatching {
            localDao.getFtsEntriesFromExistingTables()
        }.fold(
            onSuccess = { dto -> emit(dto.map { it.toVO() }) },
            onFailure = { emit(null) }
        )
    }

    override suspend fun queryPendingBookmarkFtsBatched(limit: Int): List<PendingBookmarkFtsVO> {
        runCatching {
            localDao.getPendingBookmarkFtsAsyncBatch(limit)
        }.fold(
            onSuccess = { result ->
                return result.map {
                    PendingBookmarkFtsVO(
                        stagingId = it.stagingId,
                        bookmarkId = it.bookmarkId,
                        notes = it.bookmarkNotes,
                        title = it.noticeTitle,
                        policy = it.stagingPolicy
                    )
                }
            },
            onFailure = {
                it.printLog().also { return emptyList() }
            }
        )
        return emptyList()
    }

    override fun removePendingBookmarkFtsEntry(stagingId: List<Int>): Flow<Boolean> = flow {
        runCatching {
            localDao.removePendingBookmarkFtsAsync(stagingId)
        }.fold(
            onSuccess = { emit(true) },
            onFailure = { it.printLog().also { emit(false) } }
        )
    }

    override fun queryBookmarkSorted(size: Int, pageNumber: Int, option: SortOption) = flow {
        runCatching {
            with (localDao) {
                when (option) {
                    SortOption.ASC_CREATION -> getBookmarkListSortedNewest(size, pageNumber)
                    SortOption.DES_CREATION -> getBookmarkListSortedOldest(size, pageNumber)
                    SortOption.ASC_UPDATED -> getBookmarkListSortedUpdatedNewest(size, pageNumber)
                    SortOption.DESC_UPDATED -> getBookmarkListSortedUpdatedOldest(size, pageNumber)
                }
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

    override fun queryBookmarkByKeyword(
        keyword: String,
        size: Int,
        pageNumber: Int
    ): Flow<List<BookmarkAsListElementVO>?> = flow {
        runCatching {
            val fts = localDao.getBookmarkListByKeywordFts(keyword, size, pageNumber)
            fts.ifEmpty { localDao.getBookmarkListByKeyword(keyword, size, pageNumber) }
        }.fold(
            onSuccess = { result ->
                emit(result.map { it.toVO() })
            },
            onFailure = { emit(null) }
        )
    }

    override suspend fun requestBookmarkDeletion(
        bookmark: BookmarkVO,
        targetNotice: NoticeVO,
        pending: PendingBookmarkFtsVO
    ): Boolean {
        return try {
            database.withTransaction {
                // Delete Bookmark first.
                localDao.deleteBookmark(bookmark.toBookmark())
                // Delete Notice
                localDao.deleteNoticeEntity(targetNotice.toNoticeEntity())
                // Insert Pending Entity for Bookmark Deletion
                localDao.createAsyncFtsEntity(pending.toEntry())

                // Successful Sequential DB Operation
                true
            }
        } catch (e: Exception) {
            e.printLog()
            false
        }
    }

    override fun createBookmarkFts(ftsEntry: BookmarkFtsVO) = flow {
        Log.d("LocalRepositoryImpl", "Create FTS entry using ${ftsEntry.toString()}")
        runCatching {
            localDao.createBookmarkFts(
                with(ftsEntry) {
                    BookmarkFts(
                        id = ftsId,
                        bookmarkNotes = bookmarkNote,
                        noticeTitle = noticeTitle,
                        bookmarkNoteTokenized = bookmarkNoteTokenized,
                        noticeTitleTokenized = noticeTitleTokenized
                    )
                }
            )
        }.fold(
            onSuccess = {
                emit(true)
                        },
            onFailure = {
                it.printLog()
                emit(false)
            }
        )
    }

    override fun updateBookmarkFts(ftsEntry: BookmarkFtsVO) = flow {
        runCatching {
            localDao.updateBookmarkFts(
                id = ftsEntry.ftsId,
                notes = ftsEntry.bookmarkNote,
                title = ftsEntry.noticeTitle,
                notesTokenized = ftsEntry.bookmarkNoteTokenized,
                titleTokenized = ftsEntry.noticeTitleTokenized
            )
        }.fold(
            onSuccess = { emit(true) },
            onFailure = { emit(false) }
        )
    }

    override fun deleteBookmarkFts(ftsEntry: BookmarkFtsVO) = flow {
        runCatching {
            localDao.deleteBookmarkFts(ftsEntry.ftsId)
        }.fold(
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

    private fun PendingBookmarkFtsVO.toEntry() =
        PendingBookmarkFtsAsync(
            bookmarkId = this.bookmarkId,
            bookmarkNotes = this.notes,
            noticeTitle = this.title,
            stagingPolicy = this.policy
        )

    private fun List<Bookmark>.toListOfBookmarkVO() =
        this.map { it.toBookmarkVO() }
}