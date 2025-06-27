package com.doyoonkim.bookmark.list

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doyoonkim.bookmark.viewmodel.BookmarkListViewModel
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.theme.variantPurple
import com.doyoonkim.common.ui.NotificationPreviewCardMarked
import com.doyoonkim.common.ui.TopAppBarDropdownMenu
import com.doyoonkim.common.ui.TopAppBarWithActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkListScreen(
    modifier: Modifier = Modifier,
    viewModel: BookmarkListViewModel,
    bottomPadding: Dp = 0.dp,
    onSettingsRequested: () -> Unit,
    onBookmarkSelected: (BookmarkInfo) -> Unit,
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler { onBackPressed() }

    LaunchedEffect(uiState.isRequested) {
        if (uiState.isRequested) viewModel.requestBookmarks(pageNumber = uiState.pageNumber)
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
                        stringResource(R.string.text_oldest)
                    ),
                    onMenuSelected = { index ->
                        viewModel.updateSortOption(index)
                    }
                )
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

        if (uiState.bookmarks.isEmpty()) {
            Column(
                modifier = modifier.fillMaxSize().padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(3.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.wrapContentSize()
                        .weight(1f)
                ) {
                    if (uiState.isRequested) {
                        Surface(
                            modifier = Modifier.wrapContentSize()
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
                        Text(
                            text = stringResource(R.string.text_no_bookmark),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.subTitle,
                            modifier = Modifier.wrapContentSize()
                                .align(Alignment.Center)
                        )
                    }
                }
                Spacer(Modifier.height(bottomPadding))
            }
        } else {
            LazyColumn(
                modifier = modifier.wrapContentHeight()
                    .fillMaxWidth()
                    .padding(top = innerPadding.calculateTopPadding() + 12.dp)
                    .background(Color.Transparent),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(uiState.bookmarks.size) { index ->
                    val item = uiState.bookmarks[index]
                    Log.d("BookmarkComposable", "Index: $index Element: $item")

                    NotificationPreviewCardMarked(
                        noticeTitle = item.noticeTitle,
                        noticeSubtitle = "${item.updatedAt}",
                        onItemClicked = {
                            onBookmarkSelected(
                                item.run {
                                    BookmarkInfo(
                                        noticeId = this.noticeId,
                                        noticeTitle = this.noticeTitle,
                                        noticeInfo = this.noticeCategory
                                    )
                                }
                            )
                        }
                    )

                    if (index == uiState.bookmarks.size - 1 && !uiState.isReachEnd) {
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
                        viewModel.updateBookmarkRequestStatus(true)
                    }

                }
                item {
                    Spacer(Modifier.height(bottomPadding))
                }
            }
        }
    }
}