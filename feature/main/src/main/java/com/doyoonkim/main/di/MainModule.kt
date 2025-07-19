package com.doyoonkim.main.di

import androidx.lifecycle.ViewModel
import com.doyoonkim.common.di.ViewModelKey
import com.doyoonkim.main.viewmodel.CustomerServiceViewModel
import com.doyoonkim.main.viewmodel.HomeViewModel
import com.doyoonkim.main.viewmodel.NoticeDetailViewModel
import com.doyoonkim.main.viewmodel.NoticeSearchViewModel
import com.doyoonkim.main.viewmodel.NoticesInCategoryViewModel
import com.doyoonkim.main.viewmodel.NotificationPreferencesViewModel
import com.doyoonkim.main.viewmodel.SettingsViewModel
import com.doyoonkim.main.viewmodel.SplashViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindsHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NoticeDetailViewModel::class)
    abstract fun bindsNoticeDetailViewModel(viewModel: NoticeDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NoticeSearchViewModel::class)
    abstract fun bindsNoticeSearchViewModel(viewModel: NoticeSearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NoticesInCategoryViewModel::class)
    abstract fun bindsNoticesInCategoryViewModel(viewModel: NoticesInCategoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NotificationPreferencesViewModel::class)
    abstract fun bindsNotificationPreferencesViewModel(viewModel: NotificationPreferencesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CustomerServiceViewModel::class)
    abstract fun bindsCustomerServiceViewModel(viewModel:CustomerServiceViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindsSplashViewModel(viewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindsSettingsViewModel(viewModel: SettingsViewModel): ViewModel

}