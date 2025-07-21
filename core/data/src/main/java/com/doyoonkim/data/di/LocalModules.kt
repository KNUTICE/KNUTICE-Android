package com.doyoonkim.data.di

import android.content.Context
import com.doyoonkim.common.di.ApplicationContext
import com.doyoonkim.data.repository.LocalRepositoryImpl
import com.doyoonkim.data.room.LocalDatabase
import com.doyoonkim.domain.interfaces.BookmarkLocalRepository
import com.doyoonkim.domain.interfaces.NoticeLocalRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class LocalModule {
    // Provides
    companion object {
        // Database
        @Provides
        fun provideLocalDatabase(
            @ApplicationContext context: Context
        ): LocalDatabase {
            return LocalDatabase.getInstance(context)
        }

        @Provides
        fun provideMainDatabaseDao(db: LocalDatabase) = db.getDao()
    }

    @Binds
    abstract fun bindsBookmarkLocalRepository(
        impl: LocalRepositoryImpl
    ) : BookmarkLocalRepository

    @Binds
    abstract fun bindsNoticeLocalRepository(
        impl: LocalRepositoryImpl
    ) : NoticeLocalRepository
}