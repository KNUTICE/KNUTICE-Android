package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.NoticeRemoteRepository
import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

interface FetchNoticesPerPage {
    operator fun invoke(category: NoticeCategory, lastNttId: Int): Flow<List<NoticeVO>>
}

class FetchNoticesPerPageImpl @Inject constructor(
    private val remoteRepository: NoticeRemoteRepository
) : FetchNoticesPerPage {

    override operator fun invoke(category: NoticeCategory, lastNttId: Int) =
        remoteRepository.run {
            if (lastNttId == 0) queryNoticesPerPage(category, null)
            else queryNoticesPerPage(category, lastNttId)
        }.transform { result ->
            result?.let { emit(it) }
        }.catch {
            /* Internal Error. Consume values, and never emit values. */
        }
}