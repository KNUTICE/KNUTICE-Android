package com.doyoonkim.main.notice

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doyoonkim.common.MajorResources
import com.doyoonkim.common.NoticeResources
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.KNUTICETheme
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.theme.variantPurple
import com.doyoonkim.common.ui.HorizontalPagerWithTab
import com.doyoonkim.common.ui.LocalHomeSafeBottomPadding
import com.doyoonkim.common.ui.NotificationPreview
import com.doyoonkim.common.ui.PlaceholderScreen
import com.doyoonkim.common.ui.TopAppBarWithActions
import com.doyoonkim.common.util.valueOrNull
import com.doyoonkim.main.contract.NoticeListEvent
import com.doyoonkim.main.contract.NoticeListSideEffect
import com.doyoonkim.main.contract.NoticeListUiState
import com.doyoonkim.main.viewmodel.NoticeListViewModel
import com.doyoonkim.model.MajorCategory
import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.model.NoticeVO.Companion.isNotEmpty
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NoticeListScreenItem(
    modifier: Modifier,
    viewModel: NoticeListViewModel,
    initialCategory: String,
    onNewSubscriptionRequested: () -> Unit,
    onSettingsRequested: () -> Unit,
    onBackButtonPressed: () -> Unit,
    onNoticeDetailRequested: (Int, String) -> Unit
) {
    // uiState access
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiSideEffect.collectLatest { sideEffect ->
            when (sideEffect) {
                is NoticeListSideEffect.GoBack -> onBackButtonPressed()
                is NoticeListSideEffect.NavToSettings -> onSettingsRequested()
                is NoticeListSideEffect.ShowMessage -> { /* Display toast message */
                }
            }
        }
    }

    BackHandler { viewModel.sendUiEvent(NoticeListEvent.RequestGoBack) }

    // Pull-to-Refresh action
    val pullRefreshState = rememberPullRefreshState(
        refreshing = false,
        onRefresh = {
            // Request refresh action.
            viewModel.sendUiEvent(NoticeListEvent.RequestRefresh)
        }
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBarWithActions(
                titleText = "공지"
            ) {
                IconButton(
                    onClick = { viewModel.sendUiEvent(NoticeListEvent.RequestNavToSettings) }
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
                is NoticeListUiState.NoticeLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .background(Color.Transparent)
                                .clip(RoundedCornerShape(20.dp)),
                            color = MaterialTheme.colorScheme.onAnyBackground
                        ) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(25.dp),
                                color = MaterialTheme.colorScheme.variantPurple
                            )
                        }
                    }
                }

                is NoticeListUiState.NoticeRefreshing -> Box { }
                is NoticeListUiState.NoticeAvailableOnSuccess -> {
                    val state = uiState as NoticeListUiState.NoticeAvailableOnSuccess
                    NoticeListScreenContent(
                        uiState = state,
                        initialTabPage = state.categories.indexOf(initialCategory),
                        onNextPageRequested = { category -> viewModel.sendUiEvent(NoticeListEvent.FetchNotices(category)) },
                        onNoticeDetailRequested = { notice ->
                            notice.takeIf { it.isNotEmpty() }?.let { onNoticeDetailRequested(it.nttId, it.url) }
                        },
                        onNewMajorSubscriptionRequested = { }
                    )
                }

                is NoticeListUiState.NoticeUnavailableOnError -> NoticeListUnavailable()
            }
        }
    }
}

@Composable
private fun NoticeListScreenContent(
    uiState: NoticeListUiState.NoticeAvailableOnSuccess,
    initialTabPage: Int = 0,
    onNextPageRequested: (String) -> Unit,
    onNoticeDetailRequested: (NoticeVO) -> Unit,
    onNewMajorSubscriptionRequested: () -> Unit
) {
    HorizontalPagerWithTab(
        initialPage = initialTabPage,
        tabItems = uiState.categories.map {
            stringResource(
                valueOrNull<NoticeCategory>(it)?.let { noticeCategory ->
                    NoticeResources.getStringResourcesByCategory(noticeCategory.name)
                } ?: valueOrNull<MajorCategory>(it)?.let { majorCategory ->
                    MajorResources.getLocalizedString(majorCategory.name)
                } ?: R.string.text_category_not_found
            )
        },
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
                itemsIndexed(notices) { i, notice ->

                    LaunchedEffect(i) {
                        // Refresh Logic
                        if (i == notices.lastIndex) {
                            onNextPageRequested(key)
                        }
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
                                onNoticeDetailRequested(notice)
                            }
                    ) {
                        NotificationPreview(
                            isLoading = notice.title.isBlank(),
                            notificationTitle = notice.title,
                            notificationInfo = with(notice) { "[$departName] $timestamp" },
                            isImageContained = !notice.imageUrl.isNullOrBlank(),
                            imageUrl = notice.imageUrl ?: "",
                            isRecent = notice.isRecent
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
    // TODO: uiState: NoticeUnavailableOnError
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

    KNUTICETheme {
        NoticeListScreenContent(
            uiState = sampleUiState,
            onNextPageRequested = { },
            onNoticeDetailRequested = { },
            onNewMajorSubscriptionRequested = { }
        )
    }
}
