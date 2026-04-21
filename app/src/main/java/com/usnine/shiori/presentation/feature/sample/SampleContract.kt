package com.usnine.shiori.presentation.feature.sample

import com.usnine.shiori.presentation.base.MviEffect
import com.usnine.shiori.presentation.base.MviEvent
import com.usnine.shiori.presentation.base.MviState

interface SampleContract {

    sealed interface UiEvent : MviEvent {
        data object OnButtonClick : UiEvent
        data class OnTextChange(val text: String) : UiEvent
    }

    data class UiState(
        val text: String = "",
        val count: Int = 0,
        val isLoading: Boolean = false,
    ) : MviState

    sealed interface UiEffect : MviEffect {
        data class ShowToast(val message: String) : UiEffect
    }
}
