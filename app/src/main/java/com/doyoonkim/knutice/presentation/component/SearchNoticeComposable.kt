package com.doyoonkim.knutice.presentation.component

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doyoonkim.knutice.R
import com.doyoonkim.knutice.model.FullContent
import com.doyoonkim.knutice.ui.theme.containerBackground
import com.doyoonkim.knutice.ui.theme.subTitle
import com.doyoonkim.knutice.ui.theme.textPurple
import com.doyoonkim.knutice.ui.theme.title
import com.doyoonkim.knutice.viewModel.SearchNoticeViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@OptIn(FlowPreview::class)
@Composable
fun SearchNotice(
    modifier: Modifier = Modifier,
    viewModel: SearchNoticeViewModel = hiltViewModel(),
    onBackClicked: () -> Unit = {  },
    onNoticeClicked: (FullContent) -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        snapshotFlow{ uiState.searchKeyword }
            .debounce(500L)
            .distinctUntilChanged()
            .filter { it.isNotBlank() }
            .collectLatest {
                viewModel.queryNoticeByKeyword(it)
            }
    }

    BackHandler { onBackClicked() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                modifier = Modifier
                    .weight(8f)
                    .wrapContentHeight()
                    .padding(2.dp),
                value = uiState.searchKeyword,
                placeholder = { Text(stringResource(R.string.title_search)) },
                onValueChange = { viewModel.updateKeyword(it) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.title,
                    unfocusedTextColor = MaterialTheme.colorScheme.subTitle,
                    focusedContainerColor = MaterialTheme.colorScheme.containerBackground,
                    unfocusedContainerColor = MaterialTheme.colorScheme.containerBackground,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(15.dp)
            )
        }

        Box(
            modifier = Modifier.fillMaxSize()
                .align(Alignment.CenterHorizontally)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
                    .wrapContentHeight(),
                contentPadding = PaddingValues(3.dp)
            ) {
                items(uiState.queryResult) { notice ->
                    HorizontalDivider(
                        Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp),
                        color = MaterialTheme.colorScheme.containerBackground
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().wrapContentHeight()
                            .clickable {
                                onNoticeClicked(FullContent(
                                    title = notice.title,
                                    info = "[${notice.departName}] ${notice.timestamp}",
                                    url = notice.url,
                                    imgUrl = notice.imageUrl
                                ))
                            }
                    ) {
                        NotificationPreview(
                            modifier = Modifier.fillMaxWidth(),
                            isImageContained = notice.imageUrl != "Unknown",
                            notificationTitle = notice.title,
                            notificationInfo = "[${notice.departName}] ${notice.timestamp}",
                            imageUrl = notice.imageUrl
                        )
                    }

                }
            }

            // Loading Indicator w/ AnimatedVisibility
            androidx.compose.animation.AnimatedVisibility(
                visible = uiState.isQuerying,
                modifier = Modifier.wrapContentSize().align(Alignment.Center),
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.textPurple
                )
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES.and(Configuration.UI_MODE_NIGHT_MASK), showSystemUi = true)
@Composable
fun SearchNotice_Preview(

) {
    SearchNotice {

    }
}