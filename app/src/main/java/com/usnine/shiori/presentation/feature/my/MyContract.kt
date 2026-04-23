package com.usnine.shiori.presentation.feature.my

import com.usnine.shiori.data.local.entity.PartOfSpeech
import com.usnine.shiori.data.local.entity.WordLevel
import com.usnine.shiori.presentation.base.MviEffect
import com.usnine.shiori.presentation.base.MviEvent
import com.usnine.shiori.presentation.base.MviState

data class WordUi(
    val id: Long,
    val japanese: String,
    val reading: String,
    val meaning: String,
    val partOfSpeech: PartOfSpeech,
    val level: WordLevel,
)

interface MyContract {

    data class UiState(
        // 플래시카드 세션
        val bookmarks: List<WordUi> = emptyList(),
        val currentCard: WordUi? = null,
        val isCardFlipped: Boolean = false,
        val sessionCompleted: Int = 0,   // 이번 세션 알아요 탭 횟수
        // 학습 통계 (GetStudyStatsUseCase)
        val totalBookmarks: Int = 0,     // 학습한 글자 수 (LearnedKana)
        val completedWords: Int = 0,     // 완료 단어 수 (isCompleted = true)
        val todayReviewCount: Int = 0,   // 오늘 복습 횟수
        val streakDays: Int = 0,
    ) : MviState

    sealed interface UiEvent : MviEvent {
        data class BookmarkRemoved(val wordId: Long) : UiEvent
        data object CardFlipped  : UiEvent
        data object KnowTapped   : UiEvent
        data object AgainTapped  : UiEvent
    }

    sealed interface UiEffect : MviEffect
}
