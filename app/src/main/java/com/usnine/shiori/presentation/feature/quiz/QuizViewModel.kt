package com.usnine.shiori.presentation.feature.quiz

import androidx.lifecycle.viewModelScope
import com.usnine.shiori.data.local.KanaData
import com.usnine.shiori.data.local.KanaItem
import com.usnine.shiori.presentation.base.MviViewModel
import com.usnine.shiori.presentation.feature.kana.KanaTab
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QuizViewModel : MviViewModel<QuizContract.UiEvent, QuizContract.UiState, QuizContract.UiEffect>(
    initialState = QuizContract.UiState()
) {
    private data class QuizQuestion(
        val item: KanaItem,
        val rowLabel: String,
        val choices: List<String>,
    )

    private var questions: List<QuizQuestion> = emptyList()

    override fun handleEvent(event: QuizContract.UiEvent) {
        when (event) {
            is QuizContract.UiEvent.LoadQuiz       -> loadQuiz(event.tab, event.selectedKana)
            is QuizContract.UiEvent.AnswerSelected -> onAnswerSelected(event.answer)
            QuizContract.UiEvent.NextQuestion      -> onNextQuestion()
            QuizContract.UiEvent.AdDismissed       -> onAdDismissed()
        }
    }

    private fun loadQuiz(tab: KanaTab, selectedKana: Set<String>) {
        val rows = when (tab) {
            KanaTab.HIRAGANA -> KanaData.hiraganaRows
            KanaTab.KATAKANA -> KanaData.katakanaRows
            KanaTab.DAKUTEN  -> KanaData.dakutenRows
            KanaTab.ALL      -> KanaData.hiraganaRows + KanaData.katakanaRows + KanaData.dakutenRows
        }

        val allTagged = rows.flatMap { row ->
            row.items.filterNotNull().map { item -> item to row.label }
        }
        // 오답 보기는 전체 풀에서 선택해 다양성 확보
        val choicePool = allTagged
        val allReadings = choicePool.map { it.first.koreanReading }.distinct()

        val quizPool = if (selectedKana.isEmpty()) allTagged
                       else allTagged.filter { (item, _) -> item.kana in selectedKana }

        questions = quizPool.shuffled().map { (item, rowLabel) ->
            val wrongChoices = choicePool
                .filter { (other, otherRow) -> otherRow != rowLabel && other.koreanReading != item.koreanReading }
                .map { it.first.koreanReading }
                .distinct()
                .shuffled()
                .take(3)
                .let { picked ->
                    if (picked.size < 3) {
                        val fallback = allReadings.filter { it != item.koreanReading }.shuffled()
                        (picked + fallback).distinct().take(3)
                    } else picked
                }

            QuizQuestion(
                item = item,
                rowLabel = rowLabel,
                choices = (wrongChoices + item.koreanReading).shuffled(),
            )
        }

        if (questions.isNotEmpty()) {
            val first = questions[0]
            setState {
                copy(
                    currentIndex = 0,
                    total = questions.size,
                    currentKana = first.item,
                    choices = first.choices,
                    selectedAnswer = null,
                    isCorrect = null,
                    isFinished = false,
                    correctCount = 0,
                )
            }
        }
    }

    private fun onAnswerSelected(answer: String) {
        if (state.value.selectedAnswer != null) return

        val isCorrect = answer == state.value.currentKana.koreanReading
        setState {
            copy(
                selectedAnswer = answer,
                isCorrect = isCorrect,
                correctCount = if (isCorrect) correctCount + 1 else correctCount,
            )
        }

        viewModelScope.launch {
            delay(900)
            onEvent(QuizContract.UiEvent.NextQuestion)
        }
    }

    private fun onNextQuestion() {
        val nextIndex = state.value.currentIndex + 1
        if (nextIndex >= questions.size) {
            setState { copy(currentIndex = nextIndex) }
            sendEffect(QuizContract.UiEffect.ShowInterstitialAd)
        } else {
            val next = questions[nextIndex]
            setState {
                copy(
                    currentIndex   = nextIndex,
                    currentKana    = next.item,
                    choices        = next.choices,
                    selectedAnswer = null,
                    isCorrect      = null,
                )
            }
        }
    }

    private fun onAdDismissed() {
        setState { copy(isFinished = true) }
        sendEffect(QuizContract.UiEffect.ShowResult)
    }
}
