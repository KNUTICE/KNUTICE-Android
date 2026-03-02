package com.doyoonkim.widget.carrel.action

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import com.doyoonkim.widget.model.WidgetKey
import com.doyoonkim.widget.worker.CarrelWidgetTaskScheduler
import javax.inject.Inject

class CarrelStatusRefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d("CarrelStatusRefreshAction", "Start Work Scheduling")
        CarrelWidgetTaskScheduler.schedule(context)
    }

}