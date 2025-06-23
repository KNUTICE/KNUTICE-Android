package com.doyoonkim.main.notice

import androidx.activity.compose.BackHandler
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doyoonkim.main.viewmodel.NoticeSearchViewModel
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.theme.variantPurple
import com.doyoonkim.common.ui.NotificationPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticeSearchScreen(
    modifier: Modifier = Modifier,
    viewModel: NoticeSearchViewModel,
    onBackPressed: () -> Unit,
    onNoticeSelected: (Int, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val localFocusManager = LocalFocusManager.current

    BackHandler { onBackPressed() }

    LaunchedEffect(uiState.searchKeyword) {
        viewModel.observeKeywordInput()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(top = 5.dp),
                title = {
                    TextField(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentHeight()
                            .padding(2.dp),
                        value = uiState.searchKeyword,
                        placeholder = { Text(stringResource(R.string.title_search)) },
                        onValueChange = { viewModel.updateSearchKeyword(it) },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.title,
                            unfocusedTextColor = MaterialTheme.colorScheme.subTitle,
                            focusedContainerColor = MaterialTheme.colorScheme.secondaryBackground,
                            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryBackground,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(15.dp),
                        singleLine = true
                    )
                },
                actions = {
                    IconButton(
                        onClick = { onBackPressed() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.title
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.displayBackground,
                    titleContentColor = MaterialTheme.colorScheme.title
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.displayBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.TopCenter),
                contentPadding = PaddingValues(3.dp)
            ) {
                items(uiState.fetchResult) { notice ->
                    HorizontalDivider(
                        Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp),
                        color = MaterialTheme.colorScheme.secondaryBackground
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNoticeSelected(notice.nttId, notice.url) }
                    ) {
                        NotificationPreview(
                            modifier = Modifier.fillMaxWidth(),
                            isLoading = uiState.isFetching,
                            isImageContained = notice.imageUrl != null,
                            notificationTitle = notice.title,
                            notificationInfo = "[${notice.departName}] ${notice.timestamp}",
                            imageUrl = notice.imageUrl ?: ""
                        )
                    }
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = uiState.isFetching,
                modifier = Modifier.wrapContentSize().align(Alignment.Center),
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.variantPurple,
                    trackColor = MaterialTheme.colorScheme.secondaryBackground
                )
            }
        }
    }

}