package com.doyoonkim.knutice.di.components

import androidx.lifecycle.ViewModelProvider
import com.doyoonkim.bookmark.di.BookmarkListSceneModule
import com.doyoonkim.bookmark.di.EditBookmarkSceneModule
import com.doyoonkim.data.di.DataModule
import com.doyoonkim.domain.di.BookmarkUseCaseModule
import com.doyoonkim.domain.di.NoticeUseCaseModule
import com.doyoonkim.knutice.di.modules.DispatcherModule
import com.doyoonkim.knutice.di.modules.ViewModelFactoryModule
import com.doyoonkim.knutice.di.util.SystemServices
import com.doyoonkim.network.di.NetworkModule
import com.doyoonkim.notification.di.NotificationModule
import dagger.Component

@Component(
    dependencies = [SystemServices::class],
    modules = [
        ViewModelFactoryModule::class,
        DispatcherModule::class,
        BookmarkListSceneModule::class,
        BookmarkUseCaseModule::class,
        DataModule::class
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
        DataModule::class,
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