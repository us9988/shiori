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
    val koreanReading: String,
    val meaning: String,
    val partOfSpeech: PartOfSpeech,
    val level: WordLevel,
    val isBookmarked: Boolean = false,
)

interface WordContract {

    data class UiState(
        val sortType: WordSortType = WordSortType.LEVEL,
        val selectedLevel: WordLevel = WordLevel.N5,
        val selectedPos: PartOfSpeech? = null,
        val words: List<WordUi> = emptyList(),
        val detailWordId: Long? = null,
        val isLoading: Boolean = false,
        val isSearchActive: Boolean = false,
        val searchQuery: String = "",
    ) : MviState

    sealed interface UiEvent : MviEvent {
        data class SortTypeChanged(val sortType: WordSortType) : UiEvent
        data class LevelSelected(val level: WordLevel) : UiEvent
        data class PosSelected(val pos: PartOfSpeech?) : UiEvent
        data class WordTapped(val wordId: Long) : UiEvent
        object DetailDismissed : UiEvent
        data class BookmarkToggled(val wordId: Long) : UiEvent
        data object SearchToggled : UiEvent
        data class SearchQueryChanged(val query: String) : UiEvent
    }

    sealed interface UiEffect : MviEffect
}
