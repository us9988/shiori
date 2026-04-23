package com.usnine.shiori.presentation.feature.quiz

import com.usnine.shiori.data.local.KanaItem
import com.usnine.shiori.presentation.base.MviEffect
import com.usnine.shiori.presentation.base.MviEvent
import com.usnine.shiori.presentation.base.MviState
import com.usnine.shiori.presentation.feature.kana.KanaTab

interface QuizContract {

    data class UiState(
        val currentIndex: Int = 0,
        val total: Int = 0,
        val currentKana: KanaItem = KanaItem("", ""),
        val choices: List<String> = emptyList(),
        val selectedAnswer: String? = null,
        val isCorrect: Boolean? = null,
        val isFinished: Boolean = false,
        val correctCount: Int = 0,
    ) : MviState

    sealed interface UiEvent : MviEvent {
        data class LoadQuiz(val tab: KanaTab) : UiEvent
        data class AnswerSelected(val answer: String) : UiEvent
        data object NextQuestion : UiEvent
    }

    sealed interface UiEffect : MviEffect {
        data object NavigateBack : UiEffect
        data object ShowResult : UiEffect
    }
}
