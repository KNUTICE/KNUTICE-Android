package com.doyoonkim.knutice.di.util

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.doyoonkim.knutice.di.components.WorkerSubcomponent
import com.doyoonkim.notification.di.IntermediateWorkerFactory
import dagger.MapKey
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class WorkerKey(val value: KClass<out ListenableWorker>)

class DaggerWorkerFactory @Inject constructor(
    private val workerSubcomponentFactory: WorkerSubcomponent.Factory
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val provider = workerSubcomponentFactory.create().workerMap()
        val entry = provider.entries.find {
            Class.forName(workerClassName).isAssignableFrom(it.key)
        }
        val factory = entry?.value
            ?: throw IllegalArgumentException("Unable to find worker $workerClassName")
        return factory.get().create(workerParameters)
    }
}