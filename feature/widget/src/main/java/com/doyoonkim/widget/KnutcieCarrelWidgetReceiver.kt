package com.doyoonkim.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import com.doyoonkim.widget.carrel.KnuticeCarrelRoomStatusWidget
import com.doyoonkim.widget.worker.CarrelWidgetTaskScheduler
import dagger.android.AndroidInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author kimdoyoon
 * Created 3/2/26 at 1:02 AM
 */
class KnuticeCarrelWidgetReceiver: GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = KnuticeCarrelRoomStatusWidget()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        // Initial Sync
        CarrelWidgetTaskScheduler.schedule(context)

    }

}