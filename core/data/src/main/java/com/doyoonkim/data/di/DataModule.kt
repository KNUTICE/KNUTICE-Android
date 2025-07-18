package com.doyoonkim.data.di

import android.content.Context
import com.doyoonkim.common.di.ApplicationContext
import com.doyoonkim.data.repository.ImageRepositoryImpl
import com.doyoonkim.data.repository.LocalRepositoryImpl
import com.doyoonkim.data.repository.RemoteRepositoryImpl
import com.doyoonkim.data.room.LocalDatabase
import com.doyoonkim.domain.interfaces.BookmarkLocalRepository
import com.doyoonkim.domain.interfaces.ImageRemoteRepository
import com.doyoonkim.domain.interfaces.NoticeLocalRepository
import com.doyoonkim.domain.interfaces.NoticeRemoteRepository
import com.doyoonkim.domain.interfaces.TipRemoteRepository
import com.doyoonkim.domain.interfaces.TokenRemoteRepository
import com.doyoonkim.domain.interfaces.TopicSubscriptionRemoteRepository
import com.doyoonkim.domain.interfaces.UserReportRemoteRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class DataModule {

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
    abstract fun bindsNoticeRemoteRepo(
        impl: RemoteRepositoryImpl
    ) : NoticeRemoteRepository

    @Binds
    abstract fun bindsTipRemoteRepo(
        impl: RemoteRepositoryImpl
    ) : TipRemoteRepository

    @Binds
    abstract fun bindsTokenRemoteRepo(
        impl: RemoteRepositoryImpl
    ) : TokenRemoteRepository

    @Binds
    abstract fun bindsTopicSubscriptionRemoteRepo(
        impl: RemoteRepositoryImpl
    ) : TopicSubscriptionRemoteRepository

    @Binds
    abstract fun bindsUserReportRemoteRepo(
        impl: RemoteRepositoryImpl
    ) : UserReportRemoteRepository

    @Binds
    abstract fun bindsImageRemoteRepo(
        impl: ImageRepositoryImpl
    ) : ImageRemoteRepository

    @Binds
    abstract fun bindsBookmarkLocalRepository(
        impl: LocalRepositoryImpl
    ) : BookmarkLocalRepository

    @Binds
    abstract fun bindsNoticeLocalRepository(
        impl: LocalRepositoryImpl
    ) : NoticeLocalRepository

}