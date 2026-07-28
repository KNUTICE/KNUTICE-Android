package com.doyoonkim.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.doyoonkim.widget.di.WidgetDependencyProvider
import com.doyoonkim.widget.notices.KnuticeWidget

class KnuticeWidgetReceiver : GlanceAppWidgetReceiver() {
    private val TAG = "KnuticeWidgetReceiver"

    override val glanceAppWidget: GlanceAppWidget = KnuticeWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        val provider = (context.applicationContext as WidgetDependencyProvider).provide()
        provider.noticeWidgetTaskScheduler().schedulePeriodicTask()
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)

        val provider = (context.applicationContext as WidgetDependencyProvider).provide()
        provider.noticeWidgetTaskScheduler().unschedulePeriodicTask()
    }
}
