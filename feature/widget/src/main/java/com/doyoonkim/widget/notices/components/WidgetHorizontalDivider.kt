package com.doyoonkim.widget.notices.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.glance.GlanceModifier
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.unit.ColorProvider

@Composable
fun WidgetHorizontalDivider(
    color: ColorProvider,
    thickness: Dp
) {
    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .height(thickness)
            .background(color)
    ) {}
}
