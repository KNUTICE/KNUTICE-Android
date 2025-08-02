package com.doyoonkim.knutice.di.components

import androidx.lifecycle.ViewModelProvider
import com.doyoonkim.bookmark.di.BookmarkListSceneModule
import com.doyoonkim.bookmark.di.EditBookmarkSceneModule
import com.doyoonkim.data.di.LocalModule
import com.doyoonkim.data.di.NoticeRemoteModule
import com.doyoonkim.data.di.PreferencesRemoteModule
import com.doyoonkim.data.di.TipRemoteModule
import com.doyoonkim.data.di.TokenRemoteModule
import com.doyoonkim.domain.di.BookmarkUseCaseModule
import com.doyoonkim.domain.di.NoticeUseCaseModule
import com.doyoonkim.domain.di.PreProcessingUseCaseModule
import com.doyoonkim.domain.di.PreferencesUseCaseModule
import com.doyoonkim.domain.di.TipUseCaseModule
import com.doyoonkim.domain.interfaces.BookmarkLocalRepository
import com.doyoonkim.knutice.di.modules.DispatcherModule
import com.doyoonkim.knutice.di.modules.ViewModelFactoryModule
import com.doyoonkim.knutice.di.util.SystemServices
import com.doyoonkim.main.di.CustomerServiceSceneModule
import com.doyoonkim.main.di.HomeSceneModule
import com.doyoonkim.main.di.NoticeDetailSceneModule
import com.doyoonkim.main.di.NoticeInCategorySceneModule
import com.doyoonkim.main.di.NoticeSearchSceneModule
import com.doyoonkim.main.di.NotificationPreferencesSceneModule
import com.doyoonkim.main.di.SettingsSceneModule
import com.doyoonkim.main.di.SplashSceneModule
import com.doyoonkim.network.di.NetworkModule
import com.doyoonkim.notification.di.NotificationModule
import dagger.Component

@Component(
    dependencies = [SystemServices::class],
    modules = [
        ViewModelFactoryModule::class,
        DispatcherModule::class,
        HomeSceneModule::class,
        NoticeUseCaseModule::class,
        TipUseCaseModule::class,
        NoticeRemoteModule::class,
        TipRemoteModule::class,
        NetworkModule::class
    ]
)
interface HomeSceneComponent {
    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(systemServices: SystemServices): HomeSceneComponent
    }
}


@Component(
    dependencies = [SystemServices::class],
    modules = [
        ViewModelFactoryModule::class,
        DispatcherModule::class,
        BookmarkListSceneModule::class,
        BookmarkUseCaseModule::class,
        LocalModule::class
    ]
)
interface BookmarkListSceneComponent {
    fun getViewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(systemServices: SystemServices): BookmarkListSceneComponent
    }
}


@Component(
    dependencies = [SystemServices::class],
    modules = [
        ViewModelFactoryModule::class,
        DispatcherModule::class,
        EditBookmarkSceneModule::class,
        BookmarkUseCaseModule::class,
        NoticeUseCaseModule::class,
        NotificationModule::class,
        LocalModule::class,
        NoticeRemoteModule::class,
        NetworkModule::class
    ]
)
interface EditBookmarkSceneComponent {
    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(systemService: SystemServices): EditBookmarkSceneComponent
    }
}

@Component(
    dependencies = [SystemServices::class],
    modules = [
        ViewModelFactoryModule::class,
        DispatcherModule::class,
        NoticeDetailSceneModule::class,
        NoticeUseCaseModule::class,
        NoticeRemoteModule::class,
        NetworkModule::class
    ]
)
interface NoticeDetailSceneComponent {
    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(systemServices: SystemServices): NoticeDetailSceneComponent
    }
}


@Component(
    dependencies = [SystemServices::class],
    modules = [
        ViewModelFactoryModule::class,
        DispatcherModule::class,
        NoticeSearchSceneModule::class,
        NoticeUseCaseModule::class,
        BookmarkUseCaseModule::class,
        NoticeRemoteModule::class,
        LocalModule::class,
        NetworkModule::class
    ]
)
interface NoticeSearchSceneComponent {
    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(systemServices: SystemServices): NoticeSearchSceneComponent
    }
}


@Component(
    dependencies = [SystemServices::class],
    modules = [
        ViewModelFactoryModule::class,
        DispatcherModule::class,
        NoticeInCategorySceneModule::class,
        NoticeUseCaseModule::class,
        NoticeRemoteModule::class,
        NetworkModule::class
    ]
)
interface NoticeInCategorySceneComponent {
    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(systemServices: SystemServices): NoticeInCategorySceneComponent
    }
}


@Component(
    dependencies = [SystemServices::class],
    modules = [
        ViewModelFactoryModule::class,
        DispatcherModule::class,
        CustomerServiceSceneModule::class,
        PreferencesUseCaseModule::class,
        PreferencesRemoteModule::class,
        NetworkModule::class
    ]
)
interface CustomerServiceSceneComponent {
    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(systemService: SystemServices): CustomerServiceSceneComponent
    }
}


@Component(
    dependencies = [SystemServices::class],
    modules = [
        ViewModelFactoryModule::class,
        DispatcherModule::class,
        NotificationPreferencesSceneModule::class,
        PreferencesUseCaseModule::class,
        PreferencesRemoteModule::class,
        NetworkModule::class
    ]
)
interface NotificationPreferencesSceneComponent {
    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(systemServices: SystemServices): NotificationPreferencesSceneComponent
    }
}


@Component(
    dependencies = [SystemServices::class],
    modules = [
        ViewModelFactoryModule::class,
        DispatcherModule::class,
        SettingsSceneModule::class,
        PreProcessingUseCaseModule::class,
        LocalModule::class,
        NoticeRemoteModule::class,
        NetworkModule::class
    ]
)
interface SettingsSceneComponent {
    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(systemServices: SystemServices): SettingsSceneComponent
    }
}


@Component(
    dependencies = [SystemServices::class],
    modules = [
        ViewModelFactoryModule::class,
        DispatcherModule::class,
        SplashSceneModule::class,
        NotificationModule::class,
        PreProcessingUseCaseModule::class,
        LocalModule::class,
        NoticeRemoteModule::class,
        TokenRemoteModule::class,
        NetworkModule::class
    ]
)
interface SplashSceneComponent {
    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(systemServices: SystemServices): SplashSceneComponent
    }
}