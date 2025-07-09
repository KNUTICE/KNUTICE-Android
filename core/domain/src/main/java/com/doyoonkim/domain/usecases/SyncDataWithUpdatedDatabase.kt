package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.BookmarkLocalRepository
import com.doyoonkim.domain.interfaces.NoticeLocalRepository
import com.doyoonkim.domain.interfaces.NoticeRemoteRepository
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.model.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SyncDataWithUpdateDatabase {

    override fun manualSync(): Flow<DatabaseSyncResult> = flow {
        emitAll(databaseSync_1_2())
    }.flowOn(ioDispatcher)

    override fun entrySync(): Flow<DatabaseSyncResult> = flow {
        emitAll(databaseSync_1_2())
    }.flowOn(ioDispatcher)

    private fun databaseSync_1_2() = flow {
        val bookmarks = bookmarkLocalRepository.queryAllBookmarks()
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
                val noticeLocal = noticeLocalRepository.queryNoticeById(bookmark.targetNoticeNttId)
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
                    bookmarkLocalRepository.updateBookmark(syncedBookmark).firstOrNull()
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
                    noticeLocalRepository.updateNoticeEntity(syncedNotice).firstOrNull()
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