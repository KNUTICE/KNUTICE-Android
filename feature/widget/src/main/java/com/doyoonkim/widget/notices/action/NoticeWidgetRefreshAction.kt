package com.doyoonkim.widget.notices.action

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.doyoonkim.widget.di.WidgetDependencyProvider

/**
 * @author kimdoyoon
 * Created 3/11/26 at 1:39 AM
 */
class NoticeWidgetRefreshAction: ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d("NoticeWidgetRefreshAction", "Start Notice Refresh")
        val provider = (context.applicationContext as WidgetDependencyProvider).provide()
        provider.noticeWidgetTaskScheduler().executeImmediateTask()
    }

}