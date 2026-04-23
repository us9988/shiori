package com.usnine.shiori.presentation.feature.sentence

import com.usnine.shiori.presentation.base.MviViewModel

class SentenceViewModel : MviViewModel<SentenceContract.UiEvent, SentenceContract.UiState, SentenceContract.UiEffect>(
    initialState = SentenceContract.UiState()
) {
    override fun handleEvent(event: SentenceContract.UiEvent) {
        when (event) {
            is SentenceContract.UiEvent.TabChanged -> setState { copy(selectedTab = event.tab) }
        }
    }
}
