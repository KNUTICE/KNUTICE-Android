package com.doyoonkim.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.doyoonkim.widget.notices.KnuticeWidget

class KnuticeWidgetReceiver : GlanceAppWidgetReceiver() {
    private val TAG = "KnuticeWidgetReceiver"

    override val glanceAppWidget: GlanceAppWidget = KnuticeWidget()

}