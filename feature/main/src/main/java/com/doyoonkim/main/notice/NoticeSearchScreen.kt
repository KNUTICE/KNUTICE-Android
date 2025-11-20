package com.doyoonkim.main.notice

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doyoonkim.main.viewmodel.NoticeSearchViewModel
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.containerGray
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.theme.variantPurple
import com.doyoonkim.common.ui.AnimatedTab
import com.doyoonkim.common.ui.NotificationPreview
import com.doyoonkim.common.ui.PlaceholderScreen
import com.doyoonkim.main.contract.NoticeSearchEvent
import com.doyoonkim.main.contract.NoticeSearchSideEffect
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun NoticeSearchScreen(
    modifier: Modifier = Modifier,
    viewModel: NoticeSearchViewModel,
    bottomPadding: Dp = 0.dp,
    onBackPressed: () -> Unit,
    onNoticeSelected: (Int, String) -> Unit,
    onBookmarkSelected: (Int, String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val localFocusManager = LocalFocusManager.current

    BackHandler { viewModel.sendUiEvent(NoticeSearchEvent.GoBack) }

    LaunchedEffect(Unit) {
        viewModel.uiSideEffect.collect { effect ->
            when (effect) {
                is NoticeSearchSideEffect.NavToNoticeDetail -> {
                    with (effect) {
                        onNoticeSelected(id, url)
                    }
                }
                is NoticeSearchSideEffect.NavToBack -> {
                    onBackPressed()
                }
            }
        }
    }

    // Observing Keyword Input
    LaunchedEffect(uiState.searchKeyword) {
        viewModel.observeKeywordInput()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.displayBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 5.dp)
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    value = uiState.searchKeyword,
                    placeholder = { Text(stringResource(R.string.title_search)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_search_24),
                            contentDescription = stringResource(R.string.title_search),
                            tint = MaterialTheme.colorScheme.onAnyBackground
                        )
                    },
                    onValueChange = {
                        viewModel.sendUiEvent(NoticeSearchEvent.UpdateSearchKeyword(it))
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.title,
                        unfocusedTextColor = MaterialTheme.colorScheme.subTitle,
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryBackground,
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryBackground,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(15.dp),
                    singleLine = true
                )

                if (uiState.searchKeyword.isNotBlank()) {
                    Button(
                        onClick = {
                            viewModel.sendUiEvent(NoticeSearchEvent.UpdateSearchKeyword(""))
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors().copy(
                            containerColor = MaterialTheme.colorScheme.onAnyBackground,
                            contentColor = MaterialTheme.colorScheme.containerGray
                        ),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .wrapContentSize()
                            .align(Alignment.CenterEnd)
                            .padding(end = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { localFocusManager.clearFocus() }
                        )
                    }
            ) {
                if (uiState.searchKeyword.isBlank()) {
                    PlaceholderScreen(
                        modifier = Modifier.padding(bottom = bottomPadding),
                        imageResource = R.drawable.magnifying_glasses,
                        contentText = stringResource(R.string.text_search_greeting)
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .wrapContentHeight()
                            .padding(top = 10.dp)
                            .align(Alignment.TopCenter),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        AnimatedTab(
                            modifier = Modifier.fillMaxWidth()
                                .wrapContentHeight(),
                            tabTitles = listOf("공지", "북마크"),
                            containerColor = MaterialTheme.colorScheme.secondaryBackground,
                            titleColor = MaterialTheme.colorScheme.title,
                            colorOnSelect = MaterialTheme.colorScheme.onAnyBackground
                        ) { index ->

                            LaunchedEffect(index) {
                                Log.d("NoticeSearchScreen", "Selected Tab Index: $index")
                                viewModel.updateSourceStatus(index)
                            }

                            // Target to be revised in better way.
                            if (!uiState.isFetching && uiState.isSearchResultEmpty) {
                                PlaceholderScreen(
                                    modifier = Modifier.padding(bottom = bottomPadding),
                                    imageResource = R.drawable.question_mark,
                                    contentText = stringResource(R.string.error_no_search_result)
                                )
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight(),
                                    contentPadding = PaddingValues(3.dp)
                                ) {
                                    when (index) {
                                        0 -> {
                                            itemsIndexed(uiState.fetchResult) { index, notice ->

                                                if (index == uiState.fetchResult.size - 1 && uiState.canRequestMoreNotices) {
                                                    // List reach ends.
                                                    viewModel.sendUiEvent(NoticeSearchEvent.RequestMoreNotices)
                                                }

                                                HorizontalDivider(
                                                    Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp),
                                                    color = MaterialTheme.colorScheme.onAnyBackground
                                                )

                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            viewModel.sendUiEvent(
                                                                NoticeSearchEvent.RequestNoticeDetail(
                                                                    notice.nttId, notice.url
                                                                )
                                                            )
                                                        }
                                                ) {
                                                    NotificationPreview(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        isLoading = uiState.isFetching,
                                                        isImageContained = notice.imageUrl != null,
                                                        notificationTitle = notice.title,
                                                        notificationInfo = "[${notice.departName}] ${notice.timestamp}",
                                                        imageUrl = notice.imageUrl ?: ""
                                                    )
                                                }
                                            }
                                        }
                                        else -> {
                                            itemsIndexed(uiState.localFetchResult) { index, bookmark ->

                                                if (index == uiState.localFetchResult.size - 1
                                                    && uiState.canRequestMoreNotices) {
                                                    // List reach ends.
                                                    viewModel.sendUiEvent(NoticeSearchEvent.RequestMoreNotices)
                                                }

                                                HorizontalDivider(
                                                    Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp),
                                                    color = MaterialTheme.colorScheme.onAnyBackground
                                                )

                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            onBookmarkSelected(
                                                                bookmark.noticeId,
                                                                bookmark.noticeTitle,
                                                                bookmark.noticeCategory
                                                            )
                                                        }
                                                ) {
                                                    NotificationPreview(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        isLoading = uiState.isFetching,
                                                        isImageContained = false,
                                                        notificationTitle = bookmark.noticeTitle,
                                                        notificationInfo = with(bookmark) {
                                                            if (updatedAt > createdAt) {
                                                                stringResource(R.string.text_updated_at) +
                                                                        " ${updatedAt.toFormattedDate()}"
                                                            } else {
                                                                stringResource(R.string.text_created_at) +
                                                                        " ${createdAt.toFormattedDate()}"
                                                            }
                                                        },
                                                        imageUrl = ""
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    item { Spacer(Modifier.height(bottomPadding)) }
                                }
                            }
                        }
                    }

                    androidx.compose.animation.AnimatedVisibility(
                        visible = uiState.isFetching,
                        modifier = Modifier.wrapContentSize().align(Alignment.Center),
                        enter = scaleIn(),
                        exit = scaleOut()
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.variantPurple,
                            trackColor = MaterialTheme.colorScheme.secondaryBackground
                        )
                    }
                }
            }
        }
    }
}

fun Long.toFormattedDate() =
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(this))