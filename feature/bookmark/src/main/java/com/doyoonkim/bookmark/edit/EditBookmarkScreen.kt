package com.doyoonkim.bookmark.edit

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresPermission
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doyoonkim.bookmark.viewmodel.EditBookmarkViewModel
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.common.navigation.NoticeDetail
import com.doyoonkim.common.ui.DateTimePicker
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.containerBackground
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.ui.NotificationPreviewCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBookmarkScreen(
    modifier: Modifier = Modifier,
    viewModel: EditBookmarkViewModel,
    bookmarkInfo: BookmarkInfo,
    onNoticeSelected: (NoticeDetail) -> Unit,
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val adjustImePadding = Modifier.consumeWindowInsets(WindowInsets.ime).imePadding()

    BackHandler {
        onBackPressed()
    }

    // Once per composition?
    LaunchedEffect(Unit) {
        viewModel.apply {
            createBookmarkInfo(bookmarkInfo)
            getBookmarkByNoticeId(bookmarkInfo.noticeId)
            getNoticeById(bookmarkInfo.noticeId)
        }
    }

    LaunchedEffect(uiState.isReminderRequested) {
        if (uiState.timeForRemind == 0L)
            viewModel.updateReminderOptions(timeForRemind = System.currentTimeMillis())
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        NotificationPreviewCard(
            modifier = Modifier.padding(5.dp),
            isLoading = false,
            notificationTitle = bookmarkInfo.noticeTitle,
            notificationInfo = bookmarkInfo.noticeInfo
        ) {
            // Request Full Content
            uiState.targetNotice?.let { onNoticeSelected(NoticeDetail(it.nttId, it.url, false)) }
        }

        Spacer(Modifier.height(30.dp))

        Text(
            text = stringResource(R.string.subtitle_set_reminder),
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.title
        )

        Column(
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                .background(Color.Transparent),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                    .background(Color.Transparent)
                    .clip(RoundedCornerShape(10.dp))
                    .border(2.dp, MaterialTheme.colorScheme.containerBackground)
                    .padding(start = 10.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.subtitle_get_reminder),
                    textAlign = TextAlign.Start,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.title,
                    modifier = Modifier.padding(10.dp).weight(5f)
                )

                Switch(
                    checked = uiState.isReminderRequested,
                    enabled = true,
                    modifier = Modifier.padding(10.dp).weight(1f),
                    onCheckedChange = { viewModel.updateReminderOptions(requested = !uiState.isReminderRequested) }
                )
            }
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                visible = uiState.isReminderRequested,
                enter = slideInVertically(),
                exit = slideOutVertically()
            ) {
                Surface(
                    modifier = Modifier.wrapContentSize()
                        .border(
                            2.dp, MaterialTheme.colorScheme.containerBackground, RoundedCornerShape(10.dp)
                        )
                        .background(Color.Transparent),
                    color = Color.Transparent
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(20.dp)
                            .clickable { viewModel.updateReminderOptions(
                                isDatePickerVisible = !uiState.datePickerVisible
                            ) },
                        text = uiState.timeForRemind.toFormattedDate(
                            SimpleDateFormat("yyyy/MM/dd a HH:mm", Locale.getDefault())
                        ),
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.title
                    )
                }
            }
        }

        Spacer(Modifier.height(15.dp))

        Text(
            text = stringResource(R.string.subtitle_notes),
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.title
        )

        Box(
            modifier = Modifier.fillMaxWidth()
                .fillMaxHeight()
                .weight(1f)
                .padding(top = 5.dp, bottom = 20.dp)
                .then(adjustImePadding)
        ) {
            TextField(
                modifier = Modifier.fillMaxSize().padding(bottom = 5.dp),
                value = uiState.bookmarkNote,
                placeholder = { Text(text = stringResource(R.string.placeholder_notes)) },
                enabled = true,
                onValueChange = {
                    viewModel.updateBookmarkNotes(it)
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.title,
                    unfocusedTextColor = MaterialTheme.colorScheme.subTitle,
                    focusedContainerColor = MaterialTheme.colorScheme.containerBackground,
                    unfocusedContainerColor = MaterialTheme.colorScheme.containerBackground,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(15.dp)
            )

            Text(
                text = "${uiState.bookmarkNote.length}/500",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.subTitle,
                modifier = Modifier.wrapContentSize()
                    .padding(15.dp)
                    .align(Alignment.BottomEnd)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            val coroutineScope = rememberCoroutineScope()
            Button(
                modifier = Modifier.wrapContentHeight()
                    .weight(1f),
                enabled = true,
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    viewModel.submitBookmark()
                    coroutineScope.launch {
                        delay(500L)
//                        onSaveClicked(uiState.bookmarkInstance)
                    }
                }
            ) {
                Text(
                    text = stringResource(R.string.btn_save),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
            }

            if (!uiState.requireCreation) {
                OutlinedButton(
                    modifier = Modifier.wrapContentHeight().weight(1f),
                    enabled = true,
                    shape = RoundedCornerShape(10.dp),
                    onClick = {
                        viewModel.removeBookmark()
//                        onSaveClicked(null)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.btn_delete),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }

    // DateTimePicker
    AnimatedVisibility(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().imePadding(),
        visible = uiState.datePickerVisible,
        enter = slideInVertically(initialOffsetY = { it + it / 2 }),
        exit = slideOutVertically(targetOffsetY = { it / 2 })
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(start = 5.dp, end = 5.dp)
                .clickable { viewModel.updateReminderOptions(
                    isDatePickerVisible = !uiState.datePickerVisible)
                }
        ) {
            DateTimePicker(
                modifier = Modifier.padding(5.dp)
                    .shadow(5.dp)
                    .align(Alignment.BottomCenter)
            ) {
                if (it != null) {
                    Log.d("EditBookmark", "${it}")
                    viewModel.updateReminderOptions(
                        timeForRemind = it,
                        isDatePickerVisible = !uiState.datePickerVisible
                    )
                }
            }
        }
    }

    if (uiState.isCompleted) {
        BasicAlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.

            }
        ) {
            Surface(
                modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (uiState.isSuccessful) {
                            stringResource(R.string.text_save_successful)
                        } else {
                            stringResource(R.string.text_save_unsuccessful)
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(
                        onClick = {
                            viewModel.updateCompletionStatus(false)
                            if (uiState.isSuccessful) onBackPressed()
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(stringResource(R.string.btn_confirm))
                    }
                }
            }
        }
    }
}

private fun Long.toFormattedDate(f: SimpleDateFormat): String {
    return f.apply { timeZone = TimeZone.getTimeZone(ZoneId.systemDefault()) }.format(Date(this))
}