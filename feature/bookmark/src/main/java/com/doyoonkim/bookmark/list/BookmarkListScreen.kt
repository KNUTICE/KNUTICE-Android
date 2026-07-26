package com.doyoonkim.bookmark.list

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doyoonkim.bookmark.contract.BookmarkListEvent
import com.doyoonkim.bookmark.contract.BookmarkListSideEffect
import com.doyoonkim.bookmark.viewmodel.BookmarkListViewModel
import com.doyoonkim.common.R
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.theme.variantPurple
import com.doyoonkim.common.ui.LocalHomeSafeBottomPadding
import com.doyoonkim.common.ui.NotificationPreviewCardMarked
import com.doyoonkim.common.ui.OnForegroundFocusEffect
import com.doyoonkim.common.ui.PlaceholderScreen
import com.doyoonkim.common.ui.TopAppBarDropdownMenu
import com.doyoonkim.common.ui.TopAppBarWithActions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BookmarkListScreen(
    modifier: Modifier = Modifier,
    viewModel: BookmarkListViewModel,
    onSettingsRequested: () -> Unit,
    onBookmarkSelected: (BookmarkInfo) -> Unit,
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler { onBackPressed() }

    OnForegroundFocusEffect {
        viewModel.sendUiEvent(BookmarkListEvent.CheckSyncStatus)
        viewModel.sendUiEvent(BookmarkListEvent.RequestBookmark)
    }

    LaunchedEffect(Unit) {
        viewModel.uiSideEffect.collect { sideEffect ->
            when (sideEffect) {
                is BookmarkListSideEffect.NavTo -> {
                    onBookmarkSelected(sideEffect.dest)
                }
                is BookmarkListSideEffect.Settings -> onSettingsRequested()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBarWithActions(
                titleText = stringResource(R.string.bottom_bar_bookmark)
            ) {
                // Sort Option
                TopAppBarDropdownMenu(
                    iconVector = Icons.Default.MoreVert,
                    iconTint = MaterialTheme.colorScheme.title,
                    menuContainerColor = MaterialTheme.colorScheme.onAnyBackground,
                    menuContentColor = MaterialTheme.colorScheme.title,
                    menuOptions = listOf(
                        stringResource(R.string.text_newest),
                        stringResource(R.string.text_oldest),
                        stringResource(R.string.text_updated_newest),
                        stringResource(R.string.text_updated_oldest)
                    ),
                    onMenuSelected = { index ->
                        viewModel.sendUiEvent(BookmarkListEvent.UpdateSortOption(index))
                    }
                )
                IconButton(
                    onClick = { viewModel.sendUiEvent(BookmarkListEvent.RequestSettings) }
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

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.bookmarks.isEmpty()) {
                // Read HomeScreen content safe bottom padding
                val bottomPadding = LocalHomeSafeBottomPadding.current

                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(bottom = bottomPadding)
                        .weight(1f)
                ) {
                    if (uiState.isLoading) {
                        Surface(
                            modifier = Modifier
                                .wrapContentSize()
                                .align(Alignment.Center)
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
                    } else {
                        PlaceholderScreen(
                            modifier = Modifier.fillMaxSize(),
                            imageResource = R.drawable.bookmark,
                            contentText = stringResource(R.string.error_no_bookmark)
                        )
                    }
                }
            } else {
                if (uiState.isSyncRequired) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp)
                            .background(Color.Transparent),
                        color = MaterialTheme.colorScheme.onAnyBackground,
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.text_sync_warning),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Start,
                            color = Color.Red,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }

                // Read HomeScreen content safe bottom padding
                val bottomPadding = LocalHomeSafeBottomPadding.current

                LazyColumn(
                    modifier = modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .background(Color.Transparent),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(bottom = bottomPadding)
                ) {
                    items(uiState.bookmarks.size) { index ->
                        val item = uiState.bookmarks[index]
                        Log.d("BookmarkComposable", "Index: $index Element: $item")

                        NotificationPreviewCardMarked(
                            noticeTitle = item.noticeTitle,
                            noticeSubtitle = with(item) {
                                if (updatedAt > createdAt) {
                                    stringResource(R.string.text_updated_at) +
                                        " ${updatedAt.toFormattedDate()}"
                                } else {
                                    stringResource(R.string.text_created_at) +
                                        " ${createdAt.toFormattedDate()}"
                                }
                            },
                            noticeCategory = item.noticeCategory,
                            onItemClicked = {
                                viewModel.sendUiEvent(
                                    BookmarkListEvent.RequestBookmarkDetail(index)
                                )
                            }
                        )

                        LaunchedEffect(index) {
                            // Safe-execution of request more bookmark event
                            if (index == uiState.bookmarks.size - 1 && !uiState.isReachEnd) {
                                viewModel.sendUiEvent(BookmarkListEvent.RequestMoreBookmark)
                            }
                        }
                    }

                    if (uiState.isLoading && !uiState.isRefreshing) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
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
                        }
                    }
                }
            }
        }
    }
}

fun Long.toFormattedDate() =
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(this))
