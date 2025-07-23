package com.doyoonkim.bookmark.di

import androidx.lifecycle.ViewModel
import com.doyoonkim.bookmark.viewmodel.BookmarkListViewModel
import com.doyoonkim.bookmark.viewmodel.EditBookmarkViewModel
import com.doyoonkim.common.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

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