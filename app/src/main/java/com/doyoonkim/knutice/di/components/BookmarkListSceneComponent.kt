package com.doyoonkim.knutice.di.components

import androidx.lifecycle.ViewModelProvider
import com.doyoonkim.bookmark.di.BookmarkModule
import com.doyoonkim.data.di.DataModule
import com.doyoonkim.domain.di.DomainModule
import com.doyoonkim.knutice.di.DispatcherModule
import com.doyoonkim.knutice.di.ViewModelFactoryModule
import dagger.Component

@Component(
    dependencies = [AppComponent::class],
    modules = [
        ViewModelFactoryModule::class,
        DispatcherModule::class,
        BookmarkModule::class,
        DomainModule::class,
        DataModule::class,
    ]
)
interface BookmarkListSceneComponent {

    fun getViewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): BookmarkListSceneComponent
    }

}