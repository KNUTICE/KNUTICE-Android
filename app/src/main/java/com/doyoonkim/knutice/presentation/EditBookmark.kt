package com.doyoonkim.knutice.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.doyoonkim.knutice.presentation.component.NotificationPreviewCard
import com.doyoonkim.knutice.ui.theme.containerBackground
import com.doyoonkim.knutice.ui.theme.notificationType1
import com.doyoonkim.knutice.ui.theme.subTitle
import com.doyoonkim.knutice.ui.theme.title
import com.doyoonkim.knutice.viewModel.EditBookmarkViewModel

@Composable
fun EditBookmark(
    modifier: Modifier = Modifier,
    viewModel: EditBookmarkViewModel = hiltViewModel(),
    onSaveClicked: () -> Unit = {  }
) {
    val uiState by viewModel.uiState.collectAsState()
    val localContext = LocalContext.current

    Column(
        modifier = modifier
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
            var notSupportedMessageShowed by remember { mutableStateOf(false) }

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
                    checked = false,
                    enabled = true,
                    modifier = Modifier.padding(10.dp).weight(1f),
                    onCheckedChange = { notSupportedMessageShowed = true }
                )
            }

            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                visible = notSupportedMessageShowed,
                enter = slideInVertically()
            ) {
                Text(
                    text = stringResource(R.string.text_not_supported),
                    textAlign = TextAlign.Start,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.notificationType1,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                )
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
                    onSaveClicked()
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
                        onSaveClicked()
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
}

@Preview(showBackground = true)
@Composable
fun EditBookmark_Preview() {
    EditBookmark(Modifier.fillMaxSize().padding(10.dp)) {  }
}

