package com.doyoonkim.knutice.presentation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doyoonkim.knutice.model.NoticeCategory
import com.doyoonkim.knutice.presentation.component.NotificationPreview
import com.doyoonkim.knutice.ui.theme.containerBackground
import com.doyoonkim.knutice.viewModel.MoreCategorizedNotificationViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MoreCategorizedNotification(
    modifier: Modifier = Modifier,
    viewModel: MoreCategorizedNotificationViewModel = hiltViewModel(),
    category: NoticeCategory
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshRequested,
        onRefresh = {
            viewModel.requestRefresh()
            viewModel.fetchNotificationPerPage()
        }
    )

    Box(
        modifier = modifier.fillMaxWidth()
            .pullRefresh(pullRefreshState)
    ) {
        LaunchedEffect(Unit) {
            Log.d("MoreCategorizedNotification", "Initialize Notice Category to be fetched")
            if (uiState.currentLastNttId == 0)
                viewModel.setNotificationCategory(category)
            viewModel.fetchNotificationPerPage()
        }
        LazyColumn(
            Modifier.fillMaxWidth().wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            userScrollEnabled = true
        ) {
            items(uiState.notices.size) { index ->
                if (index == uiState.notices.size - 1)
                    viewModel.fetchNotificationPerPage()
                else {
                    val notice = uiState.notices[index]
                    if (!uiState.isLoading) {
                        Divider(
                            Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp),
                            color = MaterialTheme.colorScheme.containerBackground
                        )
                    }
                    NotificationPreview(
                        notificationTitle = notice.title,
                        notificationInfo = "[${notice.departName}] ${notice.timestamp}",
                        isImageContained = notice.imageUrl != "Unknown",
                        imageUrl = notice.imageUrl
                    )
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

