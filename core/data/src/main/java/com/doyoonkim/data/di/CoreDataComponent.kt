package com.doyoonkim.data.di

import android.app.Application
import com.doyoonkim.network.di.NetworkModule
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        DataModule::class,
        NetworkModule::class
    ]
)
interface CoreDataComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): CoreDataComponent
    }
}