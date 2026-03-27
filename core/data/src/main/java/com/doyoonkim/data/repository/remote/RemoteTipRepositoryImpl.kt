package com.doyoonkim.data.repository.remote

import android.util.Log
import com.doyoonkim.domain.interfaces.TipRemoteRepository
import com.doyoonkim.model.TipVO
import com.doyoonkim.network.KnuticeRemoteSource
import kotlinx.coroutines.flow.flow
import model.Metadata
import javax.inject.Inject

class RemoteTipRepositoryImpl @Inject constructor(
    private val remoteSource: KnuticeRemoteSource
) : TipRemoteRepository {

    private val TAG = "RemoteTipRepositoryImpl"

    override fun queryAllTips() = flow {
        remoteSource.getAllTips().fold(
            onSuccess = {
                if (it.result?.resultCode == 200) {
                    emit(it.body?.map { dto -> dto.toVO() })
                } else it.result.printLog().also { emit(emptyList<TipVO>()) }
            },
            onFailure = {
                it.printLog()
                emit(null)
            }
        )
    }

    private fun Throwable.printLog() =
        Log.d(TAG, "Failed to receive data\nREASON: ${this.stackTraceToString()}")

    private fun Metadata?.printLog() =
        Log.d(TAG, "Failed to receive data (${this?.resultCode})" +
                "\nREASON:${this?.resultMessage}")
}