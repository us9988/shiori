package com.usnine.shiori.domain.model

data class Bookmark(
    val id: Long = 0,
    val japaneseText: String,
    val koreanText: String,
    val source: String,
    val savedAt: Long = System.currentTimeMillis(),
)
