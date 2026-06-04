package com.doyoonkim.domain.usecases

import com.doyoonkim.model.NoticeVO
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

/**
 * @author kimdoyoon
 * Created 6/4/26 at 6:21 PM
 */
class CheckRecentNotice @Inject constructor() {

    operator fun invoke(notices: List<NoticeVO>): List<NoticeVO> {
        // Retrieve Current Time
        val current = System.currentTimeMillis()
        // DateTimeFormatter
        val format = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        // 48-hours in Millis
        val twoDaysMillis = 86_400_000L * 2
        return notices.map {
            try {
                val timeInMillis = LocalDate.parse(it.timestamp, format)
                    .atStartOfDay(ZoneId.of("Asia/Seoul"))
                    .toInstant()
                    .toEpochMilli()

                it.copy(
                    isRecent = current - timeInMillis <= twoDaysMillis
                )
            } catch (e: DateTimeParseException) {
                // Unable to parse Date information.
                // Potentially caused by Server-side formatting issue.)
                it
            }
        }
    }

}