package com.doyoonkim.knutice.di.components

import android.app.Application
import com.doyoonkim.bookmark.di.BookmarkModule
import com.doyoonkim.common.di.CommonModule
import com.doyoonkim.data.di.DataBindingModule
import com.doyoonkim.data.di.DataModule
import com.doyoonkim.domain.di.DomainModule
import com.doyoonkim.knutice.MainActivity
import com.doyoonkim.knutice.MainApplication
import com.doyoonkim.knutice.di.AppModule
import com.doyoonkim.knutice.di.DispatcherModule
import com.doyoonkim.knutice.di.ViewModelFactoryModule
import com.doyoonkim.main.di.MainModule
import com.doyoonkim.network.di.NetworkModule
import com.doyoonkim.notification.di.NotificationBindingModule
import com.doyoonkim.notification.di.NotificationModule
import com.doyoonkim.notification.fcm.PushNotificationService
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        DispatcherModule::class,
        CommonModule::class,
        DataModule::class,
        DataBindingModule::class,
        DomainModule::class,
        NetworkModule::class,
        NotificationModule::class,
        NotificationBindingModule::class,
        BookmarkModule::class,
        MainModule::class,
        ViewModelFactoryModule::class
    ]
)
interface AppComponent {

    fun inject(app: MainApplication)

    fun inject(activity: MainActivity)

    fun inject(service: PushNotificationService)

    @Component.Factory
    interface Factory {
        // provide AppComponent
        // @BindsInstance would bind provided 'Application' to DI graph.
        // Therefore, Application instance would be bind alongside with AppComponent creation.
        // --> This would provide 'Context' to DI graph as well because Application is a context.
        fun create(@BindsInstance application: Application): AppComponent
    }

}