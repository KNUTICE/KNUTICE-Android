package com.doyoonkim.domain.interfaces

interface AsyncNoticeWidgetTaskScheduler {
    fun schedulePeriodicTask()

    fun unschedulePeriodicTask()

    fun executeImmediateTask()
}
