package com.usnine.shiori.presentation.feature.home

import com.usnine.shiori.data.local.entity.PartOfSpeech
import com.usnine.shiori.data.local.entity.PhraseCategory
import com.usnine.shiori.data.local.entity.WordLevel
import com.usnine.shiori.presentation.base.MviEffect
import com.usnine.shiori.presentation.base.MviEvent
import com.usnine.shiori.presentation.base.MviState

// GetRecentBookmarksUseCase가 이 타입을 참조하므로 유지
data class BookmarkUi(
    val id: Long,
    val jpText: String,
    val krText: String,
    val source: String,
)

data class WordOfDayUi(
    val id: Long,
    val japanese: String,
    val reading: String,
    val koreanReading: String,
    val meaning: String,
    val partOfSpeech: PartOfSpeech,
    val level: WordLevel,
    val isBookmarked: Boolean,
)

data class PhraseOfDayUi(
    val japanese: String,
    val hiragana: String,
    val korean: String,
    val category: PhraseCategory,
)

interface HomeContract {

    data class UiState(
        val streak: Int = 0,
        val todayMission: String = "",
        val wordOfDay: WordOfDayUi? = null,
        val phraseOfDay: PhraseOfDayUi? = null,
    ) : MviState

    sealed interface UiEvent : MviEvent {
        data object LoadData : UiEvent
        data object WordOfDayBookmarkToggled : UiEvent
        data object NextWordOfDay : UiEvent
        data object NextPhraseOfDay : UiEvent
    }

    sealed interface UiEffect : MviEffect
}
