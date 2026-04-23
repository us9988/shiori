package com.usnine.shiori.presentation.feature.home

import com.usnine.shiori.presentation.base.MviEffect
import com.usnine.shiori.presentation.base.MviEvent
import com.usnine.shiori.presentation.base.MviState

data class BookmarkUi(
    val id: Long,
    val jpText: String,
    val krText: String,
    val source: String,
)

interface HomeContract {

    data class UiState(
        val streak: Int = 0,
        val todayMission: String = "",
        val bookmarks: List<BookmarkUi> = emptyList(),
    ) : MviState

    sealed interface UiEvent : MviEvent {
        data object LoadData : UiEvent
    }

    sealed interface UiEffect : MviEffect
}
