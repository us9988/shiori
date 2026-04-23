package com.usnine.shiori.presentation.feature.kana

import com.usnine.shiori.data.local.KanaRow
import com.usnine.shiori.presentation.base.MviEffect
import com.usnine.shiori.presentation.base.MviEvent
import com.usnine.shiori.presentation.base.MviState

enum class KanaTab(val label: String) {
    HIRAGANA("히라가나"),
    KATAKANA("가타카나"),
    DAKUTEN("탁음"),
}

interface KanaContract {

    data class UiState(
        val selectedTab: KanaTab = KanaTab.HIRAGANA,
        val rows: List<KanaRow> = emptyList(),
        val learnedSet: Set<String> = emptySet(),
    ) : MviState

    sealed interface UiEvent : MviEvent {
        data class TabChanged(val tab: KanaTab) : UiEvent
        data class KanaTapped(val kana: String) : UiEvent
    }

    sealed interface UiEffect : MviEffect
}
