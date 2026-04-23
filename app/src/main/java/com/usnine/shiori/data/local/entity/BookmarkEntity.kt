package com.usnine.shiori.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val japaneseText: String,
    val koreanText: String,
    val source: String,
    val savedAt: Long = System.currentTimeMillis(),
)
