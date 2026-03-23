package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.NoticeRemoteRepository
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.model.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

interface FetchNoticesPerPage {
    operator fun invoke(category: String, lastNttId: Int): Flow<Result<List<NoticeVO>>>
}

class FetchNoticesPerPageImpl @Inject constructor(
    private val remoteRepository: NoticeRemoteRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FetchNoticesPerPage {

    override operator fun invoke(category: String, lastNttId: Int) =
        remoteRepository.run {
            if (lastNttId == 0) queryNoticesPerPage(category, null)
            else queryNoticesPerPage(category, lastNttId)
        }.transform { result ->
            result?.let {
                if (lastNttId == 0) {
                    emit(Result.success(it.checkRecentNotices()))
                } else {
                    emit(Result.success(it))
                }
            } ?: emit(Result.failure(NoSuchElementException()))
        }.catch {
            /* Internal Error. */
            emit(Result.failure(it))
        }.flowOn(ioDispatcher)

    private fun List<NoticeVO>.checkRecentNotices(): List<NoticeVO> {
        // Retrieve Current Time
        val current = System.currentTimeMillis()
        // DateTimeFormatter
        val format = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        // 24-hours in Millis
        val dayInMillis = 86_400_000L
        return this.map {
            try {
                val timeInMills = LocalDate.parse(it.timestamp, format)
                    .atStartOfDay(ZoneId.of("Asia/Seoul"))
                    .toInstant()
                    .toEpochMilli()

                it.copy(
                    isRecent =  current - timeInMills <= dayInMillis
                )
            } catch (e: DateTimeParseException) {
                // Unable to parse Date information. (Server-side formatting issue.)
                it
            }
        }
    }
}