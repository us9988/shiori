package com.usnine.shiori.domain.usecase

import android.content.Context
import com.usnine.shiori.R
import com.usnine.shiori.data.local.KanaData
import com.usnine.shiori.data.local.dao.LearnedKanaDao
import com.usnine.shiori.data.local.dao.WordDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetTodayMissionUseCase @Inject constructor(
    private val learnedKanaDao: LearnedKanaDao,
    private val wordDao: WordDao,
    @ApplicationContext private val context: Context,
) {
    suspend operator fun invoke(): String {
        val learnedKana = learnedKanaDao.getAll().first().map { it.kana }.toSet()

        // 1. 히라가나 미완성 행
        KanaData.hiraganaRows.firstOrNull { row ->
            row.items.filterNotNull().any { it.kana !in learnedKana }
        }?.let { row ->
            return context.getString(R.string.mission_hiragana_quiz, row.label)
        }

        // 2. 가타카나 미완성 행
        KanaData.katakanaRows.firstOrNull { row ->
            row.items.filterNotNull().any { it.kana !in learnedKana }
        }?.let { row ->
            return context.getString(R.string.mission_katakana_quiz, row.label)
        }

        // 3. 복습 단어 있음
        val wordCount = wordDao.getAll().first().size
        if (wordCount > 0) {
            return context.getString(R.string.mission_word_review, wordCount)
        }

        // 4. 다 완료
        return context.getString(R.string.mission_new_analysis)
    }
}
