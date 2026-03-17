package com.doyoonkim.widget.carrel.action

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.doyoonkim.widget.di.WidgetDependencyProvider

class CarrelStatusRefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d("CarrelStatusRefreshAction", "Start Work Scheduling")
        val provider = (context.applicationContext as WidgetDependencyProvider).provide()
        provider.carrelWidgetTaskScheduler().executeImmediateTask()
    }

}