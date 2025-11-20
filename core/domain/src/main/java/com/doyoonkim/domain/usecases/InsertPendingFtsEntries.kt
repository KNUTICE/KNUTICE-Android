package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.BookmarkLocalRepository
import com.doyoonkim.domain.util.KoreanTokenizer
import com.doyoonkim.model.BookmarkFtsVO
import com.doyoonkim.model.PendingBookmarkFtsVO
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
): InsertPendingFtsEntries {
    override suspend fun execute(target: PendingBookmarkFtsVO): Boolean {
        val result = localRepository.createBookmarkFts(
            BookmarkFtsVO(
                ftsId = target.bookmarkId,
                bookmarkNote = target.notes,
                noticeTitle = target.title,
                bookmarkNoteTokenized = withContext(defaultDispatcher) {
                    async { KoreanTokenizer.getTokenizedString(target.notes) }
                }.await(),
                noticeTitleTokenized = withContext(defaultDispatcher) {
                    async { KoreanTokenizer.getTokenizedString(target.title) }
                }.await()
            )
        ).first()
        return result
    }
}