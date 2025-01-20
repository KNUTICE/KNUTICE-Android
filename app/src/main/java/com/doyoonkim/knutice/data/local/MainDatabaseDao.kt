package com.doyoonkim.knutice.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.doyoonkim.knutice.model.Bookmark

@Dao
interface MainDatabaseDao {
    @Query("SELECT * FROM Bookmark")
    fun getAllBookmarks(): List<Bookmark>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createBookmark(entity: Bookmark)

    @Update
    fun updateBookmark(updated: Bookmark)

    @Delete
    fun deleteBookmark(target: Bookmark)
}