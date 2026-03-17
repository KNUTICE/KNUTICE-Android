package com.doyoonkim.knutice.di.components

import com.doyoonkim.knutice.di.modules.WorkSchedulerModule
import com.doyoonkim.knutice.di.util.NetworkProvider
import com.doyoonkim.knutice.di.util.SystemServices
import com.doyoonkim.widget.di.WidgetDependency
import dagger.Component

@Component(
    dependencies = [
        SystemServices::class,
        NetworkProvider::class
    ],
    modules = [
        WorkSchedulerModule::class
    ]
)
interface WidgetComponent: WidgetDependency {

    @Component.Factory
    interface Factory {
        fun create(
            systemServices: SystemServices,
            networkProvider: NetworkProvider
        ): WidgetComponent
    }
}