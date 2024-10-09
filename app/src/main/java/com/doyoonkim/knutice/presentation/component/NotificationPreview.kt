package com.doyoonkim.knutice.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.doyoonkim.knutice.ui.theme.subTitle
import com.doyoonkim.knutice.ui.theme.title

@Composable
fun NotificationPreview(
    modifier: Modifier = Modifier,
    isImageContained: Boolean = false,
    notificationTitle: String = "Title goes here.",
    notificationInfo: String = "Notification info goes here.",
    imageUrl: String = ""
) {
    Column(
        Modifier.padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        if (notificationTitle == "Unknown") {
            AnimatedGradient(Modifier.height(24.dp))
            AnimatedGradient(Modifier.height(14.dp))
        } else {
            if (isImageContained) {
                Surface(
                    Modifier.wrapContentSize(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Contained Image",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                            .height((LocalConfiguration.current.screenWidthDp / 2).dp)
                    )
                }
            }
            Text(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 7.dp, start = 5.dp, end = 5.dp),
                text = notificationTitle,
                textAlign = TextAlign.Start,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 1.dp, start = 5.dp, bottom = 5.dp, end = 5.dp),
                text = notificationInfo,
                textAlign = TextAlign.Start,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.subTitle
            )
        }
    }
}

@Preview
@Composable
fun NotificationPreview_Preview() {
    NotificationPreview()
}