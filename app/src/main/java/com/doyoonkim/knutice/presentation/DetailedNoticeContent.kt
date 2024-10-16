package com.doyoonkim.knutice.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doyoonkim.knutice.ui.theme.buttonContainer
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
    val localContext = LocalContext.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.containerBackground
    ) {
        Column(
            Modifier.padding(15.dp)
        ) {
            Row(
                Modifier.fillMaxWidth()
                    .weight(0.5f),
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
                    .wrapContentHeight()
                    .weight(0.5f),
                text = requested.info,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )

            Text(
                modifier = Modifier.fillMaxWidth()
                    .weight(8f),
                text = requested.fullContent,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
            )

            Button(
                onClick = {
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(requested.fullContentUrl))
                    localContext.startActivity(webIntent)
                },
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 15.dp, end = 15.dp, top = 5.dp, bottom = 5.dp)
                    .weight(0.7f),
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.buttonContainer,
                    contentColor = Color.White,
                    disabledContentColor = Color.Unspecified,
                    disabledContainerColor = Color.Unspecified
                )
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.btn_more_on_browser),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}


@Preview
@Composable
fun DetailedNoticeContent_Preview() {
    DetailedNoticeContent(
        requested = DetailedContentState(
            title = "Test",
            info = "Test",
            fullContent = "Full Content"
        )
    )
}