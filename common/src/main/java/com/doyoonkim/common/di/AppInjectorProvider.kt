package com.doyoonkim.common.di

/**
 * Use it with extreme cautions
 */
interface AppInjectorProvider {
    val appInjector: AppInjector
}

interface AppInjector {
    fun inject(target: Any)
}
