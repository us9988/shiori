package com.usnine.shiori.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.usnine.shiori.data.local.entity.LearnedKanaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LearnedKanaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: LearnedKanaEntity)

    @Query("SELECT * FROM learned_kana ORDER BY learnedAt ASC")
    fun getAll(): Flow<List<LearnedKanaEntity>>

    @Query("DELETE FROM learned_kana WHERE kana = :kana")
    suspend fun deleteByKana(kana: String)

    @Query("DELETE FROM learned_kana")
    suspend fun deleteAll()
}
