package com.doyoonkim.main.notice

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doyoonkim.common.theme.containerBackground
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.textPurple
import com.doyoonkim.common.ui.NotificationPreview
import com.doyoonkim.main.viewmodel.NoticesInCategoryViewModel
import com.doyoonkim.model.NoticeCategory

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NoticesInCategoryScreen(
    modifier: Modifier,
    category: NoticeCategory = NoticeCategory.Unspecified,
    viewModel: NoticesInCategoryViewModel,
    onBackButtonPressed: () -> Unit = {  },
    onNoticeSelected: (Int, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Back Handler
    BackHandler { onBackButtonPressed() }

     // Pull-to-Refresh
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshRequested,
        onRefresh = { viewModel.requestRefresh() }
    )

    // Fetching notification on entry
    LaunchedEffect(uiState.isNoticesRequested, uiState.isRefreshRequested) {
        if (uiState.isRefreshRequested || uiState.isNoticesRequested)
            viewModel.getNoticesPerPageInCategory(category)
    }

    Box(
        modifier = modifier.fillMaxWidth()
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
            .pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            Modifier.fillMaxWidth().wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            userScrollEnabled = true
        ) {
            items(uiState.notices.size) { index ->
                if (index == uiState.notices.size - 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.wrapContentSize(),
                            color = MaterialTheme.colorScheme.textPurple,
                            trackColor = MaterialTheme.colorScheme.displayBackground
                        )
                    }
                    viewModel.requestMoreNotices()
                } else {
                    if (index != 0) {
                        HorizontalDivider(
                            Modifier.fillMaxWidth(),
                            color =MaterialTheme.colorScheme.containerBackground,
                            thickness = 1.2.dp
                        )
                    }
                    val notice = uiState.notices[index]
                    Row(
                        modifier = Modifier.wrapContentSize()
                            .clickable { onNoticeSelected(notice.nttId, notice.url) }
                    ) {
                        NotificationPreview(
                            isLoading = uiState.isLoading,
                            notificationTitle = notice.title,
                            notificationInfo = "[${notice.departName}] ${notice.timestamp}",
                            isImageContained = notice.imageUrl != null,
                            imageUrl = notice.imageUrl ?: ""
                        )
                    }
                }
            }
        }
        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter)
                .padding(top = 10.dp),
            refreshing = uiState.isRefreshRequested,
            state = pullRefreshState
        )
    }
}