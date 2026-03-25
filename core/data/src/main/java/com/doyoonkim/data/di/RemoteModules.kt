package com.doyoonkim.data.di

import com.doyoonkim.data.repository.remote.CampusRemoteRepository
import com.doyoonkim.data.repository.remote.ImageRepositoryImpl
import com.doyoonkim.data.repository.remote.RemoteContentRepositoryImpl
import com.doyoonkim.data.repository.remote.RemotePreferenceRepositoryImpl
import com.doyoonkim.data.repository.remote.RemoteTokenRepositoryImpl
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
        impl: RemoteContentRepositoryImpl
    ) : NoticeRemoteRepository
}

@Module
abstract class TipRemoteModule {
    @Binds
    abstract fun bindsTipRemoteRepo(
        impl: RemoteContentRepositoryImpl
    ) : TipRemoteRepository
}

@Module
abstract class TokenRemoteModule {
    @Binds
    abstract fun bindsTokenRemoteRepo(
        impl: RemoteTokenRepositoryImpl
    ) : TokenRemoteRepository
}

@Module
abstract class PreferencesRemoteModule {
    @Binds
    abstract fun bindsTopicSubscriptionRemoteRepo(
        impl: RemotePreferenceRepositoryImpl
    ) : TopicSubscriptionRemoteRepository

    @Binds
    abstract fun bindsUserReportRemoteRepo(
        impl: RemotePreferenceRepositoryImpl
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