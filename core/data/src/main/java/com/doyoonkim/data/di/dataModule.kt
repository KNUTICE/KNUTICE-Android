package com.doyoonkim.data.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.doyoonkim.common.di.ApplicationContext
import com.doyoonkim.data.repository.ImageRepositoryImpl
import com.doyoonkim.data.repository.LocalRepositoryImpl
import com.doyoonkim.data.repository.RemoteRepositoryImpl
import com.doyoonkim.data.room.LocalDatabase
import com.doyoonkim.data.room.MainDatabaseDao
import com.doyoonkim.domain.ImageRepository
import com.doyoonkim.domain.LocalRepository
import com.doyoonkim.domain.RemoteRepository
import com.doyoonkim.network.ImageRemoteSource
import com.doyoonkim.network.KnuticeRemoteSource
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import javax.inject.Singleton

@Module
object DataModule {

    // Database
    @Provides
    @Singleton
    fun provideLocalDatabase(
        @ApplicationContext context: Context
    ): LocalDatabase {
        // @Provides annotation is used because providing LocalDatabase instance requires custom-logic for instantiation.
        return Room.databaseBuilder(
            context,
            LocalDatabase::class.java,
            "Main Local Database"
        ).setQueryCallback(
            { sqlQuery, bindArgs ->
                Log.d("SQL", "Query: $sqlQuery SQLArgs: $bindArgs")
            },
            Dispatchers.IO.asExecutor()
        ).build()
    }

    @Provides
    fun provideMainDatabaseDao(db: LocalDatabase) = db.getDao()

    @Provides
    fun provideLocalRepository(
        localDao: MainDatabaseDao
    ): LocalRepository {
        return LocalRepositoryImpl(localDao)
    }

    @Provides
    fun provideRemoteRepository(
        remoteSource: KnuticeRemoteSource
    ): RemoteRepository {
        return RemoteRepositoryImpl(remoteSource)
    }

    @Provides
    fun providesImageRepository(
        imageRemoteSource: ImageRemoteSource
    ): ImageRepository {
        return ImageRepositoryImpl(imageRemoteSource)
    }

}