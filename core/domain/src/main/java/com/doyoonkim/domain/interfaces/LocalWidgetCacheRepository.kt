package com.doyoonkim.domain.interfaces

import com.doyoonkim.model.CarrelRoomStatusVO
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.model.WidgetCategoryPolicy
import kotlinx.coroutines.flow.StateFlow

interface LocalWidgetCacheRepository {

    val noticeCacheState: StateFlow<List<NoticeVO>>
    val widgetCategoryCacheState: StateFlow<WidgetCategoryPolicy>
    val carrelCacheState: StateFlow<List<CarrelRoomStatusVO>>

    fun updateNoticeCache(notices: List<NoticeVO>)
    fun updateCarrelCache(status: List<CarrelRoomStatusVO>)
}
