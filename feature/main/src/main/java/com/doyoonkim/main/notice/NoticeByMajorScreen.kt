package com.doyoonkim.main.notice

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doyoonkim.common.CollegeResource
import com.doyoonkim.common.MajorResources
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.theme.variantPurple
import com.doyoonkim.common.ui.LocalHomeSafeBottomPadding
import com.doyoonkim.common.ui.NotificationPreview
import com.doyoonkim.common.ui.PlaceholderScreen
import com.doyoonkim.main.contract.NoticeByMajorEvent
import com.doyoonkim.main.contract.NoticeByMajorSideEffect
import com.doyoonkim.main.viewmodel.NoticeByMajorViewModel
import com.doyoonkim.model.CollegeType
import com.doyoonkim.model.MajorCategory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun NoticeByMajorScreen(
    modifier: Modifier = Modifier,
    viewModel: NoticeByMajorViewModel,
    onGoBackRequested: () -> Unit,
    onSettingRequested: () -> Unit,
    onNoticeDetailRequested: (Int, String) -> Unit
) {
    // Test States
    val uiState by viewModel.uiState.collectAsState()
    // Bottom Modal Sheet State
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val localCoroutineScope = rememberCoroutineScope()
    // Pull to Refresh State (Consider replace it with Material3 Library)
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = {
            viewModel.run {
                viewModel.sendUiEvent(NoticeByMajorEvent.RequestRefresh)
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.sendUiEvent(NoticeByMajorEvent.CheckSubscribedMajor)
        viewModel.uiSideEffect.collectLatest { sideEffect ->
            when (sideEffect) {
                is NoticeByMajorSideEffect.GoBack -> {
                    onGoBackRequested()
                }
                is NoticeByMajorSideEffect.NavToSettings -> {
                    onSettingRequested()
                }
                is NoticeByMajorSideEffect.NavToNotice -> {
                    onNoticeDetailRequested(sideEffect.nttId, sideEffect.url)
                }
            }
        }
    }

    BackHandler {
        viewModel.sendUiEvent(NoticeByMajorEvent.RequestGoBack)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    TextButton(
                        onClick = {
                            viewModel.sendUiEvent(NoticeByMajorEvent.RequestBottomSheetExpand)
                        }
                    ) {
                        Row(
                            modifier = Modifier.wrapContentSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            // Text Size Adjustment
                            var textSize by remember { mutableStateOf(20.sp) }
                            Text(
                                text = stringResource(
                                    MajorResources.getLocalizedString(uiState.targetMajor.name)
                                ),
                                style = TextStyle(
                                    fontSize = textSize,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.title
                                ),
                                onTextLayout = { result ->
                                    if (result.hasVisualOverflow) {
                                        textSize *= 0.6f
                                    }
                                },
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                            Spacer(Modifier.width(2.dp))
                            Icon(
                                painter = painterResource(R.drawable.baseline_arrow_forward_ios_24),
                                contentDescription = "Select or Change Major Selection",
                                tint = MaterialTheme.colorScheme.title,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.sendUiEvent(NoticeByMajorEvent.RequestSettings) }
                    ) {
                        Image(
                            painter = painterResource(R.drawable.baseline_settings_24),
                            contentDescription = "Settings",
                            modifier = Modifier.wrapContentSize(),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.title)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.displayBackground,
                    titleContentColor = MaterialTheme.colorScheme.title,
                    navigationIconContentColor = MaterialTheme.colorScheme.title
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.displayBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pullRefresh(pullRefreshState),
            contentAlignment = Alignment.TopCenter
        ) { 
            if (uiState.targetMajor == MajorCategory.UNSPECIFIED) {
                // Read HomeScreen content safe bottom padding
                val bottomPadding = LocalHomeSafeBottomPadding.current

                PlaceholderScreen(
                    modifier = Modifier.fillMaxSize().padding(bottom = bottomPadding),
                    imageResource = R.drawable.plus_clay,
                    contentText = stringResource(R.string.text_major_select)
                )
            } else {
                // Read HomeScreen content safe bottom padding
                val bottomPadding = LocalHomeSafeBottomPadding.current

                if (uiState.notices.isEmpty()) {
                    if (uiState.isLoading) {
                        Surface(
                            modifier = Modifier
                                .wrapContentSize()
                                .align(Alignment.Center)
                                .padding(bottom = bottomPadding)
                                .clip(RoundedCornerShape(15.dp))
                                .background(Color.Transparent),
                            color = MaterialTheme.colorScheme.onAnyBackground
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(15.dp),
                                color = MaterialTheme.colorScheme.variantPurple,
                                trackColor = MaterialTheme.colorScheme.onAnyBackground
                            )
                        }
                    }
                    if (!uiState.isFetchable) {
                        PlaceholderScreen(
                            modifier= Modifier.fillMaxSize().padding(bottom = bottomPadding),
                            imageResource = R.drawable.question_mark,
                            contentText = stringResource(R.string.text_no_major_notice)
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Transparent),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(bottom = bottomPadding)
                ) {
                    items(uiState.notices.size) { index ->
                        val item = uiState.notices[index]
                        Row(
                            modifier = Modifier.wrapContentSize()
                                .clickable {
                                    viewModel.sendUiEvent(NoticeByMajorEvent.RequestNoticeDetail(item))
                                }
                        ) {
                            NotificationPreview(
                                isLoading = uiState.isRefreshing,
                                isImageContained = item.imageUrl != null,
                                notificationTitle = item.title,
                                notificationInfo = "[${item.departName}] ${item.timestamp}",
                                imageUrl = item.imageUrl ?: ""
                            )
                        }

                        if (index != uiState.notices.size) {
                            HorizontalDivider(
                                Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.onAnyBackground,
                                thickness = 1.2.dp
                            )
                        }

                        if (index == uiState.notices.size - 1 && uiState.isFetchable) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .wrapContentHeight(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.wrapContentSize(),
                                    color = MaterialTheme.colorScheme.variantPurple,
                                    trackColor = MaterialTheme.colorScheme.displayBackground
                                )
                            }
                            viewModel.sendUiEvent(NoticeByMajorEvent.RequestNotice)
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = uiState.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
                    .padding(top = 10.dp),
                backgroundColor = MaterialTheme.colorScheme.onAnyBackground,
                contentColor = MaterialTheme.colorScheme.variantPurple,
            )
        }

        // Bottom Sheet for Major Selection
        if (uiState.isBottomSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = {
                    viewModel.sendUiEvent(NoticeByMajorEvent.RequestBottomSheetHidden)
                },
                sheetState = bottomSheetState,
                containerColor = MaterialTheme.colorScheme.secondaryBackground,
                scrimColor = Color.Transparent,
                modifier = Modifier.background(Color.Transparent)
            ) {
                val major = MajorCategory.entries.groupBy { it.collegeType }
                LazyColumn(
                    modifier = Modifier.height(
                        with(LocalConfiguration.current) { this.screenHeightDp * 0.45f }.dp
                    ).padding(horizontal = 5.dp)
                ) {
                    major.forEach { (college, majors) ->
                        if (college != CollegeType.UNSPECIFIED) {
                            stickyHeader {
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .wrapContentHeight()
                                        .background(MaterialTheme.colorScheme.secondaryBackground),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(
                                            CollegeResource.getLocalizedCollegeString(college.name)
                                        ),
                                        style = TextStyle(
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.title
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                            .padding(vertical = 10.dp, horizontal = 5.dp)
                                    )
                                }

                            }

                            items(majors) { major ->
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .wrapContentHeight()
                                        .padding(horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Text(
                                        text = stringResource(MajorResources.getLocalizedString(major.name)),
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = MaterialTheme.colorScheme.subTitle
                                        ),
                                        modifier = Modifier.weight(9f)
                                    )
                                    RadioButton(
                                        selected = uiState.targetMajor == major,
                                        onClick = {
                                            // Dismiss Bottom Sheet.
                                            localCoroutineScope.launch {
                                                bottomSheetState.hide()
                                            }.invokeOnCompletion {
                                                // update target major.
                                                viewModel.sendUiEvent(
                                                    NoticeByMajorEvent.RequestMajorSubscriptionChange(major)
                                                )
                                                viewModel.sendUiEvent(NoticeByMajorEvent.RequestBottomSheetHidden)
                                            }
                                        },
                                        modifier = Modifier.wrapContentSize().weight(1f),
                                        enabled = true,
                                        colors = RadioButtonDefaults.colors().copy(
                                            selectedColor = MaterialTheme.colorScheme.variantPurple,
                                            unselectedColor = MaterialTheme.colorScheme.subTitle,
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko-rKR")
@Composable
fun NoticeByMajorScreen_Preview() {
//    NoticeByMajorScreen()
}