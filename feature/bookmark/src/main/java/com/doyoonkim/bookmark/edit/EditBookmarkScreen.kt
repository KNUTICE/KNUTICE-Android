package com.doyoonkim.bookmark.edit

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.buttonPurple
import com.doyoonkim.common.theme.containerBackground
import com.doyoonkim.common.theme.containerGray
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.textPurple
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.ui.DatePickerDialog
import com.doyoonkim.common.ui.NotificationPreviewCard
import com.doyoonkim.common.ui.TimePickerDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    Column(
        modifier = modifier.fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
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
                .background(Color.Transparent)
                .clip(RoundedCornerShape(10.dp))
                .border(2.dp, MaterialTheme.colorScheme.containerBackground)
                .padding(start = 10.dp, end = 10.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
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
                    checked = uiState.isReminderRequested && uiState.alarmPermissionStatus,
                    enabled = true,
                    modifier = Modifier.padding(10.dp).weight(1f),
                    colors = SwitchDefaults.colors().copy(
                        checkedTrackColor = MaterialTheme.colorScheme.buttonPurple,
                        checkedThumbColor = Color.White
                    ),
                    onCheckedChange = { viewModel.updateReminderOptions(requested = !uiState.isReminderRequested) }
                )
            }

            if (uiState.isReminderRequested) {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PaddingValues(horizontal = 5.dp)),
                    color = MaterialTheme.colorScheme.containerGray
                )
            }

            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                visible = uiState.isReminderRequested,
                enter = slideInVertically(),
                exit = slideOutVertically()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(PaddingValues(vertical = 15.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = stringResource(R.string.text_set_reminder_date_time),
                        textAlign = TextAlign.Start,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.title,
                        modifier = Modifier.padding(10.dp).weight(2f)
                    )

                    DatePickerDialog(
                        initialTime = uiState.timeForRemind
                    ) { year, month, day ->
                        viewModel.updateDateInfo(year, month, day)
                    }
                    TimePickerDialog(
                        initialTime = uiState.timeForRemind
                    ) { hour, min ->
                        viewModel.updateTimeInfo(hour, min)
                    }
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
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.buttonPurple,
                    contentColor = Color.White,
                ),
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
                Button(
                    modifier = Modifier.wrapContentHeight().weight(1f),
                    enabled = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.subTitle,
                        contentColor = Color.White
                    ),
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
                tonalElevation = AlertDialogDefaults.TonalElevation,
                color = MaterialTheme.colorScheme.containerBackground
            ) {
                Column(modifier = Modifier.padding(30.dp)) {
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
                        Text(
                            stringResource(R.string.btn_confirm),
                            color = MaterialTheme.colorScheme.textPurple
                        )
                    }
                }
            }
        }
    }
}