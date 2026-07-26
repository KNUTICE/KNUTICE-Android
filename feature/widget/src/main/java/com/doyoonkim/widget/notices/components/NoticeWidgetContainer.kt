package com.doyoonkim.widget.notices.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ColumnScope
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.unit.ColorProvider
import com.doyoonkim.common.MajorResources
import com.doyoonkim.common.NoticeResources
import com.doyoonkim.common.R
import com.doyoonkim.widget.components.WidgetHeader
import com.doyoonkim.widget.notices.action.NoticeWidgetRefreshAction

@Composable
fun NoticeWidgetContainer(
    modifier: GlanceModifier = GlanceModifier,
    title: String,
    lastUpdated: Long,
    titleColor: ColorProvider,
    containerColor: ColorProvider,
    contentContainerColor: ColorProvider,
    content: @Composable (ColumnScope.(GlanceModifier) -> Unit)
) {
    val context = LocalContext.current

    val categoryTextRes = NoticeResources.getStringResourcesByCategory(title).let {
        if (it == R.string.text_category_not_found) {
            MajorResources.getLocalizedString(title)
        } else {
            it
        }
    }
    val categoryText = context.getString(categoryTextRes)

    Box(
        modifier = modifier
            .cornerRadius(15.dp)
            .background(containerColor),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = GlanceModifier.fillMaxWidth()
                .padding(10.dp)
                .background(Color.Transparent),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WidgetHeader(
                modifier = GlanceModifier.defaultWeight(),
                title = categoryText,
                lastUpdated = lastUpdated,
                iconResId = R.drawable.outline_sync_24,
                onRefreshClick = actionRunCallback<NoticeWidgetRefreshAction>()
            )

            // Notices Container
            Column(
                modifier = GlanceModifier.fillMaxWidth()
                    .fillMaxHeight()
                    .cornerRadius(15.dp)
                    .padding(5.dp)
                    .background(contentContainerColor),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content(GlanceModifier.defaultWeight())
            }
        }
    }
}
