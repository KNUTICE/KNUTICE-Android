package com.doyoonkim.common.worker

import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

interface IntermediateWorkerFactory {
    fun create(params: WorkerParameters): ListenableWorker
}
