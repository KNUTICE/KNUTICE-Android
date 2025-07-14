package com.doyoonkim.main.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doyoonkim.common.theme.notificationType1
import com.doyoonkim.common.theme.notificationType2
import com.doyoonkim.common.theme.notificationType3
import com.doyoonkim.common.theme.notificationType4
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.R
import com.doyoonkim.common.navigation.Destination
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.ui.NotificationPreviewCard
import com.doyoonkim.common.ui.PlaceholderScreen
import com.doyoonkim.common.ui.TipCategory
import com.doyoonkim.common.ui.TipContainer
import com.doyoonkim.common.ui.TopAppBarWithActions
import com.doyoonkim.main.viewmodel.HomeViewModel
import com.doyoonkim.model.NoticeVO

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    bottomPadding: Dp = 0.dp,
    onSearchRequested: () -> Unit,
    onSettingsRequested: () -> Unit,
    onGoBackAction: () -> Unit,
    onMoreNoticeRequested: (Destination) -> Unit,
    onFullContentRequested: (Int, String) -> Unit,
    onTipClicked: (TipCategory, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Back button/gesture actions
    BackHandler {
        onGoBackAction()
    }

    LaunchedEffect(Unit) {
        viewModel.getTopThreeNotices()
        viewModel.getTips()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBarWithActions(
                titleText = stringResource(R.string.app_name)
            ) {
                IconButton(
                    onClick = onSearchRequested
                ) {
                    Image(
                        painter = painterResource(R.drawable.baseline_search_24),
                        contentDescription = "Search",
                        modifier = Modifier.wrapContentSize(),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.title)
                    )
                }
                IconButton(
                    onClick = onSettingsRequested
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

        if (uiState.isError) {
            PlaceholderScreen(
                modifier = modifier.padding(innerPadding).padding(bottom = bottomPadding),
                imageResource = R.drawable.outline_network_check_24,
                contentText = stringResource(R.string.error_no_network_connection)
            )
        } else {
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState(0)),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.tips.isNotEmpty()) {
                    TipContainer(
                        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                        tipCategory = TipCategory.SYS_NOTICE,
                        containerColor = MaterialTheme.colorScheme.onAnyBackground,
                        tipText = uiState.tips[0].title,
                    ) {
                        onTipClicked(
                            TipCategory.SYS_NOTICE,
                            uiState.tips[0].url
                        )
                    }
                }

                NotificationPreviewList (
                    listTitle = stringResource(R.string.general_news),
                    titleColor = MaterialTheme.colorScheme.notificationType1,
                    isContentLoading = uiState.isLoading,
                    contents = uiState.notificationGeneral,
                    onMoreClicked = { onMoreNoticeRequested(Destination.MORE_GENERAL) }
                ) {
                    onFullContentRequested(it.nttId, it.url)
                }

                NotificationPreviewList(
                    listTitle = stringResource(R.string.academic_news),
                    titleColor = MaterialTheme.colorScheme.notificationType2,
                    isContentLoading = uiState.isLoading,
                    contents = uiState.notificationAcademic,
                    onMoreClicked = { onMoreNoticeRequested(Destination.MORE_ACADEMIC) }
                ) {
                    onFullContentRequested(it.nttId, it.url)
                }

                NotificationPreviewList(
                    listTitle = stringResource(R.string.scholarship_news),
                    titleColor = MaterialTheme.colorScheme.notificationType3,
                    isContentLoading = uiState.isLoading,
                    contents = uiState.notificationScholarship,
                    onMoreClicked = { onMoreNoticeRequested(Destination.MORE_SCHOLARSHIP) }
                ) {
                    onFullContentRequested(it.nttId, it.url)
                }

                NotificationPreviewList(
                    listTitle = stringResource(R.string.event_news),
                    titleColor = MaterialTheme.colorScheme.notificationType4,
                    isContentLoading = uiState.isLoading,
                    contents = uiState.notificationEvent,
                    onMoreClicked = { onMoreNoticeRequested(Destination.MORE_EVENT) }
                ) {
                    onFullContentRequested(it.nttId, it.url)
                }

                Spacer(Modifier.height(bottomPadding))
            }
        }
    }
}

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
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(7.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier.fillMaxWidth().wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.wrapContentHeight().weight(6f),
                text = listTitle,
                color = titleColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )

            TextButton(
                modifier = Modifier.fillMaxWidth().weight(1f),
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
        contents.forEach { content ->
            NotificationPreviewCard(
                notificationTitle = content.title,
                notificationInfo = "[${content.departName}] ${content.timestamp}",
                isLoading = isContentLoading
            ) {
                onNoticeClicked(content)
            }
        }
    }
}