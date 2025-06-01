package com.doyoonkim.knutice.domain

import android.util.Log
import com.doyoonkim.knutice.data.KnuticeRemoteSource
import com.doyoonkim.knutice.model.Notice
import javax.inject.Inject


interface fetchSingleNotice {
    suspend fun getSingleNoticeById(nttId: String): Notice
}

class FetchSingleNoticeImpl @Inject constructor(
    private val remoteSource: KnuticeRemoteSource
) : fetchSingleNotice {

    override suspend fun getSingleNoticeById(nttId: String): Notice {
        remoteSource.getNoticeById(nttId).fold(
            onSuccess = {
                Log.d("FetchSingleNoticeImpl", it.notice?.toNotice().toString())
                return it.notice?.toNotice() ?: Notice()
            },
            onFailure = {
                Log.d("FetchSingleNoticeImpl", "Unable to receive notice")
                return Notice()
            }
        )
    }

    suspend operator fun invoke(nttId: String): Notice {
        return getSingleNoticeById(nttId)
    }

}