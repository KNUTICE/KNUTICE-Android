package com.doyoonkim.data.repository.remote

import android.util.Log
import com.doyoonkim.domain.interfaces.NoticeRemoteRepository
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.network.KnuticeRemoteSource
import kotlinx.coroutines.flow.flow
import model.Metadata
import javax.inject.Inject

class RemoteNoticeRepositoryImpl @Inject constructor(
    private val remoteSource: KnuticeRemoteSource
) : NoticeRemoteRepository {
    private val TAG = "RemoteContentRepositoryImpl"

    override suspend fun queryTopThreeNotices(category: String): List<NoticeVO>? {
        remoteSource.getNoticesPerPage(category = category, size = 3).fold(
            onSuccess = {
                if (it.result?.resultCode == 200) return it.body?.map { it.toVO() }
                else it.result?.printLog().also { return emptyList<NoticeVO>() }
            },
            onFailure = {
                it.printLog()
                return null
            }
        )
        return null
    }



    override fun queryNoticesPerPage(category: String, lastNttId: Int?) = flow {
        remoteSource.getNoticesPerPage(category = category, lastNttId = lastNttId).fold(
            onSuccess = {
                if (it.result?.resultCode == 200) emit(it.body?.map { it.toVO() })
                else it.result.printLog().also { emit(emptyList<NoticeVO>()) }
            },
            onFailure = {
                it.printLog()
                emit(null)
            }
        )
    }

    override fun queryNoticeById(nttId: Int) = flow {
        remoteSource.getNoticeById(nttId).fold(
            onSuccess = {
                Log.d(TAG, "Received Result: ${it.toString()}")
                if (it.result?.resultCode == 200) emit(it.body?.toVO())
                else it.result.printLog().also { emit(null) }
            },
            onFailure = {
                it.printLog()
                emit(null)
            }
        )
    }

    override fun queryNoticesByKeyword(keyword: String, lastNttId: Int?) = flow {
        remoteSource.getNoticesByKeyword(keyword, lastNttId).fold(
            onSuccess = {
                if (it.result?.resultCode == 200) emit(it.body?.map { it.toVO() })
                else it.result.printLog().also { emit(emptyList<NoticeVO>()) }
            },
            onFailure = {
                it.printLog()
                emit(null)
            }
        )
    }

    override suspend fun getNoticeSummary(nttId: Int): String? {
        remoteSource.getNoticeSummary(nttId).fold(
            onSuccess = {
                if (it.result?.resultCode == 200) return it.body?.rawSummary
                else it.result.printLog().also { return null }
            },
            onFailure = {
                it.printLog()
                return null
            }
        )
        return null
    }

    private fun Throwable.printLog() =
        Log.d(TAG, "Failed to receive data\nREASON: ${this.stackTraceToString()}")

    private fun Metadata?.printLog() =
        Log.d(TAG, "Failed to receive data (${this?.resultCode})" +
                "\nREASON:${this?.resultMessage}")
}