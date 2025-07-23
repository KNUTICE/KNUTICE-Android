package com.doyoonkim.knutice.di.modules

import androidx.lifecycle.ViewModelProvider
import com.doyoonkim.knutice.di.util.DaggerViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    // @Binds annotation instructs Dagger to use the provided implementation.
    @Binds
    abstract fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory
}