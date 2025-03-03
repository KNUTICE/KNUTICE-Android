package com.doyoonkim.knutice.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.doyoonkim.knutice.model.Bookmark
import com.doyoonkim.knutice.model.NoticeEntity

@Dao
interface MainDatabaseDao {
    @Query("SELECT * FROM Bookmark")
    fun getAllBookmarks(): List<Bookmark>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createBookmark(entity: Bookmark)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createNoticeEntity(entity: NoticeEntity)

    @Update
    fun updateBookmark(updated: Bookmark)

    @Delete
    fun deleteBookmark(target: Bookmark)

    @Delete
    fun deleteNoticeEntity(target: NoticeEntity)

    @Query("SELECT * FROM NoticeEntity WHERE ntt_id=:nttId")
    fun getNoticeByNttId(nttId: Int): NoticeEntity

    @Query("SELECT * FROM Bookmark WHERE target_ntt_id=:nttId")
    fun getBookmarkByNttId(nttId: Int): Bookmark?

}