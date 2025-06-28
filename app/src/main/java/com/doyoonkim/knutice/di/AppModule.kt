package com.doyoonkim.knutice.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
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

    @Provides
    @Singleton
    fun providesApplicationPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences("app_pref", Context.MODE_PRIVATE)

}