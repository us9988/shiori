package com.usnine.shiori.domain.model

import com.usnine.shiori.data.local.entity.PartOfSpeech
import com.usnine.shiori.data.local.entity.WordLevel

data class Word(
    val id: Long,
    val japanese: String,
    val reading: String,
    val koreanReading: String,
    val meaning: String,
    val partOfSpeech: PartOfSpeech,
    val level: WordLevel,
    val isBookmarked: Boolean,
)
