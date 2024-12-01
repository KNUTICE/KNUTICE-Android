package com.doyoonkim.knutice.domain

import com.doyoonkim.knutice.data.NoticeLocalRepository
import com.doyoonkim.knutice.model.DetailedContentState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CrawlFullContentImpl @Inject constructor(
    private val repository: NoticeLocalRepository
): CrawlFullContent {
    override fun getFullContentFromSource(
        title: String,
        info: String,
        url: String
    ): Flow<DetailedContentState> {
        return repository.getFullNoticeContent(url)
            .map {
                DetailedContentState(
                    title = title,
                    info = info,
                    fullContent = it,
                    fullContentUrl = url
                )
            }
    }
}