package com.doyoonkim.bookmark.di

import androidx.lifecycle.ViewModel
import com.doyoonkim.bookmark.viewmodel.BookmarkListViewModel
import com.doyoonkim.bookmark.viewmodel.EditBookmarkViewModel
import com.doyoonkim.common.di.ViewModelKey
import com.doyoonkim.domain.di.BookmarkUseCaseModule
import com.doyoonkim.domain.di.NoticeUseCaseModule
import com.doyoonkim.notification.di.NotificationModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class BookmarkModule {

    @Binds
    @IntoMap
    @ViewModelKey(BookmarkListViewModel::class)
    abstract fun bindsBookmarkListViewModel(viewModel: BookmarkListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditBookmarkViewModel::class)
    abstract fun bindsEditBookmarkViewModel(viewModel: EditBookmarkViewModel): ViewModel

}

@Module
abstract class BookmarkListSceneModule {
    @Binds
    @IntoMap
    @ViewModelKey(BookmarkListViewModel::class)
    abstract fun bindsBookmarkListViewModel(vm: BookmarkListViewModel): ViewModel
}

@Module
abstract class EditBookmarkSceneModule {
    @Binds
    @IntoMap
    @ViewModelKey(EditBookmarkViewModel::class)
    abstract fun bindsEditBookmarkViewModel(viewModel: EditBookmarkViewModel): ViewModel
}