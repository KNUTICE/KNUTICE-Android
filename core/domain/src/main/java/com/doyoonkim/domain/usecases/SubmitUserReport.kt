package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.RemoteRepository
import com.doyoonkim.model.requestBody.UserReportBody
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface SubmitUserReport {
    operator fun invoke(body: UserReportBody): Flow<Boolean>
}

class SubmitUserReportImpl @Inject constructor(
    private val remoteRepository: RemoteRepository
) : SubmitUserReport {

    override operator fun invoke(body: UserReportBody) =
        remoteRepository.requestUserReportSubmission(body)

}