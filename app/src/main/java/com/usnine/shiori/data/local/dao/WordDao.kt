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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entities: List<WordEntity>)

    @Query("""
        SELECT * FROM words
        ORDER BY CASE partOfSpeech
            WHEN 'NOUN'         THEN 0
            WHEN 'VERB'         THEN 1
            WHEN 'I_ADJECTIVE'  THEN 2
            WHEN 'NA_ADJECTIVE' THEN 3
            WHEN 'ADVERB'       THEN 4
            WHEN 'CONJUNCTION'  THEN 5
            WHEN 'PARTICLE'     THEN 6
            WHEN 'OTHER'        THEN 7
            ELSE 8
        END, id ASC
    """)
    fun getAll(): Flow<List<WordEntity>>

    @Query("""
        SELECT * FROM words WHERE level = :level
        ORDER BY CASE partOfSpeech
            WHEN 'NOUN'         THEN 0
            WHEN 'VERB'         THEN 1
            WHEN 'I_ADJECTIVE'  THEN 2
            WHEN 'NA_ADJECTIVE' THEN 3
            WHEN 'ADVERB'       THEN 4
            WHEN 'CONJUNCTION'  THEN 5
            WHEN 'PARTICLE'     THEN 6
            WHEN 'OTHER'        THEN 7
            ELSE 8
        END, id ASC
    """)
    fun getByLevel(level: WordLevel): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE partOfSpeech = :pos ORDER BY id ASC")
    fun getByPartOfSpeech(pos: PartOfSpeech): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getById(id: Long): WordEntity?

    @Query("SELECT * FROM words")
    suspend fun getAllList(): List<WordEntity>

    @Query("SELECT * FROM words WHERE level = :level")
    suspend fun getByLevelList(level: WordLevel): List<WordEntity>

    @Query("SELECT COUNT(*) FROM words")
    suspend fun countAll(): Int

    @Query("SELECT COUNT(*) FROM words")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM words WHERE level = :level")
    suspend fun countByLevel(level: WordLevel): Int

    @Query("UPDATE words SET isBookmarked = :bookmarked WHERE id = :id")
    suspend fun updateBookmark(id: Long, bookmarked: Boolean)

    @Query("DELETE FROM words WHERE id = :id")
    suspend fun deleteById(id: Long)
}
