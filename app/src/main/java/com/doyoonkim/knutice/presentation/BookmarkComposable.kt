package com.doyoonkim.knutice.presentation

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.doyoonkim.knutice.model.Notice
import com.doyoonkim.knutice.presentation.component.NotificationPreviewCardMarked
import com.doyoonkim.knutice.ui.theme.displayBackground
import com.doyoonkim.knutice.viewModel.BookmarkViewModel
import kotlinx.coroutines.launch

@Composable
fun BookmarkComposable(
    modifier: Modifier = Modifier,
    viewModel: BookmarkViewModel = hiltViewModel(),
    onEachItemClicked: (Notice) -> Unit = {  },
    onBackPressed: () -> Unit = {  }
) {
    val uiState by viewModel.uiState.collectAsState()

    BackHandler {
        onBackPressed()
    }

    SideEffect {
        viewModel.getAllBookmarks()
        Log.d("BookmarkComposable", "Side Effect Called")
    }

    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.displayBackground)
            .systemBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.wrapContentHeight()
                .fillMaxWidth()
                .padding(top = 12.dp)
                .background(Color.Transparent),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(uiState.bookmarks) {
                // Being called 3 times
                Log.d("BookmarkComposable", "Element: $it")
                NotificationPreviewCardMarked(
                    noticeTitle = it.second.title,
                    noticeSubtitle = "[${it.second.departName}] ${it.second.timestamp}",
                    onItemClicked = { onEachItemClicked(it.second) }
                )
            }
        }
    }
}