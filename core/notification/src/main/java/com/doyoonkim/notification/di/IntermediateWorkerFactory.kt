package com.doyoonkim.notification.di

import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

interface IntermediateWorkerFactory {
    fun create(params: WorkerParameters): ListenableWorker
}