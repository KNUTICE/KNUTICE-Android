package com.doyoonkim.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class TextSize { Large, Medium, Small }

@Composable
fun RoundedCornerColumnTextItemWithExtraOnRight(
    modifier: Modifier = Modifier,
    verticalPadding: Dp = 0.dp,
    titleText: String,
    subTitleText: String? = null,
    fontSize: TextSize = TextSize.Medium,
    primaryColor: Color = Color.Black,
    secondaryColor: Color = Color.Gray,
    hasBottomDivider: Boolean = true,
    extraOnRight: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        RoundedCornerColumnTextItem(
            modifier = modifier.weight(4f),
            verticalPadding = verticalPadding,
            titleText = titleText,
            subTitleText = subTitleText,
            textSize = fontSize,
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            hasBottomDivider = hasBottomDivider
        )
        extraOnRight()
    }
}

@Composable
fun RoundedCornerColumnTextItem(
    modifier: Modifier = Modifier,
    verticalPadding: Dp = 0.dp,
    titleText: String,
    subTitleText: String?,
    textSize: TextSize = TextSize.Medium,
    primaryColor: Color = Color.Black,
    secondaryColor: Color = Color.Gray,
    hasBottomDivider: Boolean = true
) {
    val extra = if (subTitleText == null) 10.dp else 0.dp
    val fontSize = when (textSize) {
        TextSize.Large -> Pair(24, 20)
        TextSize.Medium -> Pair(18, 14)
        TextSize.Small -> Pair(14, 10)
    }

    RoundedCornerColumnItem(
        modifier = modifier,
        verticalPadding = verticalPadding,
        extra = extra,
        hasBottomDivider = hasBottomDivider,
        dividerColor = secondaryColor
    ) {
        Text(
            text = titleText,
            color = primaryColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = fontSize.first.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth().padding(start = 10.dp)
        )

        subTitleText?.let {
            Text(
                text = it,
                color = secondaryColor,
                fontWeight = FontWeight.Normal,
                fontSize = fontSize.second.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth().padding(start = 10.dp)
            )
        }
    }
}

@Composable
fun RoundedCornerColumnItem(
    modifier: Modifier = Modifier,
    verticalPadding: Dp = 0.dp,
    extra: Dp = 0.dp,
    hasBottomDivider: Boolean = true,
    dividerColor: Color = Color.Gray,
    content: @Composable (ColumnScope.() -> Unit)
) {
    val columnPadding = if (hasBottomDivider) {
        PaddingValues(top = verticalPadding + extra)
    } else {
        PaddingValues(vertical = verticalPadding + extra)
    }

    Column(
        modifier = modifier
            .padding(columnPadding),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()

        if (hasBottomDivider) {
            HorizontalDivider(
                Modifier.padding(top = verticalPadding + extra),
                color = dividerColor
            )
        }
    }
}
