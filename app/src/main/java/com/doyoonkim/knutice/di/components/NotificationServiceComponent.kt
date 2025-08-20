package com.doyoonkim.knutice.di.components

import com.doyoonkim.common.di.CommonModule
import com.doyoonkim.data.di.ImageRemoteModule
import com.doyoonkim.data.di.NoticeRemoteModule
import com.doyoonkim.data.di.TokenRemoteModule
import com.doyoonkim.domain.di.TokenUseCaseModule
import com.doyoonkim.knutice.di.modules.DispatcherModule
import com.doyoonkim.knutice.di.util.SystemServices
import com.doyoonkim.network.di.NotificationNetworkModule
import com.doyoonkim.notification.di.NotificationModule
import com.doyoonkim.notification.fcm.PushNotificationService
import dagger.Component

@Component(
    dependencies = [SystemServices::class],
    modules = [
        NotificationModule::class,
        DispatcherModule::class,
        TokenUseCaseModule::class,
        TokenRemoteModule::class,
        NoticeRemoteModule::class,
        ImageRemoteModule::class,
        CommonModule::class,
        NotificationNetworkModule::class
    ]
)
interface NotificationServiceComponent {
    fun inject(service: PushNotificationService)

    @Component.Factory
    interface Factory {
        fun create(systemServices: SystemServices): NotificationServiceComponent
    }
}