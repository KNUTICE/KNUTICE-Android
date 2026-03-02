package com.doyoonkim.widget.util

import android.content.Intent
import androidx.core.net.toUri
import com.doyoonkim.widget.model.WidgetState

// Notice Widget Phase based on Status
sealed interface NoticeWidgetPhase {
    data object NotConfigured: NoticeWidgetPhase
    data object FetchFailure: NoticeWidgetPhase
    data object FetchCompleted: NoticeWidgetPhase
}

// NoticeWidgetUtil
class NoticeWidgetUtil {
    companion object {
        // Notice Widget Phase Validation
        fun WidgetState.validatePhase(): NoticeWidgetPhase {
            if (category.isBlank()) {
                // Not yet configured.
                return NoticeWidgetPhase.NotConfigured
            }

            if (notices.isEmpty()) {
                // Configured but Fetch Failure
                return NoticeWidgetPhase.FetchFailure
            }

            return NoticeWidgetPhase.FetchCompleted
        }

        // Intent Creation
        fun createDeeplinkIntent(
            packageName: String,
            deeplink: String = "",
            type: String? = null
        ) = Intent(Intent.ACTION_VIEW, "knutice://$deeplink".toUri()).apply {
            setPackage(packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
    }
}
