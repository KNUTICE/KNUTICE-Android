package com.doyoonkim.knutice.di.util

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.doyoonkim.knutice.di.components.WorkerSubcomponent
import javax.inject.Inject

class DaggerWorkerFactory @Inject constructor(
    private val workerSubcomponentFactory: WorkerSubcomponent.Factory
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val workerClass = try {
            Class.forName(workerClassName)
        } catch (e: ClassNotFoundException) {
            // Worker class not found.
            return null
        }

        val provider = workerSubcomponentFactory.create().workerMap()
        // Fetch Entry
        val entry = provider.entries.find {
            workerClass.isAssignableFrom(it.key)
        }

        // Handle Third-party and internal Worker (e.g., Session Worker)
        return entry?.value?.get()?.create(workerParameters)
    }
}
