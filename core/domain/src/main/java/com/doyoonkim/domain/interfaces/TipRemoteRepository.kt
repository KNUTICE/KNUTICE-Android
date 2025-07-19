package com.doyoonkim.domain.interfaces

import com.doyoonkim.model.TipVO
import kotlinx.coroutines.flow.Flow

interface TipRemoteRepository {
    fun queryAllTips(): Flow<List<TipVO>?>
}