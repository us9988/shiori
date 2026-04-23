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
            is QuizContract.UiEvent.LoadQuiz        -> loadQuiz(event.tab)
            is QuizContract.UiEvent.AnswerSelected  -> onAnswerSelected(event.answer)
            is QuizContract.UiEvent.NextQuestion    -> onNextQuestion()
        }
    }

    private fun loadQuiz(tab: KanaTab) {
        val rows = when (tab) {
            KanaTab.HIRAGANA -> KanaData.hiraganaRows
            KanaTab.KATAKANA -> KanaData.katakanaRows
            KanaTab.DAKUTEN  -> KanaData.dakutenRows
        }

        // 전체 아이템 + 행 레이블 flatten
        val allTagged = rows.flatMap { row ->
            row.items.filterNotNull().map { item -> item to row.label }
        }

        // 모든 romaji 풀
        val allRomaji = allTagged.map { it.first.romaji }.distinct()

        questions = allTagged.shuffled().map { (item, rowLabel) ->
            // 오답: 같은 행 제외, 정답과 다른 romaji에서 3개 선택
            val wrongChoices = allTagged
                .filter { (other, otherRow) -> otherRow != rowLabel && other.romaji != item.romaji }
                .map { it.first.romaji }
                .distinct()
                .shuffled()
                .take(3)
                .let { picked ->
                    // 같은 행 외에 3개가 부족할 경우 전체 풀에서 보충
                    if (picked.size < 3) {
                        val fallback = allRomaji.filter { it != item.romaji }.shuffled()
                        (picked + fallback).distinct().take(3)
                    } else picked
                }

            QuizQuestion(
                item = item,
                rowLabel = rowLabel,
                choices = (wrongChoices + item.romaji).shuffled(),
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

        val isCorrect = answer == state.value.currentKana.romaji
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
            setState { copy(isFinished = true) }
            sendEffect(QuizContract.UiEffect.ShowResult)
        } else {
            val next = questions[nextIndex]
            setState {
                copy(
                    currentIndex = nextIndex,
                    currentKana = next.item,
                    choices = next.choices,
                    selectedAnswer = null,
                    isCorrect = null,
                )
            }
        }
    }
}
