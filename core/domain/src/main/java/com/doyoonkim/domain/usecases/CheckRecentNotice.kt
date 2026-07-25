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

    companion object {
        // DateTimeFormatter
        private val format = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // Target Zone ID (Asia/Seoul)
        private val zoneId = ZoneId.of("Asia/Seoul")
    }

    operator fun invoke(notices: List<NoticeVO>): List<NoticeVO> {
        // Retrieve current date.
        val current = LocalDate.now(zoneId)
        return notices.map {
            try {
                // Utilize direct LocalDate comparison for Recent validation.
                val posted = LocalDate.parse(it.timestamp, format)
                it.copy(
                    // Recent Notice Validation based on 48 hours window.
                    isRecent = !posted.isBefore(current.minusDays(2))
                )
            } catch (e: DateTimeParseException) {
                // Unable to parse Date information.
                // Potentially caused by Server-side formatting issue.)
                it
            }
        }
    }
}
