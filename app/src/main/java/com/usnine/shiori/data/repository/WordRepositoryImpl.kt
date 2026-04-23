package com.usnine.shiori.data.repository

import com.usnine.shiori.data.local.dao.WordDao
import com.usnine.shiori.data.local.entity.PartOfSpeech
import com.usnine.shiori.data.local.entity.WordEntity
import com.usnine.shiori.data.local.entity.WordLevel
import com.usnine.shiori.domain.model.Word
import com.usnine.shiori.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(
    private val wordDao: WordDao,
) : WordRepository {

    override fun getByLevel(level: WordLevel): Flow<List<Word>> =
        wordDao.getByLevel(level).map { list -> list.map { it.toDomain() } }

    override fun getByPartOfSpeech(pos: PartOfSpeech): Flow<List<Word>> =
        wordDao.getByPartOfSpeech(pos).map { list -> list.map { it.toDomain() } }

    override suspend fun toggleCompleted(wordId: Long) {
        val current = wordDao.getById(wordId) ?: return
        wordDao.updateCompleted(wordId, !current.isCompleted)
    }

    override suspend fun toggleBookmark(wordId: Long) {
        val current = wordDao.getById(wordId) ?: return
        wordDao.updateBookmark(wordId, !current.isBookmarked)
    }
}

private fun WordEntity.toDomain() = Word(
    id           = id,
    japanese     = japanese,
    reading      = reading,
    meaning      = meaning,
    partOfSpeech = partOfSpeech,
    level        = level,
    isCompleted  = isCompleted,
    isBookmarked = isBookmarked,
)
