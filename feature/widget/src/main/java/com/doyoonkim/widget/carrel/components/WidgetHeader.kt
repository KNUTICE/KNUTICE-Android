package com.doyoonkim.widget.carrel.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.wrapContentHeight
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.doyoonkim.common.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WidgetHeader(
    modifier: GlanceModifier = GlanceModifier,
    title: String,
    lastUpdated: Long,
    iconResId: Int,
    onRefreshClick: Action
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = GlanceTheme.colors.primary
            )
        )
        Spacer(GlanceModifier.defaultWeight())
        Text(
            text = "마지막 업데이트: ${lastUpdated.toFormattedDate()}",
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = GlanceTheme.colors.primary
            ),
            modifier = GlanceModifier.padding(end = 10.dp)
        )
        Image(
            provider = ImageProvider(iconResId),
            contentDescription = null,
            modifier = GlanceModifier
                .wrapContentSize()
                .clickable(onRefreshClick),
            colorFilter = ColorFilter.tint(GlanceTheme.colors.primary)
        )
    }
}

fun Long.toFormattedDate() =
    SimpleDateFormat("hh:mm", Locale.getDefault()).format(Date(this))