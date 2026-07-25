package com.doyoonkim.domain.interfaces

import com.doyoonkim.model.requestBody.UserReportBody
import kotlinx.coroutines.flow.Flow

interface UserReportRemoteRepository {
    fun requestUserReportSubmission(body: UserReportBody): Flow<Boolean>
}
