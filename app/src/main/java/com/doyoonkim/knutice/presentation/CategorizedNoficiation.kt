package com.doyoonkim.knutice.presentation

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.doyoonkim.knutice.model.Destination
import com.doyoonkim.knutice.model.Notice
import com.doyoonkim.knutice.presentation.component.NotificationPreviewCard
import com.doyoonkim.knutice.ui.theme.notificationType1
import com.doyoonkim.knutice.ui.theme.notificationType2
import com.doyoonkim.knutice.ui.theme.notificationType3
import com.doyoonkim.knutice.ui.theme.notificationType4
import com.doyoonkim.knutice.ui.theme.subTitle
import com.doyoonkim.knutice.viewModel.CategorizedNotificationViewModel
import com.doyoonkim.knutice.R
import com.doyoonkim.knutice.model.FullContent

@Composable
fun CategorizedNotification(
    modifier: Modifier = Modifier,
    viewModel: CategorizedNotificationViewModel = hiltViewModel(),
    onGoBackAction: () -> Unit,
    onMoreNoticeRequested: (Destination) -> Unit,
    onFullContentRequested: (FullContent) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Back button/gesture actions
    BackHandler {
        onGoBackAction()
    }

    Column(
        modifier = modifier.verticalScroll(
            rememberScrollState(0)
        ),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NotificationPreviewList (
            listTitle = stringResource(R.string.general_news),
            titleColor = MaterialTheme.colorScheme.notificationType1,
            contents = uiState.notificationGeneral,
            onMoreClicked = { onMoreNoticeRequested(Destination.MORE_GENERAL) }
        ) { title, info, url ->
            onFullContentRequested(FullContent(title, info, url))
        }

        NotificationPreviewList(
            listTitle = stringResource(R.string.academic_news),
            titleColor = MaterialTheme.colorScheme.notificationType2,
            contents = uiState.notificationAcademic,
            onMoreClicked = { onMoreNoticeRequested(Destination.MORE_ACADEMIC) }
        ) { title, info, url ->
            onFullContentRequested(FullContent(title, info, url))
        }

        NotificationPreviewList(
            listTitle = stringResource(R.string.scholarship_news),
            titleColor = MaterialTheme.colorScheme.notificationType3,
            contents = uiState.notificationScholarship,
            onMoreClicked = { onMoreNoticeRequested(Destination.MORE_SCHOLARSHIP) }
        ) { title, info, url ->
            onFullContentRequested(FullContent(title, info, url))
        }

        NotificationPreviewList(
            listTitle = stringResource(R.string.event_news),
            titleColor = MaterialTheme.colorScheme.notificationType4,
            contents = uiState.notificationEvent,
            onMoreClicked = { onMoreNoticeRequested(Destination.MORE_EVENT) }
        ) { title, info, url ->
            onFullContentRequested(FullContent(title, info, url))
        }
    }
}

@Composable
fun NotificationPreviewList(
    modifier: Modifier = Modifier,
    listTitle: String = "List Title goes here",
    titleColor: Color = Color.Unspecified,
    contents: List<Notice> = listOf(),
    onMoreClicked: () -> Unit = {  },
    onNoticeClicked: (String, String, String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(7.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier.fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.wrapContentHeight().weight(6f),
                text = listTitle,
                color = titleColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                modifier = Modifier.fillMaxWidth().weight(1f)
                    .clickable { onMoreClicked() },
                text = stringResource(R.string.btn_more),
                color = MaterialTheme.colorScheme.subTitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
        contents.forEach { content ->
            NotificationPreviewCard(
                notificationTitle = content.title,
                notificationInfo = "[${content.departName}] ${content.timestamp}"
            ) {
                onNoticeClicked(content.title, content.departName, content.url)
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun CategorizedNotification_Preview() {

}