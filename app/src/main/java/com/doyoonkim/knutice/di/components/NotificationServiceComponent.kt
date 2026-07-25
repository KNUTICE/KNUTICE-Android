package com.doyoonkim.knutice.di.components

import com.doyoonkim.common.di.CommonModule
import com.doyoonkim.data.di.ImageRemoteModule
import com.doyoonkim.data.di.TokenRemoteModule
import com.doyoonkim.domain.di.TokenUseCaseModule
import com.doyoonkim.knutice.di.modules.WorkSchedulerModule
import com.doyoonkim.knutice.di.util.LocalPreferenceProvider
import com.doyoonkim.knutice.di.util.NetworkProvider
import com.doyoonkim.knutice.di.util.SystemServices
import com.doyoonkim.notification.di.NotificationModule
import com.doyoonkim.notification.fcm.PushNotificationService
import dagger.Component

@Component(
    dependencies = [
        SystemServices::class,
        LocalPreferenceProvider::class,
        NetworkProvider::class
    ],
    modules = [
        NotificationModule::class,
        WorkSchedulerModule::class,
        TokenUseCaseModule::class,
        TokenRemoteModule::class,
        ImageRemoteModule::class,
        CommonModule::class
    ]
)
interface NotificationServiceComponent {
    fun inject(service: PushNotificationService)

    @Component.Factory
    interface Factory {
        fun create(
            systemServices: SystemServices,
            networkProvider: NetworkProvider,
            localPreferenceProvider: LocalPreferenceProvider
        ): NotificationServiceComponent
    }
}
