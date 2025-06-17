package com.doyoonkim.notification.di

import android.content.Context
import com.doyoonkim.common.BitmapHandler
import com.doyoonkim.common.di.ApplicationContext
import com.doyoonkim.domain.ImageRepository
import com.doyoonkim.domain.usecases.ValidateDeviceToken
import com.doyoonkim.notification.fcm.PushNotificationHandler
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object NotificationModule {

    @Provides
    @Singleton
    fun providesPushNotificationHandler(
        imageRepository: ImageRepository,
        bitmapHandler: BitmapHandler,
        @ApplicationContext context: Context
    ) =
        PushNotificationHandler(
            imageRepository,
            bitmapHandler,
            context
        )

}