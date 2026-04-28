package com.usnine.shiori.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.usnine.shiori.data.local.entity.PhraseCategory
import com.usnine.shiori.data.local.entity.PhraseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhraseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(phrases: List<PhraseEntity>)

    @Query("SELECT * FROM phrases ORDER BY category, id")
    fun getAll(): Flow<List<PhraseEntity>>

    @Query("SELECT * FROM phrases WHERE category = :category ORDER BY id")
    fun getByCategory(category: PhraseCategory): Flow<List<PhraseEntity>>

    @Query("SELECT * FROM phrases WHERE id = :id")
    suspend fun getById(id: Long): PhraseEntity?

    @Query("SELECT COUNT(*) FROM phrases")
    suspend fun getCount(): Int
}
