package com.doyoonkim.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RoundedCornerColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    cornerRadius: Dp = 15.dp,
    backgroundColor: Color,
    content: @Composable() (ColumnScope.() -> Unit)
) {

    Surface(
        modifier = Modifier
            .wrapContentSize()
            .background(Color.Transparent)
            .clip(RoundedCornerShape(cornerRadius)),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            content = content
        )
    }

}

@Preview(showBackground = true)
@Composable
fun RoundedCornerColumn_Preview() {
    RoundedCornerColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        backgroundColor = Color.LightGray
    ) {
        RoundedCornerColumnItem(
            titleText = "First Item",
            subTitleText = "This is a first item",
            verticalPadding = 10.dp
        )

        RoundedCornerColumnItemWithExtraOnRight(
            titleText = "Second Item",
            subTitleText = "This is a Second item",
            verticalPadding = 10.dp,
            hasBottomDivider = false,
            extraOnRight = {
                IconButton(
                    onClick = {   },
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentSize()
                        .clip(CircleShape),
                    colors = IconButtonDefaults.iconButtonColors().copy(
                        containerColor = Color.Gray,
                        contentColor = Color.LightGray
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                        contentDescription = "Go",
                        tint = Color.LightGray
                    )
                }
            }
        )
    }
}