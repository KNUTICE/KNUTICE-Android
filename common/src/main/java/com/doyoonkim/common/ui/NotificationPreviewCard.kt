package com.doyoonkim.common.ui

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.doyoonkim.common.theme.secondaryBackground

@Composable
fun NotificationPreviewCard(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true,
    notificationTitle: String = "Title goes here.",
    notificationInfo: String = "Notification info goes here.",
    onClick: () -> Unit = { /* Action should be defined. */ }
) {
    Card(
        modifier.fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                onClick()
            },
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.secondaryBackground,
            contentColor = Color.Unspecified,
            disabledContainerColor = Color.Unspecified,
            disabledContentColor = Color.Unspecified
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        NotificationPreview(
            notificationTitle = notificationTitle,
            notificationInfo = notificationInfo,
            isLoading = isLoading
        )
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun NotificationPreviewContainer_Preview() {

}