package com.doyoonkim.widget.di

import com.doyoonkim.common.di.WorkerKey
import com.doyoonkim.common.worker.IntermediateWorkerFactory
import com.doyoonkim.widget.worker.KnuticeCarrelWidgetSync
import com.doyoonkim.widget.worker.KnuticeWidgetSync
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class WidgetModule {

    @Binds
    @IntoMap
    @WorkerKey(KnuticeWidgetSync::class)
    abstract fun bindsKnuticeWidgetSyncWorker(
        factory: KnuticeWidgetSync.Factory
    ): IntermediateWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(KnuticeCarrelWidgetSync::class)
    abstract fun bindsKnuticeCarrelWidgetSyncWorker(
        factory: KnuticeCarrelWidgetSync.Factory
    ): IntermediateWorkerFactory
}
