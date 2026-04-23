package com.usnine.shiori.domain.usecase

import com.usnine.shiori.data.local.dao.WordDao
import com.usnine.shiori.data.local.entity.WordEntity
import com.usnine.shiori.presentation.feature.sentence.analysis.WordUi
import javax.inject.Inject

class SaveWordUseCase @Inject constructor(
    private val wordDao: WordDao,
) {
    suspend operator fun invoke(word: WordUi): Long = wordDao.insert(
        WordEntity(
            japanese     = word.japanese,
            reading      = word.reading,
            meaning      = word.meaning,
            partOfSpeech = word.partOfSpeech,
            level        = word.level,
        )
    )
}
