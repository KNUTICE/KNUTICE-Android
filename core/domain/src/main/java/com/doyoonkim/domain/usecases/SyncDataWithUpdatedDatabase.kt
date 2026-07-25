package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.BookmarkLocalRepository
import com.doyoonkim.domain.interfaces.NoticeLocalRepository
import com.doyoonkim.domain.interfaces.NoticeRemoteRepository
import com.doyoonkim.domain.util.KoreanTokenizer
import com.doyoonkim.model.BookmarkFtsVO
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.model.di.DefaultDispatcher
import com.doyoonkim.model.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

data class DatabaseSyncResult(
    val completed: Boolean = false,
    val withError: Boolean = true,
    val targetCounts: Int = 0,
    val failureCounts: Int = 0
)

interface SyncDataWithUpdateDatabase {

    fun manualSync(): Flow<DatabaseSyncResult>

    fun entrySync(): Flow<DatabaseSyncResult>
}

class SyncDataWithUpdatedDatabaseImpl @Inject constructor(
    private val noticeLocalRepository: NoticeLocalRepository,
    private val bookmarkLocalRepository: BookmarkLocalRepository,
    private val remoteRepository: NoticeRemoteRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : SyncDataWithUpdateDatabase {

    override fun manualSync(): Flow<DatabaseSyncResult> = flow {
        val resultFirstSync = databaseSync_1_2().first()
        if (resultFirstSync.withError) {
            emit(resultFirstSync)
        } else {
            emitAll(databaseSync_2_3())
        }
    }.flowOn(ioDispatcher)

    override fun entrySync(): Flow<DatabaseSyncResult> = flow {
        val resultFirstSync = databaseSync_1_2().first()
        if (resultFirstSync.withError) {
            emit(resultFirstSync)
        } else {
            emitAll(databaseSync_2_3())
        }
    }.flowOn(ioDispatcher)

    // Consider change this function to suspend function.
    private fun databaseSync_1_2() = flow {
        val bookmarks = bookmarkLocalRepository.queryAllBookmarks()
            .firstOrNull()

        if (bookmarks.isNullOrEmpty()) {
            emit(
                DatabaseSyncResult(
                    completed = true,
                    withError = false,
                    targetCounts = 0,
                    failureCounts = 0
                )
            )
            return@flow
        }

        var failureCounts = 0
        for (bookmark in bookmarks) {
            println("BookmarkVO: $bookmark")
            try {
                val noticeLocal = noticeLocalRepository.queryNoticeById(bookmark.targetNoticeNttId)
                    .firstOrNull()
                if (noticeLocal == null) {
                    failureCounts++
                    continue
                }

                println("NoticeLocalVO: $noticeLocal")

                // Check whether the values are already synced or not.
                if (!bookmark.isSynced()) {
                    val syncedBookmark = bookmark.copy(
                        createdAt = noticeLocal.timestamp.toLong(),
                        updatedAt =
                        if (bookmark.updatedAt > 0) {
                            bookmark.updatedAt
                        } else {
                            noticeLocal.timestamp.toLong()
                        }
                    ).also { println("Synced Bookmark: $it") }
                    if (!bookmarkLocalRepository.updateBookmark(syncedBookmark)) {
                        failureCounts++
                        continue
                    }
                }

                if (!noticeLocal.isSynced()) {
                    // Fetch noticeRemote
                    val noticeRemote = withTimeout(5000L) {
                        remoteRepository.queryNoticeById(noticeLocal.nttId)
                            .firstOrNull()
                    }
                    if (noticeRemote == null) {
                        failureCounts++
                        continue
                    }

                    val syncedNotice = noticeLocal.copy(
                        noticeName = noticeRemote.noticeName
                    ).also { println("Synced Notice: $it") }
                    if (!noticeLocalRepository.updateNoticeEntity(syncedNotice).first()) {
                        failureCounts++
                        continue
                    }
                }
            } catch (e: Exception) {
                // Error occurred during synchronization
                failureCounts++
                continue
            }
        }

        emit(
            DatabaseSyncResult(
                completed = true,
                withError = failureCounts > 0,
                targetCounts = bookmarks.size,
                failureCounts = failureCounts
            )
        )
    }.catch { /* OVERALL ERROR */ }.flowOn(ioDispatcher)

    // Consider change this function to suspend function.
    private fun databaseSync_2_3() = flow {
        val targets = bookmarkLocalRepository.queryBookmarkFtsTarget()
            .firstOrNull()

        println("[SyncProcess_2_3] target size: ${targets?.size}")

        if (targets.isNullOrEmpty()) {
            emit(
                DatabaseSyncResult(
                    completed = true,
                    withError = false,
                    targetCounts = 0,
                    failureCounts = 0
                )
            )
            return@flow
        }

        var failureCounts = 0
        for (target in targets) {
            try {
                val noteTokenized =
                    withContext(defaultDispatcher) {
                        async {
                            KoreanTokenizer.getTokenizedString(
                                target.bookmarkNotes
                            )
                        }
                    }.await()

                val titleTokenized =
                    withContext(defaultDispatcher) {
                        async {
                            KoreanTokenizer.getTokenizedString(
                                target.noticeTitle
                            )
                        }
                    }.await()

                val ftsEntryResult = bookmarkLocalRepository.createBookmarkFts(
                    BookmarkFtsVO(
                        ftsId = target.bookmarkId,
                        bookmarkNote = target.bookmarkNotes,
                        noticeTitle = target.noticeTitle,
                        bookmarkNoteTokenized = noteTokenized,
                        noticeTitleTokenized = titleTokenized
                    )
                ).first()

                println("[SyncProcess_2_3] ftsEntry processed with result $ftsEntryResult")
            } catch (e: Exception) {
                failureCounts++
                continue
            }
        }

        emit(
            DatabaseSyncResult(
                completed = true,
                withError = failureCounts > 0,
                targetCounts = targets.size,
                failureCounts = failureCounts
            )
        )
    }.catch { /* OVERALL ERROR */ }.flowOn(ioDispatcher)

    private fun BookmarkVO.isSynced(): Boolean {
        return this.createdAt > 0 && this.updatedAt > 0
    }

    private fun NoticeVO.isSynced(): Boolean {
        return this.noticeName.isNotBlank()
    }

    private fun String.toLong(): Long {
        val timestamps = this.split(" ")
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(timestamps[0]).time
    }
}
