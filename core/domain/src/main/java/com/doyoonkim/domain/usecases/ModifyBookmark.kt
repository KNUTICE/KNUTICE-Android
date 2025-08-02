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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface ModifyBookmark {
    // Contains function for query, update, delete of Bookmark
    fun query(nttId: Int): Flow<BookmarkVO>

    fun createOrUpdate(bookmark: BookmarkVO, notice: NoticeVO?): Flow<Boolean>

    fun delete(bookmark: BookmarkVO, notice: NoticeVO): Flow<Boolean>
}

class ModifyBookmarkImpl @Inject constructor(
    private val noticeLocalRepository: NoticeLocalRepository,
    private val bookmarkLocalRepository: BookmarkLocalRepository,
    private val remoteRepository: NoticeRemoteRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ModifyBookmark {

    override fun query(nttId: Int): Flow<BookmarkVO> =
        bookmarkLocalRepository.queryBookmarkByNttId(nttId).transform { result ->
            result?.let { emit(it) }
        }.catch {
            /* Internal Error. Consume values, and never emit values. */
        }.flowOn(ioDispatcher)

    // TODO: Need to be revised later. (Is NoticeVO really required)
    override fun createOrUpdate(bookmark: BookmarkVO, notice: NoticeVO?) = flow {
        if (notice == null) {
            // Need creation. Request notice instance from the remote source first.
            remoteRepository.queryNoticeById(bookmark.targetNoticeNttId).transform { result ->
                if (result != null) {
                    emitAll(
                        combine(
                            bookmarkLocalRepository.createBookmark(bookmark, result),
                            bookmarkLocalRepository.createBookmarkFts(
                                generateBookmarkFtsEntry(bookmark, result)
                            )
                        ) { first, second ->
                            first && second
                        }
                    )
                }
                else emit(false)
            }.catch {
                /* Internal Error. Consume values, and never emit values. */
            }.flowOn(ioDispatcher)
        } else {
            emitAll(
                combine(
                    bookmarkLocalRepository.updateBookmark(bookmark),
                    bookmarkLocalRepository.updateBookmarkFts(
                        generateBookmarkFtsEntry(bookmark, notice)
                    )
                ) { first, second ->
                    first && second
                }
            )
        }
    }

    override fun delete(bookmark: BookmarkVO, notice: NoticeVO): Flow<Boolean> =
        bookmarkLocalRepository.requestBookmarkDeletion(bookmark).transform { result ->
            if (result) {
                emitAll(
                    combine(
                        noticeLocalRepository.requestNoticeDeletion(notice),
                        bookmarkLocalRepository.deleteBookmarkFts(
                            generateBookmarkFtsEntry(bookmark, notice)
                        )
                    ) { first, second ->
                        first && second
                    }
                )
            }
            else emit(false)
        }.catch {
            /* Internal Error. Consume values, and never emit values. */
        }.flowOn(ioDispatcher)


    private suspend fun generateBookmarkFtsEntry(bookmark: BookmarkVO, notice: NoticeVO): BookmarkFtsVO {
        return BookmarkFtsVO(
            ftsId = bookmark.bookmarkId,
            bookmarkNote = bookmark.bookmarkNote,
            noticeTitle = notice.title,
            bookmarkNoteTokenized = withContext(defaultDispatcher) {
                async { KoreanTokenizer.getTokenizedString(bookmark.bookmarkNote) }
            }.await(),
            noticeTitleTokenized = withContext(defaultDispatcher) {
                async { KoreanTokenizer.getTokenizedString(notice.title) }
            }.await()
        )
    }

}

