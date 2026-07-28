package com.doyoonkim.common.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title

data class ExpandableFabItem(
    val icon: ImageVector,
    val text: String,
    val textColorEnabled: Color,
    val textColorDisabled: Color,
    val isEnabled: Boolean = true
)

@Composable
fun ExpandableFloatingActionButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textWhenExpanded: String = "close",
    items: List<ExpandableFabItem>,
    primaryContentColor: Color,
    secondaryContainerColor: Color,
    onItemClicked: (Int) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier
            .background(Color.Transparent),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 3.dp,
        color = secondaryContainerColor
    ) {
        Column(
            // Force parent column to calculate its width based on its widest child BEFORE drawing.
            modifier = Modifier.width(IntrinsicSize.Max)
        ) {
            // Expanded Items
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandIn(tween(350)) + fadeIn(tween(350)),
                exit = shrinkOut(tween(350)) + fadeOut(tween(350))
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 25.dp)
                ) {
                    items.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier.padding(vertical = 15.dp)
                                .fillMaxWidth() // Take maximum width under the parent column
                                .clip(RoundedCornerShape(15.dp))
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) {
                                    if (item.isEnabled) {
                                        onItemClicked(index)
                                        isExpanded = !isExpanded
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.text,
                                tint = if (item.isEnabled) {
                                    item.textColorEnabled
                                } else {
                                    item.textColorDisabled
                                }
                            )
                            Spacer(Modifier.width(20.dp))
                            Text(
                                text = item.text,
                                color = if (item.isEnabled) {
                                    item.textColorEnabled
                                } else {
                                    item.textColorDisabled
                                }
                            )
                        }

                        if (index < items.size - 1) {
                            HorizontalDivider(
                                Modifier.padding(horizontal = 1.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.subTitle
                            )
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier.background(Color.Transparent)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        if (enabled) isExpanded = !isExpanded
                    },
                shape = RoundedCornerShape(16.dp),
                color = if (enabled) primaryContentColor else secondaryContainerColor
            ) {
                val rotateAngle by animateFloatAsState(
                    targetValue = if (isExpanded) 45f else 0f,
                    label = "Rotate on Expand"
                )

                Row(
                    modifier = Modifier.padding(vertical = 20.dp, horizontal = 25.dp)
                        // Take maximum width under the parent column.
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Expanded Floating Action Button",
                        tint = MaterialTheme.colorScheme.title,
                        modifier = Modifier.graphicsLayer { rotationZ = rotateAngle }
                    )

                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = expandHorizontally(tween(350)) + fadeIn(tween(350)),
                        exit = shrinkHorizontally(tween(350)) + fadeOut(tween(350))
                    ) {
                        Row {
                            Spacer(Modifier.width(20.dp))
                            Text(
                                text = textWhenExpanded,
                                color = MaterialTheme.colorScheme.title
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ExpandedFloatingActionButton_Preview() {
    /* (Ui Preview)
    val items = listOf(
        ExpandableFabItem(
            icon = ImageVector.vectorResource(R.drawable.baseline_bookmarks_24),
            text = "북마크"
        ),
        ExpandableFabItem(
            icon = ImageVector.vectorResource(R.drawable.outline_wand_stars_24),
            text = "AI 요약"
        )
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ExpandableFloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd)
                .padding(end = 10.dp, bottom = 30.dp),
            textWhenExpanded = "닫기",
            items = items,
            primaryContentColor = MaterialTheme.colorScheme.variantPurple,
            secondaryContainerColor = MaterialTheme.colorScheme.containerGray
        ) {

        }
    }
     */
}
