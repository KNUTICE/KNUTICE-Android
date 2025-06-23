package com.doyoonkim.bookmark.list

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doyoonkim.bookmark.viewmodel.BookmarkListViewModel
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.ui.NotificationPreviewCardMarked

@Composable
fun BookmarkListScreen(
    modifier: Modifier = Modifier,
    viewModel: BookmarkListViewModel,
    bottomPadding: Dp = 0.dp,
    onBookmarkSelected: (BookmarkInfo) -> Unit,
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler { onBackPressed() }

    LaunchedEffect(Unit) {
        viewModel.getAllBookmarks()
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        if (uiState.bookmarks.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.text_no_bookmark),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.subTitle
                )
                Spacer(Modifier.height(bottomPadding))
            }
        } else {
            LazyColumn(
                modifier = Modifier.wrapContentHeight()
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = bottomPadding)
                    .background(Color.Transparent),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(
                    items = uiState.bookmarks,
                    key = { it.second.nttId }
                ) {
                    // Being called 3 times
                    Log.d("BookmarkComposable", "Element: $it")
                    NotificationPreviewCardMarked(
                        noticeTitle = it.second.title,
                        noticeSubtitle = "[${it.second.departName}] ${it.second.timestamp}",
                        onItemClicked = { onBookmarkSelected(
                            it.second.run {
                                BookmarkInfo(
                                    noticeId = this.nttId,
                                    noticeTitle = this.title,
                                    noticeInfo = "[${this.departName}] ${this.timestamp}"
                                )
                            }
                        ) }
                    )
                }
            }
        }
    }
}