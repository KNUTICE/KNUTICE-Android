package com.doyoonkim.knutice.model

import com.doyoonkim.knutice.domain.NoticeDummySource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
ActivityRetainedComponent lives across configuration changes, so it is created at the first onCreate
 and last onDestroy, and when you mark your dependencies in ActivityRetainedComponent with
 @ActivityRetainedScope its guarantees that your object will be a singleton and survive across
 configuration changes
 */
@ActivityRetainedScoped
class NoticeLocalRepository @Inject constructor(
    private val remoteSource: NoticeRemoteSource
) {
    private var localData: TopThreeNotices? = null

    fun getTopThreeNotice(isDummy: Boolean = false): Flow<TopThreeNotices> {
        if (isDummy) {
            return channelFlow {
                trySend(async { NoticeDummySource.getTopThreeNoticeDummy() }.await())
                close()
            }.flowOn(Dispatchers.IO)
        } else {
            return flow<TopThreeNotices> {
                if (localData?.result?.resultCode == 200) {
                    emit(localData!!)
                } else {
                    val response = remoteSource.getTopThreeNotice().also {
                        localData = it
                    }
                    if (response.result?.resultCode == 200) {
                        emit(response)
                    } else {
                        emit(TopThreeNotices())
                    }
                }
            }.flowOn(Dispatchers.IO)
        }
    }
}