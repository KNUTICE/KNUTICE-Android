package com.doyoonkim.common.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.doyoonkim.common.theme.animationGradientEnd
import com.doyoonkim.common.theme.animationGradientStart

@Composable
fun AnimatedGradient(
    modifier: Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .skeletonEffect(
                baseColor = MaterialTheme.colorScheme.animationGradientStart,
                highLightColor = MaterialTheme.colorScheme.animationGradientEnd
            )
    )
}

// Optimized Skeleton Effect
@Composable
fun Modifier.skeletonEffect(
    baseColor: Color,
    highLightColor: Color
): Modifier = composed {
    // Simmer Effect
    val transition = rememberInfiniteTransition("Simmer Effect")

    val skeletonAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Colors
    val simmerColors = listOf(
        baseColor.copy(alpha = 0.3f),
        highLightColor.copy(alpha = 0.8f),
        baseColor.copy(alpha = 0.3f)
    )

    this.then(
        // Defer Phase -> Let animation runs on Draw Phase (after the completion of Composition and Layout Phases)
        Modifier.drawBehind {
            val xOffset = skeletonAnimation * size.width / 1000f // get 0.0 to 1.0 range float value for offset calcuation

            drawRect(
                brush = Brush.linearGradient(
                    colors = simmerColors,
                    start = Offset(x = xOffset - size.width, y = xOffset - size.height),
                    end = Offset(x = xOffset, y = xOffset)
                )
            )
        }
    )
}
