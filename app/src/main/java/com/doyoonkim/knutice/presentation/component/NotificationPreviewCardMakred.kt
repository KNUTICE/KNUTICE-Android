package com.doyoonkim.knutice.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.doyoonkim.knutice.R

@Composable
fun NotificationPreviewCardMarked(
    modifier: Modifier = Modifier,
    noticeTitle: String = "Title goes here",
    noticeSubtitle: String = "Subtitle goes here",
    onItemClicked: () -> Unit = {  },
    onBackPressed: () -> Unit = {  }
) {
    Box(
        modifier = modifier.wrapContentSize(),
        contentAlignment = Alignment.TopEnd
    ) {
        NotificationPreviewCard(
            notificationTitle = noticeTitle,
            notificationInfo = noticeSubtitle,
        ) {
            onItemClicked()
        }
        Image(
            painter = painterResource(R.drawable.baseline_bookmarks_24),
            contentDescription = "Bookmark Image",
            colorFilter = ColorFilter.tint(Color.Red),
            modifier = Modifier.padding(end = 10.dp)
        )
    }
}

@Preview(showSystemUi = false, showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun NotificationPreviewCardMarked_Preview() {
    NotificationPreviewCardMarked()
}