package com.usnine.shiori.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.usnine.shiori.data.local.entity.PartOfSpeech
import com.usnine.shiori.data.local.entity.WordEntity
import com.usnine.shiori.data.local.entity.WordLevel
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WordEntity): Long

    @Query("SELECT * FROM words ORDER BY id DESC")
    fun getAll(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE level = :level ORDER BY id DESC")
    fun getByLevel(level: WordLevel): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE partOfSpeech = :pos ORDER BY id DESC")
    fun getByPartOfSpeech(pos: PartOfSpeech): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getById(id: Long): WordEntity?

    @Query("SELECT * FROM words WHERE sourceBookmarkId = :bookmarkId")
    fun getByBookmarkId(bookmarkId: Long): Flow<List<WordEntity>>

    @Query("SELECT COUNT(*) FROM words WHERE isCompleted = 1")
    fun countCompleted(): Flow<Int>

    @Query("UPDATE words SET isCompleted = :completed WHERE id = :id")
    suspend fun updateCompleted(id: Long, completed: Boolean)

    @Query("UPDATE words SET isBookmarked = :bookmarked WHERE id = :id")
    suspend fun updateBookmark(id: Long, bookmarked: Boolean)

    @Query("DELETE FROM words WHERE id = :id")
    suspend fun deleteById(id: Long)
}
