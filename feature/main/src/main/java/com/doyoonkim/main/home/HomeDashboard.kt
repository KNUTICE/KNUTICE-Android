package com.doyoonkim.main.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doyoonkim.common.MajorResources
import com.doyoonkim.common.NoticeResources
import com.doyoonkim.common.R
import com.doyoonkim.common.navigation.Destination
import com.doyoonkim.common.theme.buttonOnBackground
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.theme.variantPurple
import com.doyoonkim.common.ui.DashboardPlaceholder
import com.doyoonkim.common.ui.EntryPointButton
import com.doyoonkim.common.ui.HorizontalContentPager
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
fun HomeDashboard(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    bottomPadding: Dp = 0.dp,
    onSettingsRequested: () -> Unit,
    onGoBackAction: () -> Unit,
    onMoreNoticeRequested: (Destination) -> Unit,
    onMoreMajorNoticeRequested: () -> Unit,
    onFullContentRequested: (Int, String) -> Unit,
    onTipClicked: (TipCategory, String) -> Unit
) {
    // UiState
    val uiState by viewModel.uiState.collectAsState()

    // Back Button/Gesture actions
    BackHandler {
        viewModel.sendUiEvent(HomeEvent.GoBack)
    }

    // SideEffect Handling
    LaunchedEffect(Unit) {
        viewModel.sendUiEvent(HomeEvent.RequestMainContents)

        // SideEffect Channel Subscription
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
                is HomeSideEffect.NavToMoreMajorNotices -> {
                    onMoreMajorNoticeRequested()
                }
                is HomeSideEffect.NavToTipDetail -> {
                    with (effect) {
                        onTipClicked(category, url)
                    }
                }
                is HomeSideEffect.NavToSettings -> {
                    onSettingsRequested()
                }
                is HomeSideEffect.NavToBack -> {
                    onGoBackAction()
                }
            }
        }
    }

    // Top Level Scaffold
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
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(bottom = bottomPadding),
                imageResource = R.drawable.wifi,
                contentText = stringResource(R.string.error_no_network_connection)
            )
        } else {
            LazyColumn(
                modifier = modifier
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.displayBackground),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                if (!uiState.tipState.isError && uiState.tipState.tips.isNotEmpty()) {
                    item("tip") {
                        TipPager(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .padding(5.dp),
                            tips = uiState.tipState.tips
                        ) {
                            viewModel.sendUiEvent(HomeEvent.RequestTipDetail(TipCategory.UPDATES, it))
                        }
                        Spacer(Modifier.height(15.dp))
                    }
                }

                item("Service Entry points") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(horizontal = 7.dp)
                            .background(Color.Transparent),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        EntryPointButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            text = "학식 조회",
                            painter = painterResource(R.drawable.icon_dining_menu),
                            containerColor = MaterialTheme.colorScheme.secondaryBackground,
                            textColor = MaterialTheme.colorScheme.title,
                            size = 140.dp
                        )

                        EntryPointButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            text = "열람실 현황",
                            painter = painterResource(R.drawable.icon_study_area),
                            containerColor = MaterialTheme.colorScheme.secondaryBackground,
                            textColor = MaterialTheme.colorScheme.title,
                            size = 140.dp
                        )
                    }
                }

                item("main notice area") {
                    HorizontalContentPager(
                        modifier = Modifier.fillMaxWidth(),
                        progressDelay = 7000L,
                        startingPage = 0,
                        size = 5
                    ) { index ->
                        val targetKey = index % 5
                        val category = NoticeCategory.entries[targetKey]
                        val notices = when (category) {
                            NoticeCategory.GENERAL_NEWS -> {
                                uiState.mainContentState.notificationGeneral
                            }
                            NoticeCategory.ACADEMIC_NEWS -> {
                                uiState.mainContentState.notificationAcademic
                            }
                            NoticeCategory.SCHOLARSHIP_NEWS -> {
                                uiState.mainContentState.notificationScholarship
                            }
                            NoticeCategory.EVENT_NEWS -> {
                                uiState.mainContentState.notificationEvent
                            }
                            NoticeCategory.EMPLOYMENT_NEWS -> {
                                uiState.mainContentState.notificationEmployment
                            }
                            NoticeCategory.Unspecified -> {
                                emptyList()
                            }
                        }

                        NotificationPreviewList(
                            modifier = Modifier
                                .padding(7.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(MaterialTheme.colorScheme.secondaryBackground),
                            listTitle = stringResource(
                                NoticeResources.getStringResourcesByCategory(category.toString())
                            ),
                            titleColor = NoticeResources.getColorResourceByCategory(category.toString()),
                            isContentLoading = uiState.mainContentState.isLoading,
                            contents = notices,
                            onMoreClicked = {
                                viewModel.sendUiEvent(HomeEvent.RequestMore(category))
                            }
                        ) {
                            viewModel.sendUiEvent(HomeEvent.RequestNoticeDetail(it.nttId, it.url))
                        }

                    }
                }

                item("notice by selected major") {
                    if (uiState.majorNoticesState.isError) {
                        DashboardPlaceholder(
                            modifier = modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(15.dp))
                                .background(MaterialTheme.colorScheme.secondaryBackground),
                            imageResources = R.drawable.outline_school_24,
                            imageColor = MaterialTheme.colorScheme.subTitle,
                            contentText = stringResource(R.string.title_major_select)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.sendUiEvent(HomeEvent.RequestMoreMajorNotices)
                                },
                                modifier = Modifier.wrapContentSize(),
                                enabled = true,
                                shape = RoundedCornerShape(15.dp),
                                colors = ButtonDefaults.buttonColors().copy(
                                    containerColor = MaterialTheme.colorScheme.buttonOnBackground,
                                    contentColor = MaterialTheme.colorScheme.title
                                ),
                                contentPadding = PaddingValues(20.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.text_major_select),
                                    style = TextStyle(
                                        color = MaterialTheme.colorScheme.title,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                        }
                    } else {
                        NotificationPreviewList(
                            modifier = Modifier
                                .padding(7.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(MaterialTheme.colorScheme.secondaryBackground),
                            listTitle = stringResource(
                                MajorResources.getLocalizedString(
                                    uiState.majorNoticesState.subscribed.name
                                )
                            ),
                            titleColor = MaterialTheme.colorScheme.title,
                            isContentLoading = uiState.majorNoticesState.isLoading,
                            contents = uiState.majorNoticesState.majorNotices,
                            onMoreClicked = {
                                viewModel.sendUiEvent(HomeEvent.RequestMoreMajorNotices)
                            }
                        ) {
                            viewModel.sendUiEvent(HomeEvent.RequestNoticeDetail(it.nttId, it.url))
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(bottomPadding))
                }
            }
        }
    }
}