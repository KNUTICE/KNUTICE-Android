package com.doyoonkim.domain.interfaces

interface AsyncCarrelWidgetTaskScheduler {
    fun schedulePeriodicTask()

    fun unschedulePeriodicTask()

    fun executeImmediateTask()
}