package com.doyoonkim.knutice.di.components

import android.app.Application
import com.doyoonkim.data.di.DataModule
import com.doyoonkim.knutice.di.AppModule
import com.doyoonkim.network.di.NetworkModule
import com.doyoonkim.notification.di.NotificationModule
import com.google.firebase.messaging.FirebaseMessagingService
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        AppModule::class,
        NotificationModule::class,
        DataModule::class,
        NetworkModule::class
    ]
)
interface NotificationServiceComponent {
    fun inject(service: FirebaseMessagingService)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): NotificationServiceComponent
    }
}