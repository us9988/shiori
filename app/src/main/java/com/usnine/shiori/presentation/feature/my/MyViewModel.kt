package com.usnine.shiori.presentation.feature.my

import androidx.lifecycle.viewModelScope
import com.usnine.shiori.data.local.dao.WordDao
import com.usnine.shiori.data.local.entity.WordEntity
import com.usnine.shiori.data.local.entity.PartOfSpeech
import com.usnine.shiori.data.local.entity.WordLevel
import com.usnine.shiori.domain.usecase.GetStudyStatsUseCase
import com.usnine.shiori.presentation.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val wordDao: WordDao,
    private val getStudyStatsUseCase: GetStudyStatsUseCase,
) : MviViewModel<MyContract.UiEvent, MyContract.UiState, MyContract.UiEffect>(
    initialState = MyContract.UiState()
) {
    private var reviewQueue: List<WordUi> = emptyList()
    private var reviewIndex: Int = 0

    init {
        observeWords()
        observeStats()
    }

    override fun handleEvent(event: MyContract.UiEvent) {
        when (event) {
            is MyContract.UiEvent.BookmarkRemoved -> removeBookmark(event.wordId)
            MyContract.UiEvent.CardFlipped        -> setState { copy(isCardFlipped = !isCardFlipped) }
            MyContract.UiEvent.KnowTapped         -> advance(incrementCompleted = true)
            MyContract.UiEvent.AgainTapped        -> advance(incrementCompleted = false)
        }
    }

    private fun observeStats() {
        getStudyStatsUseCase()
            .onEach { stats ->
                setState {
                    copy(
                        totalBookmarks   = stats.totalBookmarks,
                        completedWords   = stats.completedWords,
                        todayReviewCount = stats.todayReviewCount,
                        streakDays       = stats.streakDays,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeWords() {
        wordDao.getAll()
            .onEach { entities ->
                val words = entities.map { it.toUi() }
                reviewQueue = words.shuffled()
                reviewIndex = 0
                setState {
                    copy(
                        bookmarks      = words,
                        sessionCompleted = 0,
                        currentCard    = reviewQueue.firstOrNull(),
                        isCardFlipped  = false,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun removeBookmark(wordId: Long) {
        viewModelScope.launch {
            wordDao.deleteById(wordId)
        }
    }

    private fun advance(incrementCompleted: Boolean) {
        // TODO: SM-2 알고리즘 (Phase 2)
        reviewIndex++
        val next = reviewQueue.getOrNull(reviewIndex)
        setState {
            copy(
                isCardFlipped    = false,
                currentCard      = next,
                sessionCompleted = if (incrementCompleted) sessionCompleted + 1 else sessionCompleted,
            )
        }
    }
}

private fun WordEntity.toUi() = WordUi(
    id           = id,
    japanese     = japanese,
    reading      = reading,
    meaning      = meaning,
    partOfSpeech = partOfSpeech,
    level        = level,
)
