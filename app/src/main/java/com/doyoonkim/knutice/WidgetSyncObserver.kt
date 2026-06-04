package com.doyoonkim.knutice

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.doyoonkim.domain.interfaces.AppSubscriptionPreferenceRepository
import com.doyoonkim.domain.interfaces.AppWidgetPreferenceRepository
import com.doyoonkim.domain.interfaces.LocalWidgetCacheRepository
import com.doyoonkim.model.WidgetCategoryPolicy
import com.doyoonkim.widget.model.CarrelWidgetState
import com.doyoonkim.widget.model.WidgetNoticeVO
import com.doyoonkim.widget.model.WidgetState
import com.doyoonkim.widget.util.WidgetStateUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class WidgetSyncObserver @Inject constructor(
    private val localWidgetCacheRepository: LocalWidgetCacheRepository,
    private val widgetStateUpdater: WidgetStateUpdater,
    private val appWidgetPreference: AppWidgetPreferenceRepository,
    private val appSubscriptionPreference: AppSubscriptionPreferenceRepository,
    private val applicationScope: CoroutineScope
): DefaultLifecycleObserver {

    companion object {
        private const val TAG = "WidgetSyncObserver"
    }

    override fun onStop(owner: LifecycleOwner) {
        // Update Widget on App enters its onStop Status.
        updateNoticeWidgetLocalCache()
        updateCarrelWidgetLocalCache()
    }

    private fun updateNoticeWidgetLocalCache() {
        Log.d(TAG, "Update Notice Widget via Cache")
        // Check Notice Widget Local Cache
        val noticeCache = localWidgetCacheRepository.noticeCacheState.value.map { vo ->
            WidgetNoticeVO(
                id = vo.nttId,
                title = vo.title,
                info = "[${vo.departName}] ${vo.timestamp}",
                contentUrl = vo.url
            )
        }
        val categoryPolicy = appWidgetPreference.getWidgetCategoryPolicy()
        // Category Mismatch: Recent Category Changed via WidgetConfiguration. Widget is already updated.
        if (categoryPolicy != localWidgetCacheRepository.widgetCategoryCacheState.value) return

        if (noticeCache.isNotEmpty()) {
            applicationScope.launch {
                with(widgetStateUpdater) {
                    when (categoryPolicy) {
                        is WidgetCategoryPolicy.Main -> {
                            updateNoticeWidgetState(
                                WidgetState(categoryPolicy.categoryKey, noticeCache)
                            )
                        }
                        is WidgetCategoryPolicy.Major -> {
                            val majorCategory = appSubscriptionPreference.getSubscribedMajor()
                            majorCategory?.let {
                                updateNoticeWidgetState(
                                    WidgetState(it, noticeCache)
                                )
                            }
                        }
                        else -> { /* DO NOT UPDATE WIDGET */ }
                    }
                }
            }
        }
    }

    private fun updateCarrelWidgetLocalCache() {
        Log.d(TAG, "Update Carrel Widget via Cache")
        val carrelCache = localWidgetCacheRepository.carrelCacheState.value
        if (carrelCache.isNotEmpty()) {
            applicationScope.launch {
                widgetStateUpdater.updateCarrelWidgetState(CarrelWidgetState(carrelCache))
            }
        }
    }

}