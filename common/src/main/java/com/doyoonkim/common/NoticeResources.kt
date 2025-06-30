package com.doyoonkim.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.doyoonkim.common.theme.containerGray
import com.doyoonkim.common.theme.notificationType1
import com.doyoonkim.common.theme.notificationType2
import com.doyoonkim.common.theme.notificationType3
import com.doyoonkim.common.theme.notificationType4
import com.doyoonkim.model.NoticeCategory

class NoticeResources {
    companion object {
        @Composable
        fun getStringResourcesByCategory(category: String): Int {
            return when (category) {
                NoticeCategory.GENERAL_NEWS.name -> R.string.general_news
                NoticeCategory.ACADEMIC_NEWS.name -> R.string.academic_news
                NoticeCategory.SCHOLARSHIP_NEWS.name -> R.string.scholarship_news
                NoticeCategory.EVENT_NEWS.name -> R.string.event_news
                else -> R.string.text_category_not_found
            }
        }

        @Composable
        fun getColorResourceByCategory(category: String): Color {
            return when (category) {
                NoticeCategory.GENERAL_NEWS.name -> MaterialTheme.colorScheme.notificationType1
                NoticeCategory.ACADEMIC_NEWS.name -> MaterialTheme.colorScheme.notificationType2
                NoticeCategory.SCHOLARSHIP_NEWS.name -> MaterialTheme.colorScheme.notificationType3
                NoticeCategory.EVENT_NEWS.name -> MaterialTheme.colorScheme.notificationType4
                else -> MaterialTheme.colorScheme.containerGray
            }
        }
    }
}