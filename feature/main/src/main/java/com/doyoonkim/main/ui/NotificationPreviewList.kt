package com.doyoonkim.main.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.ui.LazyText
import com.doyoonkim.common.ui.NotificationPreviewCard
import com.doyoonkim.model.NoticeVO
import kotlin.collections.forEach

@Composable
fun NotificationPreviewList(
    modifier: Modifier = Modifier,
    listTitle: String = "List Title goes here",
    titleColor: Color = Color.Unspecified,
    isContentLoading: Boolean = false,
    contents: List<NoticeVO> = listOf(),
    onMoreClicked: () -> Unit = {  },
    onNoticeClicked: (NoticeVO) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(7.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            LazyText(
                modifier = Modifier
                    .wrapContentHeight()
                    .weight(6f),
                text = listTitle,
                fontColor = titleColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLine = 1,
                isLoading = isContentLoading
            )

            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onClick = { onMoreClicked() },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = stringResource(R.string.btn_more),
                    color = MaterialTheme.colorScheme.subTitle,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        contents.forEach { content ->
            NotificationPreviewCard(
                notificationTitle = content.title,
                notificationInfo = "[${content.departName}] ${content.timestamp}",
                isLoading = isContentLoading
            ) {
                onNoticeClicked(content)
            }
        }
    }
}