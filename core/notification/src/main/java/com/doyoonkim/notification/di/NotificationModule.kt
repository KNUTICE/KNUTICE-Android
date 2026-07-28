package com.doyoonkim.notification.di

import android.content.Context
import com.doyoonkim.common.BitmapHandler
import com.doyoonkim.common.di.TokenHandler
import com.doyoonkim.domain.interfaces.ImageRemoteRepository
import com.doyoonkim.model.di.ApplicationContext
import com.doyoonkim.model.di.IoDispatcher
import com.doyoonkim.notification.fcm.PushNotificationHandler
import com.doyoonkim.notification.fcm.TokenHandlerImpl
import com.doyoonkim.notification.local.AlarmScheduler
import com.doyoonkim.notification.local.NotificationAlarmScheduler
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher

@Module
abstract class NotificationModule {

    companion object {
        // Considered to be removed.
        @Provides
        fun providesPushNotificationHandler(
            imageRepository: ImageRemoteRepository,
            bitmapHandler: BitmapHandler,
            @IoDispatcher dispatcher: CoroutineDispatcher,
            @ApplicationContext context: Context
        ) =
            PushNotificationHandler(
                imageRepository,
                bitmapHandler,
                dispatcher,
                context
            )
    }

    @Binds
    abstract fun bindsNotificationAlarmScheduler(
        impl: NotificationAlarmScheduler
    ): AlarmScheduler

    @Binds
    abstract fun bindsTokenHandler(
        impl: TokenHandlerImpl
    ): TokenHandler
}

@Module
abstract class FcmTokenModule {
    @Binds
    abstract fun bindsTokenHandler(
        impl: TokenHandlerImpl
    ): TokenHandler
}
