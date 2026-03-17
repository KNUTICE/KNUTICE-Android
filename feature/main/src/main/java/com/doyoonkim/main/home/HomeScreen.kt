package com.doyoonkim.main.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.doyoonkim.common.theme.notificationType1
import com.doyoonkim.common.theme.notificationType2
import com.doyoonkim.common.theme.notificationType3
import com.doyoonkim.common.theme.notificationType4
import com.doyoonkim.common.R
import com.doyoonkim.common.navigation.Destination
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.notificationType5
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.ui.PlaceholderScreen
import com.doyoonkim.common.ui.TipCategory
import com.doyoonkim.common.ui.TipPager
import com.doyoonkim.common.ui.TopAppBarWithActions
import com.doyoonkim.main.contract.HomeEvent
import com.doyoonkim.main.contract.HomeSideEffect
import com.doyoonkim.main.ui.NotificationPreviewList
import com.doyoonkim.main.viewmodel.HomeViewModel
import com.doyoonkim.model.NoticeCategory

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    bottomPadding: Dp = 0.dp,
    onSettingsRequested: () -> Unit,
    onGoBackAction: () -> Unit,
    onMoreNoticeRequested: (Destination) -> Unit,
    onFullContentRequested: (Int, String) -> Unit,
    onTipClicked: (TipCategory, String) -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    // Back button/gesture actions
    BackHandler {
        viewModel.sendUiEvent(HomeEvent.GoBack)
    }

    LaunchedEffect(Unit) {
        viewModel.sendUiEvent(HomeEvent.RequestMainContents)

        viewModel.uiSideEffect.collect { effect ->
            when (effect) {
                is HomeSideEffect.NavToNoticeDetail -> {
                    with (effect) {
                        onFullContentRequested(id, url)
                    }
                }
                is HomeSideEffect.NavToMoreNoticeInCategory -> {
                    with (effect) {
                        onMoreNoticeRequested(dest)
                    }
                }
                is HomeSideEffect.NavToTipDetail -> {
                    with (effect) {
                        onTipClicked(category,url)
                    }
                }
                is HomeSideEffect.NavToSettings -> {
                    onSettingsRequested()
                }
                is HomeSideEffect.NavToBack -> {
                    onGoBackAction()
                }
                else -> { /* Currently Do Nothing */ }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBarWithActions(
                titleText = stringResource(R.string.app_name)
            ) {
                IconButton(
                    onClick = { viewModel.sendUiEvent(HomeEvent.RequestSettings) }
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

        if (uiState.mainContentState.isError) {
            PlaceholderScreen(
                modifier = modifier
                    .padding(innerPadding)
                    .padding(bottom = bottomPadding),
                imageResource = R.drawable.wifi,
                contentText = stringResource(R.string.error_no_network_connection)
            )
        } else {
            LazyColumn(
                modifier = modifier
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.tipState.tips.isNotEmpty()) {
                    item(key = "header") {
                        TipPager(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            tips = uiState.tipState.tips
                        ) {
                            viewModel.sendUiEvent(
                                HomeEvent.RequestTipDetail(TipCategory.UPDATES, it)
                            )
                        }
                    }
                }

                item {
                    NotificationPreviewList (
                        listTitle = stringResource(R.string.general_news),
                        titleColor = MaterialTheme.colorScheme.notificationType1,
                        isContentLoading = uiState.mainContentState.isLoading,
                        contents = uiState.mainContentState.notificationGeneral,
                        onMoreClicked = {
                            viewModel.sendUiEvent(
                                HomeEvent.RequestMore(NoticeCategory.GENERAL_NEWS)
                            )
                        }
                    ) {
                        viewModel.sendUiEvent(HomeEvent.RequestNoticeDetail(it.nttId, it.url))
                    }
                }

                item {
                    NotificationPreviewList(
                        listTitle = stringResource(R.string.academic_news),
                        titleColor = MaterialTheme.colorScheme.notificationType2,
                        isContentLoading = uiState.mainContentState.isLoading,
                        contents = uiState.mainContentState.notificationAcademic,
                        onMoreClicked = {
                            viewModel.sendUiEvent(
                                HomeEvent.RequestMore(NoticeCategory.ACADEMIC_NEWS)
                            )
                        }
                    ) {
                        viewModel.sendUiEvent(HomeEvent.RequestNoticeDetail(it.nttId, it.url))
                    }
                }

                item {
                    NotificationPreviewList(
                        listTitle = stringResource(R.string.scholarship_news),
                        titleColor = MaterialTheme.colorScheme.notificationType3,
                        isContentLoading = uiState.mainContentState.isLoading,
                        contents = uiState.mainContentState.notificationScholarship,
                        onMoreClicked = {
                            viewModel.sendUiEvent(
                                HomeEvent.RequestMore(NoticeCategory.SCHOLARSHIP_NEWS)
                            )
                        }
                    ) {
                        viewModel.sendUiEvent(HomeEvent.RequestNoticeDetail(it.nttId, it.url))
                    }
                }

                item {
                    NotificationPreviewList(
                        listTitle = stringResource(R.string.event_news),
                        titleColor = MaterialTheme.colorScheme.notificationType4,
                        isContentLoading = uiState.mainContentState.isLoading,
                        contents = uiState.mainContentState.notificationEvent,
                        onMoreClicked = {
                            viewModel.sendUiEvent(
                                HomeEvent.RequestMore(NoticeCategory.EVENT_NEWS)
                            )
                        }
                    ) {
                        viewModel.sendUiEvent(HomeEvent.RequestNoticeDetail(it.nttId, it.url))
                    }
                }

                item {
                    NotificationPreviewList(
                        listTitle = stringResource(R.string.employment_news),
                        titleColor = MaterialTheme.colorScheme.notificationType5,
                        isContentLoading = uiState.mainContentState.isLoading,
                        contents = uiState.mainContentState.notificationEmployment,
                        onMoreClicked = {
                            viewModel.sendUiEvent(
                                HomeEvent.RequestMore(NoticeCategory.EMPLOYMENT_NEWS)
                            )
                        }
                    ) {
                        viewModel.sendUiEvent(HomeEvent.RequestNoticeDetail(it.nttId, it.url))
                    }
                }

                item {
                    Spacer(Modifier.height(bottomPadding))
                }
            }
        }
    }
}