package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.BookmarkLocalRepository
import com.doyoonkim.domain.util.KoreanTokenizer
import com.doyoonkim.model.BookmarkFtsVO
import com.doyoonkim.model.PendingBookmarkFtsVO
import com.doyoonkim.model.StagingPolicy
import com.doyoonkim.model.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface InsertPendingFtsEntries {
    suspend fun execute(target: PendingBookmarkFtsVO): Boolean
}

class InsertPendingFtsEntriesImpl @Inject constructor(
    private val localRepository: BookmarkLocalRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : InsertPendingFtsEntries {
    override suspend fun execute(target: PendingBookmarkFtsVO): Boolean {
        // Policy Based Execution
        return when (target.policy) {
            StagingPolicy.INSERT -> {
                localRepository.createBookmarkFts(
                    createFtsVO(target)
                ).first()
            }
            StagingPolicy.UPDATE -> {
                localRepository.updateBookmarkFts(
                    createFtsVO(target)
                ).first()
            }

            StagingPolicy.DELETE -> {
                localRepository.deleteBookmarkFts(
                    createFtsVO(target)
                ).first()
            }
        }
    }

    private suspend fun createFtsVO(target: PendingBookmarkFtsVO): BookmarkFtsVO {
        return BookmarkFtsVO(
            ftsId = target.bookmarkId,
            bookmarkNote = target.notes,
            noticeTitle = target.title,
            bookmarkNoteTokenized = tokenization(target.notes),
            noticeTitleTokenized = tokenization(target.title)
        )
    }

    private suspend fun tokenization(target: String): String =
        withContext(defaultDispatcher) {
            async {
                KoreanTokenizer.getTokenizedString(target)
            }.await()
        }
}
