package com.doyoonkim.widget.notices.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxWidth
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.doyoonkim.widget.model.WidgetNoticeVO

@Composable
fun WidgetNoticeContainer(
    modifier: GlanceModifier = GlanceModifier,
    notice: WidgetNoticeVO,
    onNoticeClicked: (WidgetNoticeVO) -> Action
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .clickable(
                onNoticeClicked(notice)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = notice.title,
            modifier = GlanceModifier
                .fillMaxWidth(),
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = GlanceTheme.colors.primary,
                textAlign = TextAlign.Start
            ),
            maxLines = 1
        )
        Text(
            text = notice.info,
            modifier = GlanceModifier
                .fillMaxWidth(),
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = GlanceTheme.colors.secondary,
                textAlign = TextAlign.Start
            ),
            maxLines = 1
        )
    }
}