package com.doyoonkim.knutice.di.components

import android.app.Application
import androidx.work.ListenableWorker
import com.doyoonkim.common.di.AppPreferences
import com.doyoonkim.data.di.LocalModule
import com.doyoonkim.data.di.TokenRemoteModule
import com.doyoonkim.domain.di.AsyncFtsEntryInsertionModule
import com.doyoonkim.domain.di.TokenUseCaseModule
import com.doyoonkim.knutice.MainApplication
import com.doyoonkim.knutice.di.modules.AppModule
import com.doyoonkim.knutice.di.modules.DispatcherModule
import com.doyoonkim.knutice.di.modules.WorkerModule
import com.doyoonkim.network.di.NetworkModule
import com.doyoonkim.notification.di.FcmTokenModule
import com.doyoonkim.common.worker.IntermediateWorkerFactory
import com.doyoonkim.data.di.CampusRemoteModule
import com.doyoonkim.data.di.NoticeRemoteModule
import com.doyoonkim.data.di.RoomDatabaseModule
import com.doyoonkim.data.di.SystemCoroutineModule
import com.doyoonkim.infrastructure.di.FirebaseAnalyticsModule
import com.doyoonkim.infrastructure.di.FirebaseRemoteConfigModule
import com.doyoonkim.knutice.di.util.FirebaseInfrastructureProvider
import com.doyoonkim.knutice.di.util.LocalStorageProvider
import com.doyoonkim.knutice.di.util.NetworkProvider
import com.doyoonkim.knutice.di.util.SystemServices
import com.doyoonkim.widget.di.WidgetModule
import dagger.BindsInstance
import dagger.Component
import dagger.Subcomponent
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        FirebaseAnalyticsModule::class,
        FirebaseRemoteConfigModule::class,
        DispatcherModule::class,
        SystemCoroutineModule::class,
        LocalModule::class,
        RoomDatabaseModule::class,
        NetworkModule::class
    ]
)
interface AppComponent :
    SystemServices,
    NetworkProvider,
    LocalStorageProvider,
    FirebaseInfrastructureProvider
{
    fun inject(app: MainApplication)

    // AppPref Instance
    fun appPreference(): AppPreferences

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
        WorkerModule::class,
        WidgetModule::class,
        FcmTokenModule::class,
        TokenUseCaseModule::class,
        TokenRemoteModule::class,
        AsyncFtsEntryInsertionModule::class,
        NoticeRemoteModule::class,
        CampusRemoteModule::class
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