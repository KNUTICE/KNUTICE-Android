package com.doyoonkim.knutice.data

import androidx.annotation.WorkerThread
import com.doyoonkim.knutice.data.local.KnuticeLocalSource
import com.doyoonkim.knutice.model.Bookmark
import com.doyoonkim.knutice.model.Notice
import com.doyoonkim.knutice.model.NoticeCategory
import com.doyoonkim.knutice.model.NoticeEntity
import com.doyoonkim.knutice.model.NoticesPerPage
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/*
ActivityRetainedComponent lives across configuration changes, so it is created at the first onCreate
 and last onDestroy, and when you mark your dependencies in ActivityRetainedComponent with
 @ActivityRetainedScope its guarantees that your object will be a singleton and survive across
 configuration changes
 */
// TODO: Consider change class name to KnuticeLocalRepository
@ActivityRetainedScoped
class NoticeLocalRepository @Inject constructor(
    private val remoteSource: KnuticeRemoteSource,
    private val localSource: KnuticeLocalSource
) {
    // Local
    fun createBookmark(bookmark: Bookmark): Result<Boolean> {
        return localSource.createBookmark(bookmark)
    }

    fun createBookmark(bookmark: Bookmark, targetNotice: Notice): Result<Boolean> {
        return localSource.createBookmark(bookmark, targetNotice)
    }

    fun updateBookmark(bookmark: Bookmark) {
        localSource.updateBookmark(bookmark)
    }

    fun deleteBookmark(bookmark: Bookmark) {
        localSource.deleteBookmark(bookmark)
    }

    fun deleteNoticeEntity(entity: NoticeEntity) {
        localSource.deleteNoticeEntity(entity)
    }

    fun getAllBookmarks(): Flow<Bookmark> {
        return flow {
            runCatching {
                localSource.getAllBookmarks().forEach { emit(it) }
            }.onFailure { emit(Bookmark(-1)) }
        }
    }

    fun getNoticeByNttId(nttId: Int): Notice {
        val noticeEntity = localSource.getNoticeByNttId(nttId)
        return noticeEntity.toNotice()
    }

    fun getBookmarkByNttID(nttId: Int): Result<Bookmark> {
        return runCatching {
            localSource.getBookmarkByNttId(nttId) ?: Bookmark(-1)
        }.onFailure { throw it }
    }

    // Remote
    @WorkerThread
    fun getTopThreeNotice(category: NoticeCategory): Flow<NoticesPerPage> {
        return flow<NoticesPerPage> {
            delay(10L)
            val response = remoteSource.getTopThreeNotice(category, 3)

            if (response.result?.resultCode == 200) emit(response)
            else emit(NoticesPerPage())
        }.flowOn(Dispatchers.IO)
    }

    fun getNoticesByCategoryPerPage(category: NoticeCategory, lastNttId: Int): Flow<NoticesPerPage> {
        return flow<NoticesPerPage> {
            val response = remoteSource.getNoticeListPerPage(category, lastNttId)
            if (response.result?.resultCode == 200) {
                emit(response)
            } else {
                NoticesPerPage()
            }
        }.flowOn(Dispatchers.IO)
    }

    fun queryNoticesByKeyword(keyword: String): Flow<NoticesPerPage> {
        return flow {
            remoteSource.queryNoticesByKeyword(keyword).run {
                if (this.result?.resultCode == 200) {
                    emit(this)
                } else {
                    emit(NoticesPerPage())
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getFullNoticeContent(url: String): Flow<String> {
        return flow<String> {
            emit(remoteSource.getFullNoticeContent(url).await())
        }.flowOn(Dispatchers.IO)
    }

    fun postNoticeSubscriptionPreference(topic: NoticeCategory, status: Boolean): Flow<Result<Boolean>> {
        return flow {
            emit(remoteSource.submitTopicSubscriptionPreference(
                topic, status
            ))
        }
    }
}