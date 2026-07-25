package com.doyoonkim.widget.di

import com.doyoonkim.domain.interfaces.AsyncCarrelWidgetTaskScheduler
import com.doyoonkim.domain.interfaces.AsyncNoticeWidgetTaskScheduler

interface WidgetDependencyProvider {
    fun provide(): WidgetDependency
}

interface WidgetDependency {
    fun noticeWidgetTaskScheduler(): AsyncNoticeWidgetTaskScheduler
    fun carrelWidgetTaskScheduler(): AsyncCarrelWidgetTaskScheduler
}
