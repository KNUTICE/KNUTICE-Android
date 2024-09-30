package com.doyoonkim.knutice.presentation.component

import android.content.res.Configuration
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.doyoonkim.knutice.ui.theme.containerBackground

@Composable
fun NotificationPreviewCard(
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
       NotificationPreview(
           notificationTitle = notificationTitle,
           notificationInfo = notificationInfo
       )
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
fun NotificationPreviewContainer_Preview() {
    AnimatedGradient(Modifier.height(20.dp))
}