package com.doyoonkim.common.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.doyoonkim.common.NoticeResources
import com.doyoonkim.common.R
import com.doyoonkim.model.NoticeCategory

@Composable
fun NotificationPreviewCardMarked(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    noticeTitle: String = "Title goes here",
    noticeSubtitle: String = "Subtitle goes here",
    noticeCategory: String = NoticeCategory.Unspecified.name,
    onItemClicked: () -> Unit = { }
) {
    val iconTint = NoticeResources.getColorResourceByCategory(noticeCategory)
    Box(
        modifier = modifier.wrapContentSize(),
        contentAlignment = Alignment.TopEnd
    ) {
        NotificationPreviewCard(
            isLoading = isLoading,
            notificationTitle = noticeTitle,
            notificationInfo = noticeSubtitle
        ) {
            onItemClicked()
        }
        Image(
            painter = painterResource(R.drawable.baseline_bookmarks_24),
            contentDescription = "Bookmark Image",
            colorFilter = ColorFilter.tint(iconTint)
        )
    }
}

@Preview(
    showSystemUi = false,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun NotificationPreviewCardMarked_Preview() {
}
