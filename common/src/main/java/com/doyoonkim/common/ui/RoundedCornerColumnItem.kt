package com.doyoonkim.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun RoundedCornerColumnItemWithExtraOnRight(
    modifier: Modifier = Modifier,
    verticalPadding: Dp = 0.dp,
    titleText: String,
    subTitleText: String,
    titleColor: Color = Color.Black,
    subTitleColor: Color = Color.Gray,
    hasBottomDivider: Boolean = true,
    extraOnRight: @Composable() () -> Unit
) {
    Row(
        modifier = Modifier.wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundedCornerColumnItem(
            modifier = modifier.weight(4f),
            verticalPadding = verticalPadding,
            titleText = titleText,
            subTitleText = subTitleText,
            titleColor = titleColor,
            subTitleColor = subTitleColor,
            hasBottomDivider = hasBottomDivider
        )
        extraOnRight()
    }
}

@Composable
fun RoundedCornerColumnItem(
    modifier: Modifier = Modifier,
    verticalPadding: Dp = 0.dp,
    titleText: String,
    subTitleText: String,
    titleColor: Color = Color.Black,
    subTitleColor: Color = Color.Gray,
    hasBottomDivider: Boolean = true
) {
    val columnPadding = if (hasBottomDivider) {
        PaddingValues(top = verticalPadding)
    } else {
        PaddingValues(vertical = verticalPadding)
    }

    Column(
        modifier = modifier
            .padding(columnPadding),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = titleText,
            color = titleColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = subTitleText,
            color = subTitleColor,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        if (hasBottomDivider) {
            HorizontalDivider(
                Modifier.padding(top = verticalPadding),
                color = subTitleColor
            )
        }
    }

}