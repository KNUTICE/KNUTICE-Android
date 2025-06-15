package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.LocalRepository
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import javax.inject.Inject


interface FetchAllBookmarks {
    operator fun invoke(): Flow<Pair<BookmarkVO, NoticeVO>>
}

class FetchAllBookmarksImpl @Inject constructor(
    private val localRepository: LocalRepository
) : FetchAllBookmarks {

    override operator fun invoke(): Flow<Pair<BookmarkVO, NoticeVO>> =
        localRepository.queryAllBookmarks().transform { bookmarkVO ->
            bookmarkVO?.let {
                emitAll(localRepository.queryNoticeById(it.targetNoticeNttId).transform { nullable ->
                    nullable?.let { vo-> emit(Pair(bookmarkVO, vo)) }
                })
            }
        }

}