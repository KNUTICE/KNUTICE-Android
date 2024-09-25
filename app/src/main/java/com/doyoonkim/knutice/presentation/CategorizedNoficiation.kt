package com.doyoonkim.knutice.presentation

import android.content.res.Configuration
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.doyoonkim.knutice.domain.Notice
import com.doyoonkim.knutice.ui.theme.containerBackground
import com.doyoonkim.knutice.ui.theme.notificationType1
import com.doyoonkim.knutice.ui.theme.notificationType2
import com.doyoonkim.knutice.ui.theme.notificationType3
import com.doyoonkim.knutice.ui.theme.notificationType4
import com.doyoonkim.knutice.ui.theme.subTitle
import com.doyoonkim.knutice.ui.theme.title
import com.doyoonkim.knutice.viewModel.CategorizedNotificationViewModel
import com.example.knutice.R

@Composable
fun CategorizedNotification(
    modifier: Modifier = Modifier,
    viewModel: CategorizedNotificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
            contents = uiState.notificationGeneral
        ) {
            // TODO: onMoreClicked
        }

        NotificationPreviewList(
            listTitle = stringResource(R.string.academic_news),
            titleColor = MaterialTheme.colorScheme.notificationType2,
            contents = uiState.notificationAcademic
        ) {
            // TODO: onMoreClicked
        }

        NotificationPreviewList(
            listTitle = stringResource(R.string.scholarship_news),
            titleColor = MaterialTheme.colorScheme.notificationType3,
            contents = uiState.notificationScholarship
        ) {
            // TODO: onMoreClicked
        }

        NotificationPreviewList(
            listTitle = stringResource(R.string.event_news),
            titleColor = MaterialTheme.colorScheme.notificationType4,
            contents = uiState.notificationEvent
        ) {
            // TODO: onMoreClicked
        }

    }
}

@Composable
fun NotificationPreviewList(
    modifier: Modifier = Modifier,
    listTitle: String = "List Title goes here",
    titleColor: Color = Color.Unspecified,
    contents: List<Notice> = listOf(),
    onMoreClicked: () -> Unit = {  }
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
            NotificationPreviewContainer(
                notificationTitle = content.title,
                notificationInfo = "[${content.departName}] ${content.timestamp}"
            ) {  }
        }
    }
}

@Composable
fun NotificationPreviewContainer(
    notificationTitle: String = "Title goes here.",
    notificationInfo: String = "Notification info goes here.",
    onClick: () -> Unit = { /* Action should be defined. */ }
) {
    Card(
        Modifier.fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                onClick()
            },
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.containerBackground,
            contentColor = Color.Unspecified,
            disabledContainerColor = Color.Unspecified,
            disabledContentColor = Color.Unspecified
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            if (notificationTitle == "Unknown") {
                AnimatedGradient(Modifier.height(24.dp))
                AnimatedGradient(Modifier.height(14.dp))
            } else {
                Text(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 7.dp, start = 5.dp, end = 5.dp),
                    text = notificationTitle,
                    textAlign = TextAlign.Start,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.title
                )
                Text(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 1.dp, start = 5.dp, bottom = 5.dp, end = 5.dp),
                    text = notificationInfo,
                    textAlign = TextAlign.Start,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.subTitle
                )
            }
        }
    }
}

@Composable
fun AnimatedGradient(
    modifier: Modifier
) {

    val backgroundGray = Color(0xFF323232)
    val backgroundMediumGray = Color(0xFF4C4C4C)
    val backgroundLightGray = Color(0xFF5E5E5E)

    val transition = rememberInfiniteTransition(label="placeholder")
    val backgroundStart by transition.animateColor(
        initialValue = backgroundGray,
        targetValue = backgroundMediumGray,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "backgroundStart"
    )

    val backgroundEnd by transition.animateColor(
        initialValue = backgroundMediumGray,
        targetValue = backgroundLightGray,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "backgroundEnd"
    )

    Box(
        modifier = modifier.fillMaxWidth()
            .background(
                brush = Brush.linearGradient(listOf(backgroundStart, backgroundEnd)),
                shape = RoundedCornerShape(3.dp)
            )
    )
}


@Composable
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun CategorizedNotification_Preview() {
    AnimatedGradient(Modifier.height(20.dp))
}