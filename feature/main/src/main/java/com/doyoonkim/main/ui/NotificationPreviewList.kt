package com.doyoonkim.main.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.HorizontalDivider
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
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.ui.LazyText
import com.doyoonkim.common.ui.NotificationPreview
import com.doyoonkim.model.NoticeVO

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
    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(7.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 7.dp),
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
            contents.forEachIndexed { index, content ->
                NotificationPreview(
                    modifier = Modifier.clickable(
                        interactionSource = null,
                        indication = null,
                        enabled = true,
                        onClick = { onNoticeClicked(content) },
                    ),
                    isLoading = isContentLoading,
                    notificationTitle = content.title,
                    notificationInfo = "[${content.departName}] ${content.timestamp}",
                    isRecent = content.isRecent
                )
                if (index != contents.lastIndex)
                    HorizontalDivider(
                        thickness = 1.2.dp,
                        color = MaterialTheme.colorScheme.onAnyBackground
                    )
            }
        }
    }
}