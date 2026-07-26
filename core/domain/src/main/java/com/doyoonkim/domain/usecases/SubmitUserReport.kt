package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.UserReportRemoteRepository
import com.doyoonkim.model.di.IoDispatcher
import com.doyoonkim.model.requestBody.UserReportBody
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface SubmitUserReport {
    operator fun invoke(body: UserReportBody): Flow<Boolean>
}

class SubmitUserReportImpl @Inject constructor(
    private val remoteRepository: UserReportRemoteRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SubmitUserReport {

    override operator fun invoke(body: UserReportBody) =
        remoteRepository.requestUserReportSubmission(body)
            .catch {
                /* Internal Error. Consume values, and never emit values. */
            }.flowOn(ioDispatcher)
}
