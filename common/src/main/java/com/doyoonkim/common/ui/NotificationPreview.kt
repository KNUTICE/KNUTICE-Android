package com.doyoonkim.common.ui

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.theme.variantPurple
import com.doyoonkim.common.theme.variantPurpleLight
import com.doyoonkim.common.theme.variantPurpleStrong

@Composable
fun NotificationPreview(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true,
    isImageContained: Boolean = false,
    notificationTitle: String = "Unknown",
    notificationInfo: String = "Notification info goes here.",
    imageUrl: String = "",
    isRecent: Boolean = false
) {
    Column(
        modifier.padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (isLoading) {
            AnimatedGradient(Modifier.height(24.dp))
            AnimatedGradient(Modifier.height(14.dp))
        } else {
            if (isImageContained) {
                val imageHeight = (LocalConfiguration.current.screenWidthDp / 2).dp
                Surface(
                    Modifier.wrapContentSize(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .diskCachePolicy(CachePolicy.DISABLED)
                            .memoryCachePolicy(CachePolicy.READ_ONLY)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Contained Image",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                            .height(imageHeight)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 7.dp, start = 5.dp, end = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // Badge
                if (isRecent){
                    Surface(
                        modifier = Modifier.wrapContentSize()
                            .background(Color.Transparent),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.variantPurpleLight,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.variantPurple)
                    ) {
                        Text(
                            modifier = Modifier.wrapContentSize()
                                .padding(horizontal = 5.dp, vertical = 3.dp),
                            text = "N",
                            style = TextStyle(
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.variantPurpleStrong
                            )
                        )
                    }
                }

                Text(
                    modifier = Modifier.fillMaxWidth().weight(9f),
                    text = notificationTitle,
                    textAlign = TextAlign.Start,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 1.dp, start = 5.dp, bottom = 5.dp, end = 5.dp),
                text = notificationInfo,
                textAlign = TextAlign.Start,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.subTitle
            )
        }
    }
}

@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_DESK,
    backgroundColor = 0xFF000000
)
@Composable
fun NotificationPreview_Preview() {
    NotificationPreview(
        modifier = Modifier.fillMaxWidth(),
        isLoading = false,
        isImageContained = false,
        notificationTitle = "[한남대학교] 데이터 분석 파이썬 기초과정 참여자 모집 안내",
        notificationInfo = "[취업성공지원과] 2025-08-21",
        imageUrl = "",
        isRecent = true
    )
}