package com.doyoonkim.knutice.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.doyoonkim.knutice.ui.theme.buttonContainer
import com.doyoonkim.knutice.ui.theme.containerBackground
import com.doyoonkim.knutice.R
import com.doyoonkim.knutice.viewModel.DetailedNoticeContentViewModel

@Composable
fun DetailedNoticeContent(
    modifier: Modifier = Modifier,
    viewModel: DetailedNoticeContentViewModel = hiltViewModel()
) {
    val localContext = LocalContext.current

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.requestFullContent()
    }

    Box(
        Modifier.fillMaxSize()
            .clickable(false) { /* Do Nothing; Prevent user to control UI behind the popped-up composable. */ }
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
                    text = state.title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .weight(0.5f),
                text = state.info,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )

            Surface(
                modifier = Modifier.fillMaxWidth()
                    .weight(8f)
                    .verticalScroll(rememberScrollState()),
                color = MaterialTheme.colorScheme.containerBackground,
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ) {
                    if (state.imageUrl != "") {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(state.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Loaded Image, which is a part of the notice.",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxSize().padding(7.dp)
                        )
                    }
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        text = state.fullContent,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }

            }

            Button(
                onClick = {
                    if (state.fullContentUrl != "") {
                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(state.fullContentUrl))
                        localContext.startActivity(webIntent)
                    }
                },
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 5.dp)
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
}