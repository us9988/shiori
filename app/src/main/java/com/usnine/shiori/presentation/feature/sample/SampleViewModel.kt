package com.usnine.shiori.presentation.feature.sample

import com.usnine.shiori.presentation.base.MviViewModel

class SampleViewModel : MviViewModel<SampleContract.UiEvent, SampleContract.UiState, SampleContract.UiEffect>(
    initialState = SampleContract.UiState()
) {
    override fun handleEvent(event: SampleContract.UiEvent) {
        when (event) {
            is SampleContract.UiEvent.OnButtonClick -> onButtonClick()
            is SampleContract.UiEvent.OnTextChange -> setState { copy(text = event.text) }
        }
    }

    private fun onButtonClick() {
        val next = state.value.count + 1
        setState { copy(count = next) }
        sendEffect(SampleContract.UiEffect.ShowToast("Clicked $next times"))
    }
}
