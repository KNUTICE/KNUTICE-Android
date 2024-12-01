package com.doyoonkim.knutice.data

import androidx.annotation.WorkerThread
import com.doyoonkim.knutice.domain.NoticeDummySource
import com.doyoonkim.knutice.model.Notice
import com.doyoonkim.knutice.model.NoticeCategory
import com.doyoonkim.knutice.model.NoticesPerPage
import com.doyoonkim.knutice.model.TopThreeNotices
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/*
ActivityRetainedComponent lives across configuration changes, so it is created at the first onCreate
 and last onDestroy, and when you mark your dependencies in ActivityRetainedComponent with
 @ActivityRetainedScope its guarantees that your object will be a singleton and survive across
 configuration changes
 */
@ActivityRetainedScoped
class NoticeLocalRepository @Inject constructor(
    private val remoteSource: KnuticeRemoteSource
) {
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

    fun getFullNoticeContent(url: String): Flow<String> {
        return flow<String> {
            emit(remoteSource.getFullNoticeContent(url).await())
        }.flowOn(Dispatchers.IO)
    }
}