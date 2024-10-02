package com.doyoonkim.knutice.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doyoonkim.knutice.ui.theme.containerBackground
import com.doyoonkim.knutice.ui.theme.title
import com.doyoonkim.knutice.viewModel.DetailedContentState
import com.example.knutice.R

@Composable
fun DetailedNoticeContent(
    modifier: Modifier = Modifier,
    requested: DetailedContentState = DetailedContentState(),
    onCloseRequested: () -> Unit = {  }
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.containerBackground
    ) {
        Column(
            Modifier.padding(15.dp)
        ) {
            Row(
                Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    modifier = Modifier.weight(5f).wrapContentHeight(),
                    text = requested.title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(
                    modifier = Modifier.weight(1f).wrapContentSize(),
                    onClick = { onCloseRequested() }
                ) {
                    Image(
                        painter = painterResource(R.drawable.baseline_close_24),
                        contentDescription = "Close Button",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.title)
                    )
                }
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                text = requested.info,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
            Text(
                modifier = Modifier.fillMaxSize(),
                text = requested.fullContent,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}


@Preview
@Composable
fun DetailedNoticeContent_Preview() {
}