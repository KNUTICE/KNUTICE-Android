package com.doyoonkim.knutice.presentation.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.doyoonkim.knutice.ui.theme.animationGradientEnd
import com.doyoonkim.knutice.ui.theme.animationGradientIntermediate
import com.doyoonkim.knutice.ui.theme.animationGradientStart

@Composable
fun AnimatedGradient(
    modifier: Modifier
) {
    val backgroundGray = MaterialTheme.colorScheme.animationGradientStart
    val backgroundMediumGray = MaterialTheme.colorScheme.animationGradientIntermediate
    val backgroundLightGray = MaterialTheme.colorScheme.animationGradientEnd

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