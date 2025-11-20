package com.doyoonkim.knutice.di.modules

import androidx.work.ListenableWorker
import com.doyoonkim.domain.interfaces.AsyncFtsTaskScheduler
import com.doyoonkim.knutice.di.util.WorkerKey
import com.doyoonkim.knutice.task.AsyncFtsTableInsertion
import com.doyoonkim.knutice.task.AsyncFtsTaskSchedulerImpl
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

    @Binds
    @IntoMap
    @WorkerKey(AsyncFtsTableInsertion::class)
    abstract fun bindsAsyncFtsTableInsertionWorker(
        factory: AsyncFtsTableInsertion.Factory
    ): IntermediateWorkerFactory

}

@Module
abstract class WorkSchedulerModule {
    @Binds
    abstract fun bindsWorkScheduler(
        impl: AsyncFtsTaskSchedulerImpl
    ): AsyncFtsTaskScheduler
}