package com.usnine.shiori.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PhraseCategory(val label: String) {
    GREETING("인사"),
    CAFE("카페"),
    SHOPPING("쇼핑"),
    TRANSPORT("교통"),
    ACCOMMODATION("숙박"),
}

@Entity(tableName = "phrases")
data class PhraseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val japanese: String,
    val hiragana: String,
    val korean: String,
    val category: PhraseCategory,
    val level: String,
    val audioFileName: String,
)
