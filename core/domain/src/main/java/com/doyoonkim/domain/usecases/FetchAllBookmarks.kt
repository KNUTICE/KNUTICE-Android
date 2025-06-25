package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.LocalRepository
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.transform
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject


interface FetchAllBookmarks {
    operator fun invoke(size: Int, pageNumber: Int): Flow<Pair<BookmarkVO, NoticeVO>>
}

class FetchAllBookmarksImpl @Inject constructor(
    private val localRepository: LocalRepository
) : FetchAllBookmarks {

    override operator fun invoke(size: Int, pageNumber: Int): Flow<Pair<BookmarkVO, NoticeVO>> =
        localRepository.queryAllBookmarks(
            size, pageNumber
        ).transform { bookmarkVO ->
            bookmarkVO?.let {
                emitAll(localRepository.queryNoticeById(it.targetNoticeNttId).transform { nullable ->
                    nullable?.let { vo->
                        emit(
                            Pair(
                                if (it.createdAt > 0) it
                                else it.copy(createdAt = vo.timestamp.toLong()),
                                vo
                            )
                        )
                    }
                })
            }
        }.catch {
            /* Internal Error. Consume values, and never emit values. */
        }

    private fun String.toLong(): Long {
        val timestamps = this.split(" ")
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(timestamps[0]).time
    }

}