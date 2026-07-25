package com.doyoonkim.knutice.di.modules

import com.doyoonkim.common.di.WorkerKey
import com.doyoonkim.common.worker.IntermediateWorkerFactory
import com.doyoonkim.domain.interfaces.AsyncCarrelWidgetTaskScheduler
import com.doyoonkim.domain.interfaces.AsyncFtsTaskScheduler
import com.doyoonkim.domain.interfaces.AsyncNoticeWidgetTaskScheduler
import com.doyoonkim.domain.interfaces.MajorSubscriptionUpdateTaskScheduler
import com.doyoonkim.knutice.task.AsyncFtsTableInsertion
import com.doyoonkim.knutice.task.AsyncFtsTaskSchedulerImpl
import com.doyoonkim.knutice.task.MajorSubscriptionUpdate
import com.doyoonkim.knutice.task.MajorSubscriptionUpdateSchedulerImpl
import com.doyoonkim.notification.task.PeriodicTokenRegistration
import com.doyoonkim.widget.worker.AsyncNoticeWidgetTaskSchedulerImpl
import com.doyoonkim.widget.worker.CarrelWidgetTaskSchedulerImpl
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

    @Binds
    @IntoMap
    @WorkerKey(MajorSubscriptionUpdate::class)
    abstract fun bindsMajorSubscriptionUpdateWorker(
        factory: MajorSubscriptionUpdate.Factory
    ): IntermediateWorkerFactory
}

@Module
abstract class WorkSchedulerModule {
    @Binds
    abstract fun bindsFtsWorkScheduler(
        impl: AsyncFtsTaskSchedulerImpl
    ): AsyncFtsTaskScheduler

    @Binds
    abstract fun bindNoticeWidgetWorkScheduler(
        impl: AsyncNoticeWidgetTaskSchedulerImpl
    ): AsyncNoticeWidgetTaskScheduler

    @Binds
    abstract fun bindCarrelWidgetWorkScheduler(
        impl: CarrelWidgetTaskSchedulerImpl
    ): AsyncCarrelWidgetTaskScheduler

    @Binds
    abstract fun bindMajorSubscriptionUpdateScheduler(
        impl: MajorSubscriptionUpdateSchedulerImpl
    ): MajorSubscriptionUpdateTaskScheduler
}
