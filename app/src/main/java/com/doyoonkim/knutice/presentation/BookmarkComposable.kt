package com.doyoonkim.knutice.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.doyoonkim.knutice.presentation.component.NotificationPreviewCardMarked
import com.doyoonkim.knutice.ui.theme.displayBackground
import com.doyoonkim.knutice.viewModel.BookmarkViewModel

@Composable
fun BookmarkComposable(
    modifier: Modifier = Modifier,
    viewModel: BookmarkViewModel = hiltViewModel()
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.displayBackground)
            .systemBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.wrapContentHeight()
                .fillMaxWidth()
                .background(Color.Transparent),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(1) {
                NotificationPreviewCardMarked()
            }
        }
    }
}