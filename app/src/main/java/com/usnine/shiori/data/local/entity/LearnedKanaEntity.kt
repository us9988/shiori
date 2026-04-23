package com.usnine.shiori.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "learned_kana")
data class LearnedKanaEntity(
    @PrimaryKey val kana: String,
    val learnedAt: Long = System.currentTimeMillis(),
)
