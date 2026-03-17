package com.doyoonkim.knutice.di.components


import com.doyoonkim.knutice.MainActivity
import com.doyoonkim.knutice.di.util.FirebaseInfrastructureProvider
import com.doyoonkim.knutice.di.util.SystemServices
import dagger.Component

@Component(
    dependencies = [
        SystemServices::class,
        FirebaseInfrastructureProvider::class
    ]
)
interface MainActivityComponent {
    fun inject(activity: MainActivity)

    @Component.Factory
    interface Factory {
        fun create(
            systemServices: SystemServices,
            firebaseInfraProvider: FirebaseInfrastructureProvider
        ): MainActivityComponent
    }
}