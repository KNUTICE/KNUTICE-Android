package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.NoticeRemoteRepository
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.model.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

interface FetchNoticesPerPage {
    operator fun invoke(category: String, lastNttId: Int): Flow<Result<List<NoticeVO>>>
}

class FetchNoticesPerPageImpl @Inject constructor(
    private val remoteRepository: NoticeRemoteRepository,
    private val checkRecentNoticeUseCase: CheckRecentNotice,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FetchNoticesPerPage {

    override operator fun invoke(category: String, lastNttId: Int) =
        remoteRepository.run {
            if (lastNttId == 0) queryNoticesPerPage(category, null)
            else queryNoticesPerPage(category, lastNttId)
        }.transform { result ->
            result?.let {
                if (lastNttId == 0) {
                    emit(Result.success(checkRecentNoticeUseCase(it)))
                } else {
                    emit(Result.success(it))
                }
            } ?: emit(Result.failure(NoSuchElementException()))
        }.catch {
            /* Internal Error. */
            emit(Result.failure(it))
        }.flowOn(ioDispatcher)
}