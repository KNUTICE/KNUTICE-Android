package com.doyoonkim.knutice.di

import android.app.Application
import android.content.Context
import com.doyoonkim.common.di.ApplicationContext
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {

    // Inject ApplicationContext
    @Provides
    @Singleton
    @ApplicationContext
    fun providesApplicationContext(app: Application): Context = app.applicationContext

}