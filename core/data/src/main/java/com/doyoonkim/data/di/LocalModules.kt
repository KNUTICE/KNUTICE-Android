package com.doyoonkim.data.di

import android.content.Context
import com.doyoonkim.data.repository.local.AppPreferenceRepositoryImpl
import com.doyoonkim.model.di.ApplicationContext
import com.doyoonkim.data.repository.local.LocalRepositoryImpl
import com.doyoonkim.data.repository.local.LocalWidgetCacheRepositoryImpl
import com.doyoonkim.data.room.LocalDatabase
import com.doyoonkim.domain.interfaces.AppPreferenceRepository
import com.doyoonkim.domain.interfaces.BookmarkLocalRepository
import com.doyoonkim.domain.interfaces.LocalWidgetCacheRepository
import com.doyoonkim.domain.interfaces.NoticeLocalRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class LocalModule {
    @Binds
    abstract fun bindsBookmarkLocalRepository(
        impl: LocalRepositoryImpl
    ) : BookmarkLocalRepository

    @Binds
    abstract fun bindsNoticeLocalRepository(
        impl: LocalRepositoryImpl
    ) : NoticeLocalRepository

    @Binds
    @Singleton
    abstract fun bindLocalWidgetCacheRepository(
        impl: LocalWidgetCacheRepositoryImpl
    ): LocalWidgetCacheRepository
}

@Module
object RoomDatabaseModule {
    @Provides
    @Singleton
    fun provideLocalDatabase(
        @ApplicationContext context: Context
    ): LocalDatabase {
        return LocalDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideMainDatabaseDao(db: LocalDatabase) = db.getDao()

}

@Module
abstract class LocalPreferenceModule {
    @Binds
    @Singleton
    abstract fun bindLocalAppPreferenceRepository(
        impl: AppPreferenceRepositoryImpl
    ): AppPreferenceRepository
}