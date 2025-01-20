package com.doyoonkim.knutice.data.local

import android.content.Context
import com.doyoonkim.knutice.model.Bookmark
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
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

    fun updateBookmark(bookmark: Bookmark): Result<Boolean> {
        return runCatching {
            localDatabase.getDao().updateBookmark(bookmark)
            true
        }.onFailure { throw it }
    }

    fun getAllBookmarks(): Result<List<Bookmark>> {
        return runCatching {
            localDatabase.getDao().getAllBookmarks()
        }.onFailure { throw it }
    }

    fun deleteBookmark(bookmark: Bookmark): Result<Boolean> {
        return runCatching {
            localDatabase.getDao().deleteBookmark(bookmark)
            true
        }.onFailure { throw it }
    }

}