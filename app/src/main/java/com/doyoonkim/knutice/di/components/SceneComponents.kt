package com.doyoonkim.knutice.di.components

import androidx.lifecycle.ViewModelProvider
import com.doyoonkim.bookmark.di.BookmarkListSceneModule
import com.doyoonkim.bookmark.di.EditBookmarkSceneModule
import com.doyoonkim.data.di.CampusRemoteModule
import com.doyoonkim.data.di.NoticeRemoteModule
import com.doyoonkim.data.di.PreferencesRemoteModule
import com.doyoonkim.data.di.TipRemoteModule
import com.doyoonkim.data.di.TokenRemoteModule
import com.doyoonkim.domain.di.BookmarkUseCaseModule
import com.doyoonkim.domain.di.NoticeUseCaseModule
import com.doyoonkim.domain.di.PreProcessingUseCaseModule
import com.doyoonkim.domain.di.PreferencesUseCaseModule
import com.doyoonkim.domain.di.TipUseCaseModule
import com.doyoonkim.knutice.di.modules.ViewModelFactoryModule
import com.doyoonkim.knutice.di.modules.WorkSchedulerModule
import com.doyoonkim.knutice.di.util.FirebaseInfrastructureProvider
import com.doyoonkim.knutice.di.util.LocalCacheProvider
import com.doyoonkim.knutice.di.util.LocalPreferenceProvider
import com.doyoonkim.knutice.di.util.LocalStorageProvider
import com.doyoonkim.knutice.di.util.NetworkProvider
import com.doyoonkim.knutice.di.util.SystemServices
import com.doyoonkim.main.di.CarrelStatusSceneModule
import com.doyoonkim.main.di.CustomerServiceSceneModule
import com.doyoonkim.main.di.HomeSceneModule
import com.doyoonkim.main.di.NoticeByMajorSceneModule
import com.doyoonkim.main.di.NoticeDetailSceneModule
import com.doyoonkim.main.di.NoticeInCategorySceneModule
import com.doyoonkim.main.di.NoticeSearchSceneModule
import com.doyoonkim.main.di.NotificationPreferencesSceneModule
import com.doyoonkim.main.di.SettingsSceneModule
import com.doyoonkim.main.di.SplashSceneModule
import com.doyoonkim.main.di.WidgetConfigSceneModule
import com.doyoonkim.notification.di.NotificationModule
import dagger.Component
import javax.inject.Singleton

@Component(
    dependencies = [
        SystemServices::class,
        LocalCacheProvider::class,
        LocalPreferenceProvider::class,
        NetworkProvider::class,
        FirebaseInfrastructureProvider::class
    ],
    modules = [
        ViewModelFactoryModule::class,
        HomeSceneModule::class,
        NoticeUseCaseModule::class,
        TipUseCaseModule::class,
        NoticeRemoteModule::class,
        TipRemoteModule::class,
    ]
)
interface HomeSceneComponent {
    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(
            systemServices: SystemServices,
            networkProvider: NetworkProvider,
            localCacheProvider: LocalCacheProvider,
            localPreferenceProvider: LocalPreferenceProvider,
            firebaseInfrastructureProvider: FirebaseInfrastructureProvider
        ): HomeSceneComponent
    }
}

@Component(
    dependencies = [
        SystemServices::class,
        NetworkProvider::class,
        LocalPreferenceProvider::class
    ],
    modules = [
        ViewModelFactoryModule::class,
        NoticeByMajorSceneModule::class,
        NoticeUseCaseModule::class,
        PreferencesUseCaseModule::class,
        NoticeRemoteModule::class,
        PreferencesRemoteModule::class
    ]
)
interface NoticeByMajorSceneComponent {
    fun getViewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(
            systemServices: SystemServices,
            networkProvider: NetworkProvider,
            localPreferenceProvider: LocalPreferenceProvider
        ): NoticeByMajorSceneComponent
    }
}


@Component(
    dependencies = [
        SystemServices::class,
        LocalStorageProvider::class,
        LocalPreferenceProvider::class
    ],
    modules = [
        ViewModelFactoryModule::class,
        BookmarkListSceneModule::class,
        BookmarkUseCaseModule::class,
    ]
)
interface BookmarkListSceneComponent {
    fun getViewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(
            systemServices: SystemServices,
            localPreferenceProvider: LocalPreferenceProvider,
            localStorageProvider: LocalStorageProvider
        ): BookmarkListSceneComponent
    }
}

@Singleton
@Component(
    dependencies = [
        SystemServices::class,
        NetworkProvider::class,
        LocalStorageProvider::class
    ],
    modules = [
        ViewModelFactoryModule::class,
        WorkSchedulerModule::class,
        EditBookmarkSceneModule::class,
        BookmarkUseCaseModule::class,
        NoticeUseCaseModule::class,
        NotificationModule::class,
        NoticeRemoteModule::class,
    ]
)
interface EditBookmarkSceneComponent {
    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(
            systemService: SystemServices,
            networkProvider: NetworkProvider,
            localStorageProvider: LocalStorageProvider
        ): EditBookmarkSceneComponent
    }
}

