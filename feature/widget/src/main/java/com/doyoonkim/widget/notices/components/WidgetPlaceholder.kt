package com.doyoonkim.widget.notices.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

@Composable
fun WidgetPlaceholder(
    modifier: GlanceModifier = GlanceModifier,
    titleText: String,
    textColor: ColorProvider,
    containerColor: ColorProvider,
    extraContents: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .cornerRadius(20.dp)
            .background(containerColor),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = titleText,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                textAlign = TextAlign.Center
            ),
            maxLines = 2,
            modifier = GlanceModifier.fillMaxWidth().padding(15.dp)
        )

        Spacer(GlanceModifier.fillMaxWidth().height(5.dp))

        // Extra Contents
        extraContents()
    }
}
