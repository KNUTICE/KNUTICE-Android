package com.doyoonkim.knutice

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.doyoonkim.common.di.AppInjector
import com.doyoonkim.common.di.AppInjectorProvider
import com.doyoonkim.common.R
import com.doyoonkim.knutice.di.AppComponent
import com.doyoonkim.knutice.di.DaggerAppComponent
import com.doyoonkim.notification.fcm.PushNotificationService
import com.doyoonkim.notification.fcm.TokenHandler
import javax.inject.Inject

class MainApplication() : Application(), AppInjectorProvider {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }

    override val appInjector: AppInjector = object : AppInjector {
        override fun inject(target: Any) {
            when(target) {
                is PushNotificationService -> appComponent.inject(target)
                else -> error("Unsupported Target $target")
            }
        }

    }

    @Inject lateinit var tokenHandler: TokenHandler

    override fun onCreate() {
        super.onCreate()
        // Application-Level injection
        appComponent.inject(this)

        // Create channel group
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).run {
            createNotificationChannel(
                getString(R.string.inapp_notification_channel_id),
                getString(R.string.inapp_notificaiton_channel_name),
                getString(R.string.inapp_notification_channel_description)
            )
        }
        tokenHandler.handleCurrentTokenRequest()
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    private fun createNotificationChannel(id: String, name: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                id,
                name,
                importance
            ).apply {
                this.description = description
            }

            // Register Custom-defined notification channel
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}