package com.usnine.shiori.presentation.feature.home

import androidx.lifecycle.viewModelScope
import com.usnine.shiori.data.local.StreakDataStore
import com.usnine.shiori.data.local.dao.PhraseDao
import com.usnine.shiori.data.local.dao.WordDao
import com.usnine.shiori.data.local.entity.PhraseEntity
import com.usnine.shiori.data.local.entity.WordEntity
import com.usnine.shiori.domain.usecase.GetTodayMissionUseCase
import com.usnine.shiori.domain.usecase.ToggleWordBookmarkUseCase
import com.usnine.shiori.presentation.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val streakDataStore: StreakDataStore,
    private val getTodayMissionUseCase: GetTodayMissionUseCase,
    private val wordDao: WordDao,
    private val phraseDao: PhraseDao,
    private val toggleWordBookmark: ToggleWordBookmarkUseCase,
) : MviViewModel<HomeContract.UiEvent, HomeContract.UiState, HomeContract.UiEffect>(
    initialState = HomeContract.UiState()
) {
    private var allWords: List<WordEntity> = emptyList()
    private var allPhrases: List<PhraseEntity> = emptyList()
    private var wordIndex: Int = 0
    private var phraseIndex: Int = 0

    init {
        recordAndObserveStreak()
        loadMission()
        loadDailyContent()
    }

    override fun handleEvent(event: HomeContract.UiEvent) {
        when (event) {
            HomeContract.UiEvent.LoadData                 -> loadMission()
            HomeContract.UiEvent.WordOfDayBookmarkToggled -> toggleWodBookmark()
            HomeContract.UiEvent.NextWordOfDay            -> nextWord()
            HomeContract.UiEvent.NextPhraseOfDay          -> nextPhrase()
        }
    }

    private fun recordAndObserveStreak() {
        viewModelScope.launch { streakDataStore.recordStudyToday() }

        streakDataStore.streakDays
            .onEach { days -> setState { copy(streak = days) } }
            .launchIn(viewModelScope)
    }

    private fun loadMission() {
        viewModelScope.launch {
            val mission = getTodayMissionUseCase()
            setState { copy(todayMission = mission) }
        }
    }

    private fun loadDailyContent() {
        viewModelScope.launch {
            val seed = LocalDate.now().toEpochDay()

            allWords = wordDao.getAll().first()
            if (allWords.isNotEmpty()) {
                wordIndex = (seed % allWords.size).toInt()
                setState { copy(wordOfDay = allWords[wordIndex].toUi()) }
            }

            allPhrases = phraseDao.getAll().first()
            if (allPhrases.isNotEmpty()) {
                phraseIndex = (seed % allPhrases.size).toInt()
                setState { copy(phraseOfDay = allPhrases[phraseIndex].toUi()) }
            }
        }
    }

    private fun nextWord() {
        if (allWords.size < 2) return
        wordIndex = (0 until allWords.size).filter { it != wordIndex }.random()
        setState { copy(wordOfDay = allWords[wordIndex].toUi()) }
    }

    private fun nextPhrase() {
        if (allPhrases.size < 2) return
        phraseIndex = (0 until allPhrases.size).filter { it != phraseIndex }.random()
        setState { copy(phraseOfDay = allPhrases[phraseIndex].toUi()) }
    }

    private fun toggleWodBookmark() {
        val wod = state.value.wordOfDay ?: return
        viewModelScope.launch {
            toggleWordBookmark(wod.id)
            setState { copy(wordOfDay = wordOfDay?.copy(isBookmarked = !wod.isBookmarked)) }
        }
    }
}

private fun WordEntity.toUi() = WordOfDayUi(
    id            = id,
    japanese      = japanese,
    reading       = reading,
    koreanReading = koreanReading,
    meaning       = meaning,
    partOfSpeech  = partOfSpeech,
    level         = level,
    isBookmarked  = isBookmarked,
)

private fun PhraseEntity.toUi() = PhraseOfDayUi(
    japanese = japanese,
    hiragana = hiragana,
    korean   = korean,
    category = category,
)
