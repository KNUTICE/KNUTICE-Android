package com.doyoonkim.domain.interfaces

import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.flow.Flow

interface NoticeLocalRepository {

    fun queryNoticeById(nttId: Int): Flow<NoticeVO?>

    fun updateNoticeEntity(notice: NoticeVO): Flow<Boolean>

}