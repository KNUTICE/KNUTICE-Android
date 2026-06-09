package com.doyoonkim.knutice.di.modules

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.work.WorkManager
import com.doyoonkim.model.di.ApplicationContext
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
        // Only one instance of the SharedPreferences object is returned to any callers for the same name
        context.applicationContext.getSharedPreferences("app_pref", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun providesAlarmManager(@ApplicationContext context: Context) : AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // WorkManager
    @Provides
    @Singleton
    fun providesWorkManager(@ApplicationContext context: Context) =
        WorkManager.getInstance(context)

    // PreferenceDataStore
    @Provides
    @Singleton
    fun providePreferenceDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            // Partial Key migration: SUBSCRIBED MAJOR
            migrations = listOf(
                SharedPreferencesMigration(
                    context = context,
                    sharedPreferencesName = "app_pref",
                    // Once full migration is required, remove this line to let Migration triggers ALL_KEYS_MIGRATION.
                    keysToMigrate = setOf("SUBSCRIBED_MAJOR")
                )
            ),
            produceFile = {
                context.preferencesDataStoreFile(name = "user_preferences")
            }
        )

}