package com.doyoonkim.main.di

import androidx.lifecycle.ViewModel
import com.doyoonkim.common.di.ViewModelKey
import com.doyoonkim.main.viewmodel.CustomerServiceViewModel
import com.doyoonkim.main.viewmodel.HomeViewModel
import com.doyoonkim.main.viewmodel.NoticeByMajorViewModel
import com.doyoonkim.main.viewmodel.NoticeDetailViewModel
import com.doyoonkim.main.viewmodel.NoticeSearchViewModel
import com.doyoonkim.main.viewmodel.NoticesInCategoryViewModel
import com.doyoonkim.main.viewmodel.NotificationPreferencesViewModel
import com.doyoonkim.main.viewmodel.SettingsViewModel
import com.doyoonkim.main.viewmodel.SplashViewModel
import com.doyoonkim.main.viewmodel.WidgetConfigViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class HomeSceneModule {
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindsViewModel(viewModel: HomeViewModel): ViewModel
}

@Module
abstract class NoticeByMajorSceneModule {
    @Binds
    @IntoMap
    @ViewModelKey(NoticeByMajorViewModel::class)
    abstract fun bindViewModel(viewModel: NoticeByMajorViewModel): ViewModel
}

@Module
abstract class NoticeDetailSceneModule {
    @Binds
    @IntoMap
    @ViewModelKey(NoticeDetailViewModel::class)
    abstract fun bindsViewModel(viewModel: NoticeDetailViewModel): ViewModel
}

@Module
abstract class NoticeSearchSceneModule {
    @Binds
    @IntoMap
    @ViewModelKey(NoticeSearchViewModel::class)
    abstract fun bindsViewModel(viewModel: NoticeSearchViewModel): ViewModel
}

@Module
abstract class NoticeInCategorySceneModule {
    @Binds
    @IntoMap
    @ViewModelKey(NoticesInCategoryViewModel::class)
    abstract fun bindsViewModel(viewModel: NoticesInCategoryViewModel): ViewModel
}

@Module
abstract class CustomerServiceSceneModule {
    @Binds
    @IntoMap
    @ViewModelKey(CustomerServiceViewModel::class)
    abstract fun bindsViewModel(viewModel: CustomerServiceViewModel): ViewModel
}

@Module
abstract class NotificationPreferencesSceneModule {
    @Binds
    @IntoMap
    @ViewModelKey(NotificationPreferencesViewModel::class)
    abstract fun bindsViewModel(viewModel: NotificationPreferencesViewModel): ViewModel
}

@Module
abstract class SettingsSceneModule {
    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindsViewModel(viewModel: SettingsViewModel): ViewModel
}

@Module
abstract class SplashSceneModule {
    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindsViewModel(viewModel: SplashViewModel): ViewModel
}

@Module
abstract class WidgetConfigSceneModule {
    @Binds
    @IntoMap
    @ViewModelKey(WidgetConfigViewModel::class)
    abstract fun bindsViewModel(viewModel: WidgetConfigViewModel): ViewModel
}