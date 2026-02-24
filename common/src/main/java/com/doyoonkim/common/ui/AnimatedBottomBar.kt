package com.doyoonkim.common.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

data class BottomBarButton(
    val titleResourceId: Int,
    val iconResourceId: Int,
    val dest: String,
)

@Composable
fun AnimatedBottomBar(
    modifier: Modifier = Modifier,
    items: List<BottomBarButton> = emptyList(),
    selection: Int = 0,
    containerColor: Color,
    contentColor: Color,
    colorOnSelect: Color,
    onItemClicked: (Int, String) -> Unit = { idx, route -> }
) {
    var bottomBarSize by remember { mutableStateOf(IntSize.Zero) }
    val isDarkTheme = isSystemInDarkTheme()

    // Custom Animation
    val animationValue by animateFloatAsState(
        targetValue = ((bottomBarSize.width / items.size) * selection).toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "Bottom Bar Selection Animation"
    )

    // Bottom App Bar
    Surface(
        modifier = modifier.fillMaxWidth()
            .shadow(
                elevation = if (isDarkTheme) 0.dp else 10.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .border(
                width = 1.dp,
                color = if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            )
            .background(Color.Transparent),
        color = containerColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.wrapContentSize()
                .padding(5.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // Selection Highlight
            Box(
                modifier = Modifier
                    .offset { IntOffset(x = animationValue.roundToInt(), y = 0) }
                    .size(
                        width = with(LocalDensity.current) {
                            (bottomBarSize.width / items.size).toDp()
                        },
                        height = with(LocalDensity.current) {
                            bottomBarSize.height.toDp()
                        }
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(colorOnSelect)
            )

            // Bottom Bar Buttons
            Row(
                modifier = Modifier.fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.Transparent)
                    .onGloballyPositioned {coordinates ->
                        bottomBarSize = coordinates.size
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items.forEachIndexed { idx, button ->
                    AnimatedBottomNavBarItem(
                        modifier = Modifier.weight(1f),
                        text = stringResource(button.titleResourceId),
                        icon = painterResource(button.iconResourceId),
                        contentColor = contentColor
                    ) {
                        onItemClicked(idx, button.dest)
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedBottomNavBarItem(
    modifier: Modifier = Modifier,
    text: String,
    icon: Painter,
    contentColor: Color,
    onClicked: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Depth-based Indication
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        label = "Button Pressed Depth Scale"
    )

    Surface(
        modifier = modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,      // Disable default Material Ripple
                onClick = onClicked
            )
            .background(Color.Transparent),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .background(Color.Transparent)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Text(
                text,
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = contentColor
                )
            )
        }
    }
}