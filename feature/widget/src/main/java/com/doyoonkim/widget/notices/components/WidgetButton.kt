package com.doyoonkim.widget.notices.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.padding
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

@Composable
fun WidgetButton(
    modifier: GlanceModifier = GlanceModifier,
    text: String,
    textColor: ColorProvider,
    containerColor: ColorProvider,
    onClicked: () -> Action
) {
    Box(
        modifier = modifier
            .cornerRadius(15.dp)
            .background(containerColor)
            .clickable(
                onClicked()
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = textColor
            ),
            maxLines = 1,
            modifier = GlanceModifier.wrapContentSize().padding(15.dp)
        )
    }
}
