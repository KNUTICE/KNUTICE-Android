package com.doyoonkim.widget.notices.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ColumnScope
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.wrapContentHeight
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.doyoonkim.common.CollegeResource
import com.doyoonkim.common.NoticeResources
import com.doyoonkim.common.R

@Composable
fun NoticeWidgetContainer(
    modifier: GlanceModifier = GlanceModifier,
    title: String,
    titleColor: ColorProvider,
    containerColor: ColorProvider,
    contentContainerColor: ColorProvider,
    content: @Composable (ColumnScope.(GlanceModifier) -> Unit)
) {
    val context = LocalContext.current

    val categoryTextRes = NoticeResources.getStringResourcesByCategory(title).let {
        if (it == R.string.text_category_not_found)
            CollegeResource.getLocalizedCollegeStringByMajor(title)
        else it
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
            Text(
                text = categoryText,
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .wrapContentHeight(),
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor,
                    textAlign = TextAlign.Start
                ),
                maxLines = 1
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