package com.doyoonkim.knutice.di.components

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import com.doyoonkim.common.di.ApplicationContext
import com.doyoonkim.knutice.MainApplication
import com.doyoonkim.knutice.di.modules.AppModule
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        AppModule::class
    ]
)
interface AppComponent {

    fun inject(app: MainApplication)

    // Provision Functions
    @ApplicationContext fun applicationContext(): Context
    fun sharedPreference(): SharedPreferences
    fun alarmManager(): AlarmManager
    fun notificationManager(): NotificationManager

    @Component.Factory
    interface Factory {
        // provide AppComponent
        // @BindsInstance would bind provided 'Application' to DI graph.
        // Therefore, Application instance would be bind alongside with AppComponent creation.
        // --> This would provide 'Context' to DI graph as well because Application is a context.
        fun create(@BindsInstance application: Application): AppComponent
    }

}