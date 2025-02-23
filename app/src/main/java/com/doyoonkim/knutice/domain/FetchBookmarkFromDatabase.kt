package com.doyoonkim.knutice.domain

import com.doyoonkim.knutice.data.NoticeLocalRepository
import com.doyoonkim.knutice.model.Bookmark
import com.doyoonkim.knutice.model.Notice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FetchBookmarkFromDatabase @Inject constructor(
    private val repository: NoticeLocalRepository
) {

    fun getAllBookmarkWithNotices(): Flow<Pair<Bookmark, Notice>> {
        return repository.getAllBookmarks().map {
            Pair(it, repository.getNoticeByNttId(it.nttId))
        }
    }

}