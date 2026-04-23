package com.usnine.shiori.presentation.feature.sentence.analysis

import com.usnine.shiori.data.local.entity.PartOfSpeech
import com.usnine.shiori.data.local.entity.WordLevel
import com.usnine.shiori.presentation.base.MviEffect
import com.usnine.shiori.presentation.base.MviEvent
import com.usnine.shiori.presentation.base.MviState

data class WordUi(
    val id: Int,
    val japanese: String,
    val reading: String,
    val meaning: String,
    val partOfSpeech: PartOfSpeech,
    val level: WordLevel,
    val isBookmarked: Boolean = false,
)

interface AnalysisContract {

    data class UiState(
        val words: List<WordUi> = emptyList(),
        val isLoading: Boolean = false,
    ) : MviState

    sealed interface UiEvent : MviEvent {
        data class BookmarkTapped(val wordId: Int) : UiEvent
        // TODO: InputChanged, AnalyzeTapped (Phase 2 — Claude API)
    }

    sealed interface UiEffect : MviEffect {
        data class ShowSnackbar(val message: String) : UiEffect
    }
}
