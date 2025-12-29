package com.doyoonkim.knutice.di.components

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.work.ListenableWorker
import com.doyoonkim.common.di.ApplicationContext
import com.doyoonkim.data.di.LocalModule
import com.doyoonkim.data.di.TokenRemoteModule
import com.doyoonkim.domain.di.AsyncFtsEntryInsertionModule
import com.doyoonkim.domain.di.TokenUseCaseModule
import com.doyoonkim.knutice.MainApplication
import com.doyoonkim.knutice.analytics.AnalyticsLogger
import com.doyoonkim.knutice.di.modules.AppModule
import com.doyoonkim.knutice.di.modules.DispatcherModule
import com.doyoonkim.knutice.di.modules.FirebaseAnalyticsModule
import com.doyoonkim.knutice.di.modules.WorkerModule
import com.doyoonkim.network.di.NetworkModule
import com.doyoonkim.notification.di.FcmTokenModule
import com.doyoonkim.notification.di.IntermediateWorkerFactory
import dagger.BindsInstance
import dagger.Component
import dagger.Subcomponent
import javax.inject.Provider

@Component(
    modules = [
        AppModule::class,
        FirebaseAnalyticsModule::class
    ]
)
interface AppComponent {

    fun inject(app: MainApplication)

    // Provision Functions
    @ApplicationContext fun applicationContext(): Context
    fun sharedPreference(): SharedPreferences
    fun alarmManager(): AlarmManager
    fun notificationManager(): NotificationManager

    // Analytics
    fun analytics(): AnalyticsLogger

    // Worker Subcomponent
    fun workerComponent(): WorkerSubcomponent.Factory

    @Component.Factory
    interface Factory {
        // provide AppComponent
        // @BindsInstance would bind provided 'Application' to DI graph.
        // Therefore, Application instance would be bind alongside with AppComponent creation.
        // --> This would provide 'Context' to DI graph as well because Application is a context.
        fun create(@BindsInstance application: Application): AppComponent
    }

}

@Subcomponent(
    modules = [
        DispatcherModule::class,
        WorkerModule::class,
        FcmTokenModule::class,
        TokenUseCaseModule::class,
        TokenRemoteModule::class,
        AsyncFtsEntryInsertionModule::class,
        LocalModule::class,
        NetworkModule::class
    ]
)
interface WorkerSubcomponent {

    // But WHY?
    fun workerMap(): Map<Class<out ListenableWorker>, @JvmSuppressWildcards Provider<IntermediateWorkerFactory>>

    @Subcomponent.Factory
    interface Factory {
        fun create(): WorkerSubcomponent
    }
}