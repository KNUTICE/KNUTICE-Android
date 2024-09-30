package com.doyoonkim.knutice.domain

import kotlinx.coroutines.flow.Flow

interface FetchTopThreeNotice {

    fun fetchTopThreeGeneralNotice(): Flow<TopThreeInCategory>

    fun fetchTopThreeAcademicNotice(): Flow<TopThreeInCategory>

    fun fetchTopThreeScholarshipNotice(): Flow<TopThreeInCategory>

    fun fetchTopThreeEventNotice(): Flow<TopThreeInCategory>

    fun fetchAllTopThreeNotice(): Flow<List<TopThreeInCategory>>

}