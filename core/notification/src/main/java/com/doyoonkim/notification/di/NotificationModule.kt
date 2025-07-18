package com.doyoonkim.notification.di

import android.content.Context
import com.doyoonkim.common.BitmapHandler
import com.doyoonkim.common.di.ApplicationContext
import com.doyoonkim.model.di.IoDispatcher
import com.doyoonkim.common.di.TokenHandler
import com.doyoonkim.domain.interfaces.ImageRemoteRepository
import com.doyoonkim.domain.interfaces.NoticeRemoteRepository
import com.doyoonkim.notification.fcm.PushNotificationHandler
import com.doyoonkim.notification.fcm.TokenHandlerImpl
import com.doyoonkim.notification.local.AlarmScheduler
import com.doyoonkim.notification.local.NotificationAlarmScheduler
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
object NotificationModule {

    // Considered to be removed.
    @Provides
    fun providesPushNotificationHandler(
        remoteRepository: NoticeRemoteRepository,
        imageRepository: ImageRemoteRepository,
        bitmapHandler: BitmapHandler,
        @IoDispatcher dispatcher: CoroutineDispatcher,
        @ApplicationContext context: Context
    ) =
        PushNotificationHandler(
            remoteRepository,
            imageRepository,
            bitmapHandler,
            dispatcher,
            context
        )

}

@Module
abstract class NotificationBindingModule {
    @Binds
    abstract fun bindsNotificationAlarmScheduler(
        impl: NotificationAlarmScheduler
    ) : AlarmScheduler

    @Binds
    abstract fun bindsTokenHandler(
        impl: TokenHandlerImpl
    ): TokenHandler
}