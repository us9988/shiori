package com.usnine.shiori.presentation.feature.my

import androidx.lifecycle.viewModelScope
import com.usnine.shiori.data.local.ThemePreferenceStore
import com.usnine.shiori.data.local.dao.WordDao
import com.usnine.shiori.data.local.entity.WordEntity
import com.usnine.shiori.domain.usecase.GetStudyStatsUseCase
import com.usnine.shiori.presentation.base.MviViewModel
import com.usnine.shiori.presentation.billing.BillingManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val wordDao: WordDao,
    private val getStudyStatsUseCase: GetStudyStatsUseCase,
    private val themePreferenceStore: ThemePreferenceStore,
    private val billingManager: BillingManager,
) : MviViewModel<MyContract.UiEvent, MyContract.UiState, MyContract.UiEffect>(
    initialState = MyContract.UiState()
) {
    init {
        observeBookmarks()
        observeStats()
        observeDarkMode()
        observePremium()
    }

    override fun handleEvent(event: MyContract.UiEvent) {
        when (event) {
            MyContract.UiEvent.WordListOpened     -> setState { copy(showWordList = true) }
            MyContract.UiEvent.WordListClosed     -> setState { copy(showWordList = false) }
            MyContract.UiEvent.PremiumModalOpened -> setState { copy(showPremiumModal = true) }
            MyContract.UiEvent.PremiumModalClosed -> setState { copy(showPremiumModal = false) }
            is MyContract.UiEvent.BookmarkRemoved -> removeBookmark(event.wordId)
            MyContract.UiEvent.CardFlipped        -> setState { copy(isCardFlipped = !isCardFlipped) }
            MyContract.UiEvent.NextTapped         -> advance()
            MyContract.UiEvent.DarkModeToggled       -> toggleDarkMode()
            MyContract.UiEvent.RateTapped            -> sendEffect(MyContract.UiEffect.LaunchPlayStore)
            MyContract.UiEvent.PurchaseCompleted     -> onPurchaseCompleted()
            MyContract.UiEvent.PurchaseTapped        -> sendEffect(MyContract.UiEffect.LaunchPurchaseFlow)
            MyContract.UiEvent.RestorePurchaseTapped -> onRestorePurchase()
        }
    }

    private fun onRestorePurchase() {
        viewModelScope.launch {
            val restored = billingManager.restorePurchase()
            sendEffect(
                if (restored) MyContract.UiEffect.ShowRestoreSuccess
                else MyContract.UiEffect.ShowRestoreFailure
            )
        }
    }

    private fun observePremium() {
        billingManager.isPremium
            .onEach { premium -> setState { copy(isPremium = premium) } }
            .launchIn(viewModelScope)
    }

    private fun onPurchaseCompleted() {
        setState { copy(showPremiumModal = false) }
    }

    private fun observeDarkMode() {
        themePreferenceStore.isDarkMode
            .onEach { enabled -> setState { copy(isDarkMode = enabled) } }
            .launchIn(viewModelScope)
    }

    private fun toggleDarkMode() {
        viewModelScope.launch {
            themePreferenceStore.setDarkMode(!state.value.isDarkMode)
        }
    }

    private fun observeStats() {
        getStudyStatsUseCase()
            .onEach { stats ->
                setState {
                    copy(
                        streakDays       = stats.streakDays,
                        todayReviewCount = stats.todayReviewCount,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeBookmarks() {
        wordDao.getAll()
            .onEach { entities ->
                val bookmarked = entities.filter { it.isBookmarked }.map { it.toUi() }
                setState {
                    copy(
                        bookmarks     = bookmarked,
                        currentCard   = if (currentCard == null || !bookmarked.any { it.id == currentCard.id })
                                            bookmarked.randomOrNull()
                                        else currentCard,
                        isCardFlipped = false,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun removeBookmark(wordId: Long) {
        viewModelScope.launch {
            wordDao.updateBookmark(wordId, false)
        }
    }

    private fun advance() {
        val bookmarks = state.value.bookmarks
        val next = when {
            bookmarks.isEmpty() -> null
            bookmarks.size == 1 -> bookmarks.first()
            else -> bookmarks.filter { it.id != state.value.currentCard?.id }.random()
        }
        setState { copy(isCardFlipped = false, currentCard = next) }
    }
}

private fun WordEntity.toUi() = WordUi(
    id             = id,
    japanese       = japanese,
    reading        = reading,
    koreanReading  = koreanReading,
    meaning        = meaning,
    partOfSpeech   = partOfSpeech,
    level          = level,
)
