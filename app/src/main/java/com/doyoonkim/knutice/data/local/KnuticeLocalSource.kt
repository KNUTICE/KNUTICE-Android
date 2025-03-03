package com.doyoonkim.knutice.data.local

import android.content.Context
import com.doyoonkim.knutice.model.Bookmark
import com.doyoonkim.knutice.model.Notice
import com.doyoonkim.knutice.model.NoticeEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class KnuticeLocalSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Local Database
    private val localDatabase: LocalDatabase by lazy {
        LocalDatabase.getInstance(context)
    }

    // CURD
    fun createBookmark(bookmark: Bookmark): Result<Boolean> {
        return try {
            localDatabase.getDao().createBookmark(bookmark)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun createBookmark(bookmark: Bookmark, targetNotice: Notice): Result<Boolean> {
        return runCatching {
            // Insert Notice First.
            localDatabase.getDao().createNoticeEntity(targetNotice.toNoticeEntity())
            // Insert Bookmark Entity
            localDatabase.getDao().createBookmark(bookmark)
            true
        }.onFailure { throw it }
    }

    fun updateBookmark(bookmark: Bookmark): Result<Boolean> {
        return runCatching {
            localDatabase.getDao().updateBookmark(bookmark)
            true
        }.onFailure { throw it }
    }

    fun getAllBookmarks(): List<Bookmark> {
        return localDatabase.getDao().getAllBookmarks()
    }

    fun deleteBookmark(bookmark: Bookmark): Result<Boolean> {
        return runCatching {
            localDatabase.getDao().deleteBookmark(bookmark)
            true
        }.onFailure { throw it }
    }

    fun deleteNoticeEntity(entity: NoticeEntity): Result<Boolean> {
        return runCatching {
            localDatabase.getDao().deleteNoticeEntity(entity)
            true
        }.onFailure { throw it }
    }

    fun getNoticeByNttId(nttId: Int): NoticeEntity {
        return localDatabase.getDao().getNoticeByNttId(nttId)
    }

    fun getBookmarkByNttId(nttId: Int): Bookmark? {
        return localDatabase.getDao().getBookmarkByNttId(nttId)
    }

}