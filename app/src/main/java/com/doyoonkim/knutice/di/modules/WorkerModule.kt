package com.doyoonkim.knutice.di.modules

import androidx.work.ListenableWorker
import com.doyoonkim.knutice.di.util.WorkerKey
import com.doyoonkim.notification.di.IntermediateWorkerFactory
import com.doyoonkim.notification.task.PeriodicTokenRegistration
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(PeriodicTokenRegistration::class)
    abstract fun bindsPeriodicTokenRegistrationWorker(
        factory: PeriodicTokenRegistration.Factory
    ): IntermediateWorkerFactory

}