@Component(
    dependencies = [
        SystemServices::class,
        NetworkProvider::class,
        FirebaseInfrastructureProvider::class
    ],
    modules = [
        ViewModelFactoryModule::class,
        NoticeDetailSceneModule::class,
        NoticeUseCaseModule::class,
        NoticeRemoteModule::class,
    ]
)
interface NoticeDetailSceneComponent {
    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(
            systemServices: SystemServices,
            networkProvider: NetworkProvider,
            firebaseInfrastructureProvider: FirebaseInfrastructureProvider
        ): NoticeDetailSceneComponent
    }
}


@Component(
    dependencies = [SystemServices::class, NetworkProvider::class, LocalStorageProvider::class],
    modules = [
        ViewModelFactoryModule::class,
        NoticeSearchSceneModule::class,
        NoticeUseCaseModule::class,
        BookmarkUseCaseModule::class,
        NoticeRemoteModule::class
    ]
)
interface NoticeSearchSceneComponent {
    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(
            systemServices: SystemServices,
            networkProvider: NetworkProvider,
            localStorageProvider: LocalStorageProvider
        ): NoticeSearchSceneComponent
    }
}


@Component(
    dependencies = [SystemServices::class, NetworkProvider::class],
    modules = [
        ViewModelFactoryModule::class,
        NoticeInCategorySceneModule::class,
        NoticeUseCaseModule::class,
        NoticeRemoteModule::class
    ]
)
interface NoticeInCategorySceneComponent {
    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(
            systemServices: SystemServices,
            networkProvider: NetworkProvider
        ): NoticeInCategorySceneComponent
    }
}


@Component(
    dependencies = [
        SystemServices::class,
        LocalPreferenceProvider::class,
        NetworkProvider::class
    ],
    modules = [
        ViewModelFactoryModule::class,
        CustomerServiceSceneModule::class,
        PreferencesUseCaseModule::class,
        PreferencesRemoteModule::class
    ]
)
interface CustomerServiceSceneComponent {
    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(
            systemService: SystemServices,
            localPreferenceProvider: LocalPreferenceProvider,
            networkProvider: NetworkProvider
        ): CustomerServiceSceneComponent
    }
}


@Component(
    dependencies = [
        SystemServices::class,
        LocalPreferenceProvider::class,
        NetworkProvider::class
    ],
    modules = [
        ViewModelFactoryModule::class,
        NotificationPreferencesSceneModule::class,
        PreferencesUseCaseModule::class,
        PreferencesRemoteModule::class
    ]
)
interface NotificationPreferencesSceneComponent {
    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(
            systemServices: SystemServices,
            localPreferenceProvider: LocalPreferenceProvider,
            networkProvider: NetworkProvider
        ): NotificationPreferencesSceneComponent
    }
}


@Component(
    dependencies = [
        SystemServices::class,
        NetworkProvider::class,
        LocalStorageProvider::class,
        LocalPreferenceProvider::class
    ],
    modules = [
        ViewModelFactoryModule::class,
        SettingsSceneModule::class,
        PreProcessingUseCaseModule::class,
        NoticeRemoteModule::class
    ]
)
interface SettingsSceneComponent {
    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(
            systemServices: SystemServices,
            networkProvider: NetworkProvider,
            localStorageProvider: LocalStorageProvider,
            localPreferenceProvider: LocalPreferenceProvider
        ): SettingsSceneComponent
    }
}


@Component(
    dependencies = [
        SystemServices::class,
        NetworkProvider::class,
        LocalStorageProvider::class,
        LocalPreferenceProvider::class,
        FirebaseInfrastructureProvider::class
    ],
    modules = [
        ViewModelFactoryModule::class,
        SplashSceneModule::class,
        NotificationModule::class,
        PreProcessingUseCaseModule::class,
        NoticeRemoteModule::class,
        TokenRemoteModule::class,
    ]
)
interface SplashSceneComponent {
    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(
            systemServices: SystemServices,
            networkProvider: NetworkProvider,
            localStorageProvider: LocalStorageProvider,
            localPreferenceProvider: LocalPreferenceProvider,
            firebaseInfrastructureProvider: FirebaseInfrastructureProvider
        ): SplashSceneComponent
    }
}

@Component(
    dependencies = [SystemServices::class, LocalPreferenceProvider::class],
    modules = [
        ViewModelFactoryModule::class,
        WidgetConfigSceneModule::class,
        WorkSchedulerModule::class,
    ]
)
interface WidgetConfigSceneComponent {

    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(
            systemService: SystemServices,
            localPreferenceProvider: LocalPreferenceProvider
        ): WidgetConfigSceneComponent
    }
}

@Component(
    dependencies = [
        SystemServices::class,
        NetworkProvider::class,
        LocalCacheProvider::class,
        LocalPreferenceProvider::class
    ],
    modules = [
        ViewModelFactoryModule::class,
        CarrelStatusSceneModule::class,
        CampusRemoteModule::class
    ]
)
interface CarrelStatusSceneComponent {
    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(
            systemServices: SystemServices,
            networkProvider: NetworkProvider,
            localCacheProvider: LocalCacheProvider,
            localPreferenceProvider: LocalPreferenceProvider
        ): CarrelStatusSceneComponent
    }
}