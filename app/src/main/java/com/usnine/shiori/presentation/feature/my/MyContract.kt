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
    val koreanReading: String,
    val meaning: String,
    val partOfSpeech: PartOfSpeech,
    val level: WordLevel,
)

interface MyContract {

    data class UiState(
        val showWordList: Boolean = false,
        val showPremiumModal: Boolean = false,
        val bookmarks: List<WordUi> = emptyList(),
        val currentCard: WordUi? = null,
        val isCardFlipped: Boolean = false,
        val streakDays: Int = 0,
        val todayReviewCount: Int = 0,
        val isDarkMode: Boolean = false,
        val isPremium: Boolean = false,
    ) : MviState

    sealed interface UiEvent : MviEvent {
        data object WordListOpened       : UiEvent
        data object WordListClosed       : UiEvent
        data object PremiumModalOpened   : UiEvent
        data object PremiumModalClosed   : UiEvent
        data class  BookmarkRemoved(val wordId: Long) : UiEvent
        data object CardFlipped          : UiEvent
        data object NextTapped           : UiEvent
        data object DarkModeToggled      : UiEvent
        data object RateTapped           : UiEvent
        data object PurchaseCompleted    : UiEvent
        data object PurchaseTapped       : UiEvent
        data object RestorePurchaseTapped: UiEvent
    }

    sealed interface UiEffect : MviEffect {
        data object LaunchPlayStore    : UiEffect
        data object LaunchPurchaseFlow : UiEffect
        data object ShowRestoreSuccess : UiEffect
        data object ShowRestoreFailure : UiEffect
    }
}
