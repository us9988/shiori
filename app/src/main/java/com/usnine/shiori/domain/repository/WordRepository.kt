package com.usnine.shiori.domain.repository

import com.usnine.shiori.data.local.entity.PartOfSpeech
import com.usnine.shiori.data.local.entity.WordLevel
import com.usnine.shiori.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun getAll(): Flow<List<Word>>
    fun getByLevel(level: WordLevel): Flow<List<Word>>
    fun getByPartOfSpeech(pos: PartOfSpeech): Flow<List<Word>>
    suspend fun toggleBookmark(wordId: Long)
}
