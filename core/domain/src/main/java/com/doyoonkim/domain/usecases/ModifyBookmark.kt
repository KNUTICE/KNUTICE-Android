package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.LocalRepository
import com.doyoonkim.domain.RemoteRepository
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

interface ModifyBookmark {
    // Contains function for query, update, delete of Bookmark
    fun query(nttId: Int): Flow<BookmarkVO>

    fun createOrUpdate(bookmark: BookmarkVO, notice: NoticeVO?): Flow<Boolean>

    fun delete(bookmark: BookmarkVO, notice: NoticeVO): Flow<Boolean>
}

class ModifyBookmarkImpl @Inject constructor(
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
) : ModifyBookmark {

    override fun query(nttId: Int): Flow<BookmarkVO> =
        localRepository.queryBookmarkByNttId(nttId).transform { result ->
            result?.let { emit(it) }
        }

    // TODO: Need to be revised later. (Is NoticeVO really required)
    override fun createOrUpdate(bookmark: BookmarkVO, notice: NoticeVO?): Flow<Boolean> {
        return if (notice == null) {
            // Need creation. Request notice instance from the remote source first.
            remoteRepository.queryNoticeById(bookmark.targetNoticeNttId).transform { result ->
                if (result != null) emitAll(localRepository.createBookmark(bookmark, result))
                else emit(false)
            }
        } else {
            localRepository.updateBookmark(bookmark)
        }
    }

    override fun delete(bookmark: BookmarkVO, notice: NoticeVO): Flow<Boolean> =
        localRepository.requestBookmarkDeletion(bookmark).transform { result ->
            if (result) emitAll(localRepository.requestNoticeDeletion(notice))
            else emit(false)
        }

}

