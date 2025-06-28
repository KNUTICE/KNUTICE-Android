package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.LocalRepository
import com.doyoonkim.domain.RemoteRepository
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.model.NoticeVO
import jdk.javadoc.internal.doclets.toolkit.taglets.snippet.Bookmark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.withTimeout
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

    fun initiateManualSync(): Flow<DatabaseSyncResult>

    operator fun invoke(): Flow<Pair<Boolean, Boolean>>

}

class SyncDataWithUpdatedDatabaseImpl @Inject constructor(
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
) : SyncDataWithUpdateDatabase {

    override fun initiateManualSync() = flow {
        val bookmarks = localRepository.queryAllBookmarks()
            .firstOrNull()

        if (bookmarks.isNullOrEmpty()) {
            emit(DatabaseSyncResult(
                completed = true,
                withError = false,
                targetCounts = 0,
                failureCounts = 0
            ))
            return@flow
        }

        var failureCounts = 0
        for (bookmark in bookmarks) {
            println("BookmarkVO: ${bookmark.toString()}")
            try {
                val noticeLocal = localRepository.queryNoticeById(bookmark.targetNoticeNttId)
                    .firstOrNull()
                if (noticeLocal == null) {
                    failureCounts++
                    continue
                }

                println("NoticeLocalVO: ${noticeLocal.toString()}")

                // Check whether the values are already synced or not.
                if (!bookmark.isSynced()) {
                    val syncedBookmark = bookmark.copy(
                        createdAt = noticeLocal.timestamp.toLong(),
                        updatedAt =
                            if (bookmark.updatedAt > 0) bookmark.updatedAt
                            else noticeLocal.timestamp.toLong()
                    ).also { println("Synced Bookmark: ${it.toString()}") }
                    localRepository.updateBookmark(syncedBookmark).firstOrNull()
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
                    ).also { println("Synced Notice: ${it.toString()}") }
                    localRepository.updateNoticeEntity(syncedNotice).firstOrNull()
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

    }.catch { /* OVERALL ERROR */ }.flowOn(Dispatchers.IO)

    override fun invoke() = flow<Pair<Boolean, Boolean>> {
        var isSyncFailedOccurred = false
        // Fetch all bookmark first.
        val bookmarks = localRepository.queryAllBookmarks()
            .firstOrNull()

        if (bookmarks.isNullOrEmpty()) {
            // No bookmarks to be synced.
            emit(Pair(true, false))
            return@flow
        }

        for (bookmark in bookmarks) {
            try {
                // if noticeLocal is null, skip the sync
                val noticeLocal = localRepository.queryNoticeById(bookmark.targetNoticeNttId)
                    .firstOrNull() ?: continue

                // if noticeRemote is null, skip the sync
                val noticeRemote = withTimeout(5000L) {
                    remoteRepository.queryNoticeById(noticeLocal.nttId)
                        .firstOrNull()
                } ?: continue

                val syncedBookmark = bookmark.copy(
                    createdAt = noticeRemote.timestamp.toLong(),
                    updatedAt = noticeRemote.timestamp.toLong()
                ).also{ println("Synced Bookmark: ${it.toString()}") }
                val syncedNotice = noticeLocal.copy(
                    noticeName = noticeRemote.noticeName
                ).also{ println("Synced Notice: ${it.toString()}") }

                localRepository.updateBookmark(syncedBookmark).firstOrNull()
                localRepository.updateNoticeEntity(syncedNotice).firstOrNull()
            } catch (e: Exception) {
                // Unable to process sync. Skip sync of this elements
                println("Unable to process sync. ${e.printStackTrace()}")
                isSyncFailedOccurred = true
                continue
            }
        }
        emit(Pair(true, isSyncFailedOccurred))
    }.flowOn(Dispatchers.IO)

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