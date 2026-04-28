package com.usnine.shiori.presentation.feature.sentence

import com.usnine.shiori.presentation.base.MviEffect
import com.usnine.shiori.presentation.base.MviEvent
import com.usnine.shiori.presentation.base.MviState

enum class SentenceTab(val label: String) {
    WORD("단어"),
    CONVERSATION("일상회화"),
}

interface SentenceContract {

    data class UiState(
        val selectedTab: SentenceTab = SentenceTab.WORD,
    ) : MviState

    sealed interface UiEvent : MviEvent {
        data class TabChanged(val tab: SentenceTab) : UiEvent
    }

    sealed interface UiEffect : MviEffect
}
