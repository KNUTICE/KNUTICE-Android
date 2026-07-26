package com.doyoonkim.data.repository.local

import android.util.Log
import com.doyoonkim.domain.interfaces.AppWidgetPreferenceRepository
import com.doyoonkim.domain.interfaces.LocalWidgetCacheRepository
import com.doyoonkim.model.CarrelRoomStatusVO
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class LocalWidgetCacheRepositoryImpl @Inject constructor(
    appWidgetPreference: AppWidgetPreferenceRepository
) : LocalWidgetCacheRepository {

    companion object {
        private const val TAG = "LocalWidgetCacheRepositoryImpl"
    }

    // StatFlow (Manage In-Memory Cache)
    private val _noticeCacheState = MutableStateFlow<List<NoticeVO>>(emptyList())
    override val noticeCacheState = _noticeCacheState.asStateFlow()

    private val _widgetCategoryCacheState = MutableStateFlow(appWidgetPreference.getWidgetCategoryPolicy())
    override val widgetCategoryCacheState = _widgetCategoryCacheState.asStateFlow()

    private val _carrelCacheState = MutableStateFlow<List<CarrelRoomStatusVO>>(emptyList())
    override val carrelCacheState = _carrelCacheState.asStateFlow()

    override fun updateNoticeCache(notices: List<NoticeVO>) {
        Log.d(TAG, "Notice Cache Updated")
        _noticeCacheState.update { notices }
    }

    override fun updateCarrelCache(status: List<CarrelRoomStatusVO>) {
        Log.d(TAG, "Carrel Cache Updated")
        _carrelCacheState.update { status }
    }
}
