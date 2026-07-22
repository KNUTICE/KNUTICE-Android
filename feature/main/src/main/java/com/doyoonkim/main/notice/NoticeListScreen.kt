package com.doyoonkim.main.notice

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.ui.HorizontalPagerWithTab
import com.doyoonkim.common.ui.LocalHomeSafeBottomPadding
import com.doyoonkim.common.ui.NotificationPreview
import com.doyoonkim.common.ui.PlaceholderScreen
import com.doyoonkim.common.ui.TopAppBarWithActions
import com.doyoonkim.main.contract.NoticeListUiState
import com.doyoonkim.main.viewmodel.NoticeListViewModel
import com.doyoonkim.model.NoticeVO

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NoticeListScreen(
    modifier: Modifier,
    viewModel: NoticeListViewModel,
    initialCategory: Int,
    onNewSubscriptionRequested: () -> Unit,
    onSettingsRequested: () -> Unit,
    onBackButtonPressed: () -> Unit,
    onNoticeDetailRequested: (Int, String) -> Unit
) {
    // uiState access
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler { /* Send GoBack event request */ }

    // Pull-to-Refresh action
    val pullRefreshState = rememberPullRefreshState(
        refreshing = false,
        onRefresh = {
            // Request refresh action.
        }
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBarWithActions(
                titleText = "공지"
            ) {
                IconButton(
                    onClick = { /* RequestSettings Event */ }
                ) {
                    Image(
                        painter = painterResource(R.drawable.baseline_settings_24),
                        contentDescription = "Settings",
                        modifier = Modifier.wrapContentSize(),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.title)
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.displayBackground
    ) { innerPadding ->
        // Get content safe bottom padding for customized bottom navigation bar
        // Access via CompositionLocal.
        val bottomPadding = LocalHomeSafeBottomPadding.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = bottomPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            when (uiState) {
                is NoticeListUiState.NoticeLoading -> Box() { }
                is NoticeListUiState.NoticeRefreshing -> Box() { }
                is NoticeListUiState.NoticeAvailableOnSuccess ->
                    NoticeListScreenContent(
                        uiState = uiState as NoticeListUiState.NoticeAvailableOnSuccess,
                        onNoticeDetailRequested = { },
                        onNewMajorSubscriptionRequested = { }
                    )

                is NoticeListUiState.NoticeUnavailableOnError -> NoticeListUnavailable()
            }
        }
    }

}

@Composable
private fun NoticeListScreenContent(
    uiState: NoticeListUiState.NoticeAvailableOnSuccess,
    onNoticeDetailRequested: (NoticeVO) -> Unit,
    onNewMajorSubscriptionRequested: () -> Unit
) {
    HorizontalPagerWithTab(
        initialPage = 0,
        tabItems = uiState.categories,
        isTabDynamic = true
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            userScrollEnabled = true
        ) {
            val key = uiState.categories[it]
            uiState.notices[key]?.let { notices ->
                itemsIndexed(notices) { i, e ->

                    LaunchedEffect(i) {
                        // Refresh Logic
                    }

                    if (i != 0) {
                        HorizontalDivider(
                            Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onAnyBackground,
                            thickness = 1.2.dp
                        )
                    }

                    Row(
                        modifier = Modifier
                            .wrapContentSize()
                            .clickable {
                                // Notice Click actions
                            }
                    ) {
                        NotificationPreview(
                            isLoading = e.title.isBlank(),
                            notificationTitle = e.title,
                            notificationInfo = "[${e.departName}] ${e.timestamp}",
                            isImageContained = !e.imageUrl.isNullOrBlank(),
                            imageUrl = e.imageUrl ?: "",
                            isRecent = e.isRecent
                        )
                    }

                    /* Loading Indicator
                    */
                }
            }
        }
    }

}

@Composable
private fun NoticeListUnavailable(
    // uiState: NoticeUnavailableOnError
) {
    // Replace Content Text with uiState.errors.first().message
    PlaceholderScreen(
        modifier = Modifier.fillMaxSize(),
        imageResource = R.drawable.question_mark,
        imageColor = MaterialTheme.colorScheme.title,
        contentText = "Notice List Unavailable"
    )
}

@Preview(showSystemUi = true, showBackground = true)
@PreviewFontScale
@Composable
fun NoticeListScreen_Preview() {
    val category = listOf("일반", "장학", "학사", "행사", "취업", "소프트웨어공학과")
    val testNotices = List<NoticeVO>(15) {
        NoticeVO(
            entityId = it,
            nttId = it,
            title = "Dummy Notice $it",
            url = "Some URL",
            imageUrl = "Image UrL",
            departName = "대학본부",
            timestamp = "2026-06-09",
            noticeName = "",
            isSummaryAvailable = true,
            isRecent = it < 6
        )
    }

    val notices = hashMapOf(
        "일반" to testNotices,
        "장학" to testNotices,
        "학사" to testNotices,
        "행사" to testNotices,
        "취업" to testNotices,
        "소프트웨어공학과" to testNotices
    )

    val sampleUiState = NoticeListUiState.NoticeAvailableOnSuccess(
        categories = category,
        notices = notices
    )


}