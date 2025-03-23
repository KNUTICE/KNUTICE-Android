package com.doyoonkim.knutice.presentation

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.doyoonkim.knutice.R
import com.doyoonkim.knutice.model.Bookmark
import com.doyoonkim.knutice.presentation.component.DateTimePicker
import com.doyoonkim.knutice.presentation.component.NotificationPreviewCard
import com.doyoonkim.knutice.ui.theme.containerBackground
import com.doyoonkim.knutice.ui.theme.subTitle
import com.doyoonkim.knutice.ui.theme.title
import com.doyoonkim.knutice.viewModel.EditBookmarkViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun EditBookmark(
    modifier: Modifier = Modifier,
    viewModel: EditBookmarkViewModel = hiltViewModel(),
    onSaveClicked: (Bookmark?) -> Unit = {  }
) {
    val uiState by viewModel.uiState.collectAsState()
    val localContext = LocalContext.current

    LaunchedEffect(uiState.isReminderRequested) {
        if (uiState.timeForRemind == 0L) {
            viewModel.updateReminderOptions(updatedTimeForRemind = System.currentTimeMillis())
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        NotificationPreviewCard(
            modifier = Modifier.padding(5.dp),
            notificationTitle = uiState.targetNotice.title,
            notificationInfo = uiState.targetNotice.departName
        )

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
                    onCheckedChange = { viewModel.updateReminderOptions(reminderRequested = !uiState.isReminderRequested) }
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
                                updatedDatePickerVisible = !uiState.datePickerVisible
                            ) },
                        text = uiState.timeForRemind.toFormattedDate(
                            SimpleDateFormat("yyyy/MM/dd a HH:mm", Locale.getDefault())),
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
            modifier = Modifier.fillMaxWidth().weight(5f)
                .padding(top = 5.dp, bottom = 25.dp)
        ) {
            TextField(
                modifier = Modifier.fillMaxSize(),
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
                modifier = Modifier.wrapContentHeight().weight(1f),
                enabled = true,
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    if (!uiState.requireCreation) {
                        viewModel.modifyBookmark()
                    } else {
                        viewModel.createNewBookmark()
                    }
                    coroutineScope.launch {
                        delay(500L)
                        onSaveClicked(uiState.bookmarkInstance)
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
                        onSaveClicked(null)
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
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        visible = uiState.datePickerVisible,
        enter = slideInVertically(initialOffsetY = { it + it / 2 }),
        exit = slideOutVertically(targetOffsetY = { it / 2 })
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(start = 5.dp, end = 5.dp)
                .clickable { viewModel.updateReminderOptions(
                    updatedDatePickerVisible = !uiState.datePickerVisible)
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
                        updatedTimeForRemind = it,
                        updatedDatePickerVisible = !uiState.datePickerVisible
                    )
                }
            }
        }

    }
}

private fun Long.toFormattedDate(f: SimpleDateFormat): String {
    return f.apply { timeZone = TimeZone.getTimeZone(ZoneId.systemDefault()) }.format(Date(this))
}



@Preview(showBackground = true)
@Composable
fun EditBookmark_Preview() {
    EditBookmark(Modifier.fillMaxSize().padding(10.dp)) {  }
}

