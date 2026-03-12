package com.doyoonkim.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.doyoonkim.widget.carrel.KnuticeCarrelRoomStatusWidget
import com.doyoonkim.widget.di.WidgetDependencyProvider

/**
 * @author kimdoyoon
 * Created 3/2/26 at 1:02 AM
 */
class KnuticeCarrelWidgetReceiver: GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = KnuticeCarrelRoomStatusWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        val provider = (context.applicationContext as WidgetDependencyProvider).provide()
        provider.carrelWidgetTaskScheduler().schedulePeriodicTask()
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)

        val provider = (context.applicationContext as WidgetDependencyProvider).provide()
        provider.carrelWidgetTaskScheduler().unschedulePeriodicTask()
    }

}