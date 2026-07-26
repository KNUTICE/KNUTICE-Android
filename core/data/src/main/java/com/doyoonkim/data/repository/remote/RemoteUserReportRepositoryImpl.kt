package com.doyoonkim.data.repository.remote

import android.util.Log
import com.doyoonkim.domain.interfaces.AppTokenPreferenceRepository
import com.doyoonkim.domain.interfaces.UserReportRemoteRepository
import com.doyoonkim.model.requestBody.UserReportBody
import com.doyoonkim.network.KnuticeRemoteSource
import com.doyoonkim.network.model.ReportSaveRequest
import kotlinx.coroutines.flow.flow
import model.Metadata
import javax.inject.Inject

class RemoteUserReportRepositoryImpl @Inject constructor(
    private val remoteSource: KnuticeRemoteSource,
    private val appPreference: AppTokenPreferenceRepository
) : UserReportRemoteRepository {

    private val TAG = "RemoteUserReportRepositoryImpl"

    override fun requestUserReportSubmission(body: UserReportBody) = flow {
        remoteSource.submitUserReport(
            appPreference.getCachedToken(),
            ReportSaveRequest(
                content = body.content,
                deviceName = body.deviceName,
                version = body.version
            )
        ).fold(
            onSuccess = {
                if (it.result?.resultCode == 200) {
                    emit(true)
                } else {
                    it.result.printLog().also { emit(false) }
                }
            },
            onFailure = {
                it.printLog()
                emit(false)
            }
        )
    }

    private fun Throwable.printLog() =
        Log.d(TAG, "Failed to receive data\nREASON: ${this.stackTraceToString()}")

    private fun Metadata?.printLog() =
        Log.d(
            TAG,
            "Failed to receive data (${this?.resultCode})" +
                "\nREASON:${this?.resultMessage}"
        )
}
