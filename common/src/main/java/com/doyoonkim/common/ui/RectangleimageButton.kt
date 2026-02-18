package com.doyoonkim.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RectangleImageButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: Painter,
    iconSize: Dp = 50.dp,
    isSelected: Boolean = false,
    textColor: Color,
    containerColor: Color,
    border: Color,
    borderHighlighted: Color,
    onClicked: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier.wrapContentSize()
            .background(Color.Transparent)
            .clip(RoundedCornerShape(15.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onClicked() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = modifier
                .background(containerColor)
                .border(
                    width =3.dp,
                    color = if (isSelected) borderHighlighted else border,
                    shape = RoundedCornerShape(15.dp))
                .padding(25.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(iconSize)
            )
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start,
                    color = textColor,
                ),
                maxLines = 2,
                modifier = Modifier.wrapContentSize()
                    .padding(top = 5.dp)
            )
        }
    }
}