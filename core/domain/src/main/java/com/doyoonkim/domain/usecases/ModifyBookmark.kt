package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.AsyncFtsTaskScheduler
import com.doyoonkim.domain.interfaces.BookmarkLocalRepository
import com.doyoonkim.domain.interfaces.NoticeRemoteRepository
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.model.PendingBookmarkFtsVO
import com.doyoonkim.model.StagingPolicy
import com.doyoonkim.model.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

interface ModifyBookmark {
    // Contains function for query, update, delete of Bookmark
    fun query(nttId: Int): Flow<BookmarkVO>

    fun createOrUpdate(bookmark: BookmarkVO, notice: NoticeVO?): Flow<Boolean>

    fun delete(bookmark: BookmarkVO, notice: NoticeVO): Flow<Boolean>
}

class ModifyBookmarkImpl @Inject constructor(
    private val bookmarkLocalRepository: BookmarkLocalRepository,
    private val remoteRepository: NoticeRemoteRepository,
    private val pendingWorkScheduler: AsyncFtsTaskScheduler,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ModifyBookmark {

    override fun query(nttId: Int): Flow<BookmarkVO> =
        bookmarkLocalRepository.queryBookmarkByNttId(nttId).transform { result ->
            result?.let { emit(it) }
        }.catch {
            /* Internal Error. Consume values, and never emit values. */
        }.flowOn(ioDispatcher)

    override fun createOrUpdate(bookmark: BookmarkVO, notice: NoticeVO?) = flow {
        if (notice == null) {
            // Creation
            // Need creation. Request notice instance from the remote source first.
            val vo = remoteRepository.queryNoticeById(bookmark.targetNoticeNttId)
                .firstOrNull()
            if (vo == null) emit(false).also { return@flow }

            // Bookmark Entity Creation (Sequential DB Operation)
            val result = bookmarkLocalRepository.createBookmark(
                bookmark, vo, PendingBookmarkFtsVO(
                    bookmarkId = bookmark.bookmarkId,
                    notes = bookmark.bookmarkNote,
                    title = vo.title,
                    policy = StagingPolicy.INSERT
                )
            )

            if (result) pendingWorkScheduler.execute()

            emit(result)
        } else {
            // Update
            val result = bookmarkLocalRepository.updateBookmark(
                bookmark, PendingBookmarkFtsVO(
                    bookmarkId = bookmark.bookmarkId,
                    notes = bookmark.bookmarkNote,
                    title = notice.title,
                    policy = StagingPolicy.UPDATE
                )
            )
            if (result) pendingWorkScheduler.execute()

            emit(result)     // Bookmark Entity insertion is already successful.
        }
    }.catch { /* Internal Error */ }.flowOn(ioDispatcher)

    override fun delete(bookmark: BookmarkVO, notice: NoticeVO): Flow<Boolean> = flow {
        val result = bookmarkLocalRepository.requestBookmarkDeletion(
            bookmark, notice, PendingBookmarkFtsVO(
                bookmarkId = bookmark.bookmarkId,
                notes = bookmark.bookmarkNote,
                title = notice.title,
                policy = StagingPolicy.DELETE
            ))
        if (result) pendingWorkScheduler.execute()

        emit(result)
    }.catch {
        /* Internal Error. Consume values, and never emit values. */
    }.flowOn(ioDispatcher)

}

