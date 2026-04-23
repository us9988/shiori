package com.usnine.shiori.presentation.feature.sentence.conversation

import com.usnine.shiori.data.local.entity.PhraseCategory
import com.usnine.shiori.presentation.base.MviEffect
import com.usnine.shiori.presentation.base.MviEvent
import com.usnine.shiori.presentation.base.MviState

enum class ConversationFilter(val label: String, val category: PhraseCategory?) {
    ALL("전체",   null),
    GREETING("인사",    PhraseCategory.GREETING),
    CAFE("카페",        PhraseCategory.CAFE),
    SHOPPING("쇼핑",   PhraseCategory.SHOPPING),
    TRANSPORT("교통",  PhraseCategory.TRANSPORT),
    ACCOMMODATION("숙소", PhraseCategory.ACCOMMODATION),
}

data class PhraseUi(
    val id: Long,
    val japanese: String,
    val hiragana: String,
    val korean: String,
    val category: PhraseCategory,
    val level: String,
    val audioFileName: String,
)

interface ConversationContract {

    data class UiState(
        val selectedFilter: ConversationFilter = ConversationFilter.ALL,
        val displayedPhrases: List<PhraseUi> = emptyList(),
        val playingId: Long? = null,
    ) : MviState

    sealed interface UiEvent : MviEvent {
        data class FilterChanged(val filter: ConversationFilter) : UiEvent
        data class PlayTapped(val phrase: PhraseUi) : UiEvent
    }

    sealed interface UiEffect : MviEffect
}
