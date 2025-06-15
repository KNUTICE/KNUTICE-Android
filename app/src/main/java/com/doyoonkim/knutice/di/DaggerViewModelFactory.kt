package com.doyoonkim.knutice.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class DaggerViewModelFactory @Inject constructor(
    private val providers: @JvmSuppressWildcards Map<Class<out ViewModel>, Provider<ViewModel>>
) : ViewModelProvider.Factory {
    // Function to lazily creates ViewModel instance
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val provider = providers[modelClass]
            ?: providers.entries.firstOrNull { modelClass.isAssignableFrom(it.key) }?.value
            ?:throw IllegalArgumentException("Unknown ViewModel Class $modelClass")

        @Suppress("UNCHECKED_CAST")
        return provider.get() as T
    }
}