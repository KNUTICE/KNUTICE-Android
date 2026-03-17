package com.doyoonkim.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doyoonkim.common.R

@Composable
fun AiSummaryDialog(
    modifier: Modifier = Modifier,
    icon: Int,
    header: String,
    attribution: String,
    footer: String,
    containerColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    isContentAvailable: Boolean,
    content: @Composable () -> Unit
) {
    if (!isContentAvailable) {
        Box(
            modifier = modifier.padding(horizontal = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            PlaceholderScreen(
                modifier = Modifier.fillMaxSize(),
                imageResource = R.drawable.question_mark,
                contentText = stringResource(R.string.info_ai_summary_not_available)
            )
        }
    } else {
        Column(
            modifier = modifier
        ) {
            Row(
                modifier = Modifier.wrapContentHeight()
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                        .clip(CircleShape)
                )
                Text(
                    text = header,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primaryTextColor
                )
            }

            // Content Area
            Surface(
                modifier = Modifier.wrapContentSize()
                    .padding(top = 10.dp, bottom = 10.dp, start = 40.dp, end = 15.dp)
                    .background(Color.Transparent),
                color = containerColor,
                shape = RoundedCornerShape(16.dp),
            ) { content() }

            // Attribution
            Text(
                text = attribution,
                style = TextStyle(
                    color = secondaryTextColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
                    .wrapContentHeight()
            )

            // Footer
            Text(
                text = footer,
                style = TextStyle(
                    color = secondaryTextColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
                    .wrapContentHeight()
            )
        }
    }
}