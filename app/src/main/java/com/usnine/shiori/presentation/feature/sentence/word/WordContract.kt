package com.usnine.shiori.presentation.feature.sentence.word

import com.usnine.shiori.data.local.entity.PartOfSpeech
import com.usnine.shiori.data.local.entity.WordLevel
import com.usnine.shiori.presentation.base.MviEffect
import com.usnine.shiori.presentation.base.MviEvent
import com.usnine.shiori.presentation.base.MviState

enum class WordSortType { LEVEL, PART_OF_SPEECH }

data class WordUi(
    val id: Long,
    val japanese: String,
    val reading: String,
    val meaning: String,
    val partOfSpeech: PartOfSpeech,
    val level: WordLevel,
    val isCompleted: Boolean = false,
    val isBookmarked: Boolean = false,
)

interface WordContract {

    data class UiState(
        val sortType: WordSortType = WordSortType.LEVEL,
        val selectedLevel: WordLevel = WordLevel.N5,
        val selectedPos: PartOfSpeech = PartOfSpeech.NOUN,
        val words: List<WordUi> = emptyList(),
        val isLoading: Boolean = false,
    ) : MviState

    sealed interface UiEvent : MviEvent {
        data class SortTypeChanged(val sortType: WordSortType) : UiEvent
        data class LevelSelected(val level: WordLevel) : UiEvent
        data class PosSelected(val pos: PartOfSpeech) : UiEvent
        data class BookmarkToggled(val wordId: Long) : UiEvent
        data class CompletedToggled(val wordId: Long) : UiEvent
    }

    sealed interface UiEffect : MviEffect
}
