package com.usnine.shiori.presentation.feature.sentence.analysis

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.usnine.shiori.R
import com.usnine.shiori.data.local.entity.PartOfSpeech
import com.usnine.shiori.data.local.entity.WordLevel
import com.usnine.shiori.domain.usecase.SaveWordUseCase
import com.usnine.shiori.presentation.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val saveWordUseCase: SaveWordUseCase,
    @ApplicationContext private val context: Context,
) : MviViewModel<AnalysisContract.UiEvent, AnalysisContract.UiState, AnalysisContract.UiEffect>(
    initialState = AnalysisContract.UiState(words = dummyWords)
) {
    override fun handleEvent(event: AnalysisContract.UiEvent) {
        when (event) {
            is AnalysisContract.UiEvent.BookmarkTapped -> toggleBookmark(event.wordId)
        }
    }

    private fun toggleBookmark(wordId: Int) {
        val current = state.value.words.find { it.id == wordId } ?: return
        val toggled = !current.isBookmarked
        setState {
            copy(words = words.map { if (it.id == wordId) it.copy(isBookmarked = toggled) else it })
        }
        if (toggled) {
            viewModelScope.launch {
                saveWordUseCase(current)
                sendEffect(AnalysisContract.UiEffect.ShowSnackbar(
                    context.getString(R.string.analysis_bookmark_saved)
                ))
            }
        }
    }
}

// TODO: Phase 2 — replace with Claude API analysis results
private val dummyWords = listOf(
    WordUi(id = 1, japanese = "食べる",  reading = "たべる",     meaning = "먹다",   partOfSpeech = PartOfSpeech.VERB,         level = WordLevel.N5),
    WordUi(id = 2, japanese = "飲む",    reading = "のむ",       meaning = "마시다", partOfSpeech = PartOfSpeech.VERB,         level = WordLevel.N5),
    WordUi(id = 3, japanese = "綺麗",    reading = "きれい",     meaning = "예쁘다", partOfSpeech = PartOfSpeech.NA_ADJECTIVE, level = WordLevel.N4),
    WordUi(id = 4, japanese = "勉強",    reading = "べんきょう", meaning = "공부",   partOfSpeech = PartOfSpeech.NOUN,         level = WordLevel.N5),
    WordUi(id = 5, japanese = "難しい",  reading = "むずかしい", meaning = "어렵다", partOfSpeech = PartOfSpeech.I_ADJECTIVE,  level = WordLevel.N4),
    WordUi(id = 6, japanese = "友達",    reading = "ともだち",   meaning = "친구",   partOfSpeech = PartOfSpeech.NOUN,         level = WordLevel.N5),
)
