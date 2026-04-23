package com.usnine.shiori.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.usnine.shiori.data.local.entity.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ReviewEntity): Long

    @Update
    suspend fun update(entity: ReviewEntity)

    @Query("SELECT * FROM reviews WHERE nextReviewDate <= :today ORDER BY nextReviewDate ASC")
    fun getDueToday(today: Long): Flow<List<ReviewEntity>>

    @Query("SELECT COUNT(*) FROM reviews WHERE reviewedAt >= :startOfDay")
    fun countTodayReviews(startOfDay: Long): Flow<Int>
}
