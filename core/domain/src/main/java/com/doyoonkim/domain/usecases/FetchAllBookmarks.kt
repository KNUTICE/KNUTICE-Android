package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.LocalRepository
import com.doyoonkim.domain.RemoteRepository
import com.doyoonkim.domain.SortOption
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject


interface FetchAllBookmarks {
    operator fun invoke(size: Int, pageNumber: Int, option: SortOption): Flow<Pair<BookmarkVO, NoticeVO>>
}

class FetchAllBookmarksImpl @Inject constructor(
    private val localRepository: LocalRepository,
    private val syncDataWithUpdatedDatabase: SyncDataWithUpdateDatabase
) : FetchAllBookmarks {

    override operator fun invoke(size: Int, pageNumber: Int, option: SortOption): Flow<Pair<BookmarkVO, NoticeVO>> =
        localRepository.queryBookmarkSorted(
            size, pageNumber, option
        ).transform { bookmarkVO ->
            bookmarkVO?.let {
                emitAll(localRepository.queryNoticeById(it.targetNoticeNttId).transform { nullable ->
                    nullable?.let { vo->
                        println("Local Notice: ${vo.toString()}")
                        emitAll(syncDataWithUpdatedDatabase.syncNotice(vo)
                            .flowOn(Dispatchers.IO).transform { synced ->
                            emit(
                                Pair(
                                    if (it.createdAt > 0) it
                                    else it.copy(createdAt = synced.timestamp.toLong()),
                                    synced
                                )
                            )
                        })
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