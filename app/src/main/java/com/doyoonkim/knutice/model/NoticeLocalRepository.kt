package com.doyoonkim.knutice.model

import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
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
@ActivityRetainedScoped
class NoticeLocalRepository @Inject constructor(
    private val remoteSource: NoticeRemoteSource
) {
    private var localData: TopThreeNotices? = null

    fun getTopThreeNotice(): Flow<TopThreeNotices> {
        return flow<TopThreeNotices> {
            if (localData?.result?.resultCode == 200) {
                emit(localData!!)
            } else {
                val response = remoteSource.getTopThreeNotice(test = "").also {
                    localData = it
                }
                if (response?.result?.resultCode == 200) {
                    emit(response)
                } else {
                    emit(TopThreeNotices())
                }
            }
        }.flowOn(Dispatchers.IO)
    }

}