package com.doyoonkim.bookmark.edit

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doyoonkim.bookmark.contract.EditBookmarkEvent
import com.doyoonkim.bookmark.contract.EditBookmarkSideEffect
import com.doyoonkim.bookmark.viewmodel.EditBookmarkViewModel
import com.doyoonkim.common.CollegeResource
import com.doyoonkim.common.NoticeResources
import com.doyoonkim.common.R
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.common.navigation.NoticeDetail
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.theme.variantPurple
import com.doyoonkim.common.ui.DatePickerDialog
import com.doyoonkim.common.ui.NotificationPreviewCard
import com.doyoonkim.common.ui.RoundedCornerColumn
import com.doyoonkim.common.ui.RoundedCornerColumnItem
import com.doyoonkim.common.ui.RoundedCornerColumnTextItemWithExtraOnRight
import com.doyoonkim.common.ui.TextSize
import com.doyoonkim.common.ui.TimePickerDialog
import com.doyoonkim.common.ui.TopAppBarWithNavButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBookmarkScreen(
    modifier: Modifier = Modifier,
    viewModel: EditBookmarkViewModel,
    bookmarkInfo: BookmarkInfo,
    onNoticeSelected: (NoticeDetail) -> Unit,
    onCompleted: () -> Unit,
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val adjustImePadding = Modifier.consumeWindowInsets(WindowInsets.ime).imePadding()
    val localFocusManager = LocalFocusManager.current

    BackHandler {
        viewModel.sendUiEvent(EditBookmarkEvent.GoBack)
    }

    // Once per composition?
    LaunchedEffect(Unit) {
        viewModel.sendUiEvent(EditBookmarkEvent.GetBookmarkInformation(bookmarkInfo))

        // Process Side Effect
        viewModel.uiSideEffect.collect { effect ->
            when (effect) {
                is EditBookmarkSideEffect.NavToSelectedNotice -> {
                    onNoticeSelected(effect.dest)
                }
                is EditBookmarkSideEffect.NavToBack -> onBackPressed()
                is EditBookmarkSideEffect.ExitOnCompletion -> onCompleted()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBarWithNavButton(
                titleText = stringResource(R.string.title_edit_bookmark),
                onBackPressed = { viewModel.sendUiEvent(EditBookmarkEvent.GoBack) }
            )
        },
        containerColor = MaterialTheme.colorScheme.displayBackground
    ) { innerPadding ->
        Column(
            modifier = modifier.fillMaxSize()
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { localFocusManager.clearFocus() }
                    )
                }
        ) {
            NotificationPreviewCard(
                modifier = Modifier.padding(5.dp),
                isLoading = false,
                notificationTitle = bookmarkInfo.noticeTitle,
                notificationInfo = stringResource(
                    NoticeResources.getStringResourcesByCategory(bookmarkInfo.noticeInfo).let {
                        if (it == R.string.text_category_not_found) {
                            CollegeResource.getLocalizedCollegeStringByMajor(bookmarkInfo.noticeInfo)
                        } else {
                            it
                        }
                    }
                )
            ) {
                // Request Full Content
                viewModel.sendUiEvent(EditBookmarkEvent.RequestNoticeDetail)
            }

            Spacer(Modifier.height(30.dp))

            RoundedCornerColumn(
                backgroundColor = MaterialTheme.colorScheme.secondaryBackground
            ) {
                RoundedCornerColumnTextItemWithExtraOnRight(
                    verticalPadding = 12.dp,
                    titleText = stringResource(R.string.subtitle_get_reminder),
                    subTitleText = null,
                    fontSize = TextSize.Small,
                    primaryColor = MaterialTheme.colorScheme.title,
                    secondaryColor = MaterialTheme.colorScheme.subTitle,
                    hasBottomDivider = uiState.isReminderRequested
                ) {
                    Switch(
                        checked = uiState.isReminderRequested && uiState.alarmPermissionStatus,
                        enabled = true,
                        modifier = Modifier.padding(10.dp).weight(1f),
                        colors = SwitchDefaults.colors().copy(
                            checkedTrackColor = MaterialTheme.colorScheme.variantPurple,
                            checkedThumbColor = Color.White
                        ),
                        onCheckedChange = { viewModel.sendUiEvent(EditBookmarkEvent.UpdateReminderOption(it)) }
                    )
                }

                AnimatedVisibility(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    visible = uiState.isReminderRequested,
                    enter = slideInVertically(),
                    exit = slideOutVertically()
                ) {
                    RoundedCornerColumnItem(
                        verticalPadding = 12.dp,
                        hasBottomDivider = false
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.text_set_reminder_date_time),
                                textAlign = TextAlign.Start,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.title,
                                modifier = Modifier.padding(10.dp).weight(2f)
                            )

                            DatePickerDialog(
                                initialTime = uiState.timeForRemind
                            ) { year, month, day ->
                                viewModel.sendUiEvent(
                                    EditBookmarkEvent.UpdateReminderDate(year, month, day)
                                )
                            }
                            TimePickerDialog(
                                initialTime = uiState.timeForRemind
                            ) { hour, min ->
                                viewModel.sendUiEvent(
                                    EditBookmarkEvent.UpdateReminderTime(hour, min)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(15.dp))

            Text(
                text = stringResource(R.string.subtitle_notes),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.title,
                modifier = Modifier.padding(10.dp)
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
                        viewModel.sendUiEvent(EditBookmarkEvent.UpdateBookmarkNotes(it))
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.title,
                        unfocusedTextColor = MaterialTheme.colorScheme.subTitle,
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryBackground,
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryBackground,
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
                Button(
                    modifier = Modifier.wrapContentHeight()
                        .weight(1f),
                    enabled = true,
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.variantPurple,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    onClick = {
                        viewModel.sendUiEvent(EditBookmarkEvent.SaveBookmark)
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
                            viewModel.sendUiEvent(EditBookmarkEvent.RemoveBookmark)
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

        if (uiState.isProcessing) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Color.Transparent)
            ) {
                Surface(
                    modifier = Modifier.align(Alignment.Center)
                        .background(Color.Transparent)
                        .clip(RoundedCornerShape(20.dp)),
                    color = MaterialTheme.colorScheme.onAnyBackground
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                            .padding(25.dp),
                        color = MaterialTheme.colorScheme.variantPurple
                    )
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
                    color = MaterialTheme.colorScheme.secondaryBackground
                ) {
                    Column(modifier = Modifier.padding(30.dp)) {
                        Text(
                            text = if (uiState.isSuccessful) {
                                stringResource(R.string.text_save_successful)
                            } else {
                                stringResource(R.string.text_save_unsuccessful)
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.title
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        TextButton(
                            onClick = {
                                viewModel.sendUiEvent(EditBookmarkEvent.ValidateProcessResult)
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = stringResource(R.string.btn_confirm),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.variantPurple
                            )
                        }
                    }
                }
            }
        }
    }
}
