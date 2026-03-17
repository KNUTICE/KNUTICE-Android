package com.doyoonkim.data.di

import com.doyoonkim.data.repository.CampusRemoteRepository
import com.doyoonkim.data.repository.ImageRepositoryImpl
import com.doyoonkim.data.repository.RemoteRepositoryImpl
import com.doyoonkim.domain.interfaces.CarrelStatusRemoteRepository
import com.doyoonkim.domain.interfaces.ImageRemoteRepository
import com.doyoonkim.domain.interfaces.NoticeRemoteRepository
import com.doyoonkim.domain.interfaces.TipRemoteRepository
import com.doyoonkim.domain.interfaces.TokenRemoteRepository
import com.doyoonkim.domain.interfaces.TopicSubscriptionRemoteRepository
import com.doyoonkim.domain.interfaces.UserReportRemoteRepository
import dagger.Binds
import dagger.Module

@Module
abstract class NoticeRemoteModule {
    @Binds
    abstract fun bindsNoticeRemoteRepo(
        impl: RemoteRepositoryImpl
    ) : NoticeRemoteRepository
}

@Module
abstract class TipRemoteModule {
    @Binds
    abstract fun bindsTipRemoteRepo(
        impl: RemoteRepositoryImpl
    ) : TipRemoteRepository
}

@Module
abstract class TokenRemoteModule {
    @Binds
    abstract fun bindsTokenRemoteRepo(
        impl: RemoteRepositoryImpl
    ) : TokenRemoteRepository
}

@Module
abstract class PreferencesRemoteModule {
    @Binds
    abstract fun bindsTopicSubscriptionRemoteRepo(
        impl: RemoteRepositoryImpl
    ) : TopicSubscriptionRemoteRepository

    @Binds
    abstract fun bindsUserReportRemoteRepo(
        impl: RemoteRepositoryImpl
    ) : UserReportRemoteRepository
}

@Module
abstract class ImageRemoteModule {
    @Binds
    abstract fun bindsImageRemoteRepo(
        impl: ImageRepositoryImpl
    ) : ImageRemoteRepository
}

@Module
abstract class CampusRemoteModule {
    @Binds
    abstract fun bindsCarrelRoomRemoteRepo(
        impl: CampusRemoteRepository
    ): CarrelStatusRemoteRepository
}