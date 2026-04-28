package com.usnine.shiori.presentation.feature.quiz

import android.app.Activity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.usnine.shiori.R
import com.usnine.shiori.presentation.analytics.AnalyticsManager
import com.usnine.shiori.presentation.analytics.CrashlyticsManager
import com.usnine.shiori.presentation.ad.AdMobEntryPoint
import dagger.hilt.android.EntryPointAccessors
import com.usnine.shiori.presentation.feature.kana.KanaTab
import com.usnine.shiori.ui.components.DuckImage
import com.usnine.shiori.ui.components.DuckMood
import com.usnine.shiori.ui.theme.NotoSerifJpFamily

@Composable
fun QuizScreen(
    tab: KanaTab,
    selectedKana: Set<String> = emptySet(),
    isPremium: Boolean = false,
    onNavigateBack: () -> Unit,
    viewModel: QuizViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(tab) {
        viewModel.onEvent(QuizContract.UiEvent.LoadQuiz(tab, selectedKana))
    }

    LaunchedEffect(Unit) {
        AnalyticsManager.logScreenView(AnalyticsManager.SCREEN_KANA_QUIZ)
        CrashlyticsManager.setCurrentScreen(AnalyticsManager.SCREEN_KANA_QUIZ)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                QuizContract.UiEffect.NavigateBack       -> onNavigateBack()
                QuizContract.UiEffect.ShowResult         -> Unit
                QuizContract.UiEffect.ShowInterstitialAd -> {
                    val adMobManager = EntryPointAccessors
                        .fromApplication(context.applicationContext, AdMobEntryPoint::class.java)
                        .adMobManager()
                    val activity = context as? Activity
                    if (activity != null) {
                        adMobManager.showInterstitialAd(activity, isPremium) {
                            viewModel.onEvent(QuizContract.UiEvent.AdDismissed)
                        }
                    } else {
                        viewModel.onEvent(QuizContract.UiEvent.AdDismissed)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        QuizProgressRow(
            currentIndex = state.currentIndex,
            total = state.total,
            onBack = onNavigateBack,
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (state.isFinished) {
            ResultCard(
                correctCount = state.correctCount,
                total = state.total,
                onBack = onNavigateBack,
            )
        } else if (state.total > 0) {
            val duckMood = when (state.isCorrect) {
                true  -> DuckMood.CORRECT
                false -> DuckMood.WRONG
                null  -> DuckMood.DEFAULT
            }
            val duckAlpha by animateFloatAsState(
                targetValue   = if (state.isCorrect != null) 1f else 0.5f,
                animationSpec = tween(durationMillis = 300),
                label         = "duckAlpha",
            )
            DuckImage(
                mood     = duckMood,
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.CenterHorizontally)
                    .graphicsLayer { alpha = duckAlpha },
            )

            Spacer(modifier = Modifier.height(12.dp))

            KanaQuizCard(kana = state.currentKana.kana)

            Spacer(modifier = Modifier.height(20.dp))

            ChoiceGrid(
                choices = state.choices,
                correctAnswer = state.currentKana.koreanReading,
                selectedAnswer = state.selectedAnswer,
                onChoiceClick = { viewModel.onEvent(QuizContract.UiEvent.AnswerSelected(it)) },
            )

            Spacer(modifier = Modifier.height(16.dp))

            val feedback = when (state.isCorrect) {
                true  -> stringResource(R.string.quiz_correct, state.currentKana.kana, state.currentKana.koreanReading)
                false -> stringResource(R.string.quiz_wrong, state.currentKana.kana, state.currentKana.koreanReading)
                null  -> ""
            }
            val feedbackColor = when (state.isCorrect) {
                true  -> MaterialTheme.colorScheme.tertiary
                false -> MaterialTheme.colorScheme.error
                null  -> MaterialTheme.colorScheme.onBackground
            }
            Text(
                text = feedback,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = feedbackColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun QuizProgressRow(
    currentIndex: Int,
    total: Int,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(R.string.quiz_back),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onBack,
            ),
        )
        LinearProgressIndicator(
            progress = { if (total > 0) currentIndex.toFloat() / total else 0f },
            modifier = Modifier
                .weight(1f)
                .height(3.dp)
                .clip(RoundedCornerShape(99.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surface,
        )
        Text(
            text = if (total > 0) "${(currentIndex + 1).coerceAtMost(total)} / $total" else "",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun KanaQuizCard(kana: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            .padding(vertical = 40.dp, horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = kana,
                fontFamily = NotoSerifJpFamily,
                fontSize = 72.sp,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 80.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.quiz_prompt),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ChoiceGrid(
    choices: List<String>,
    correctAnswer: String,
    selectedAnswer: String?,
    onChoiceClick: (String) -> Unit,
) {
    val padded = (choices + List(4 - choices.size.coerceAtMost(4)) { "" }).take(4)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(padded.take(2), padded.drop(2)).forEach { rowChoices ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowChoices.forEach { choice ->
                    ChoiceButton(
                        text = choice,
                        correctAnswer = correctAnswer,
                        selectedAnswer = selectedAnswer,
                        onClick = { if (choice.isNotEmpty()) onChoiceClick(choice) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ChoiceButton(
    text: String,
    correctAnswer: String,
    selectedAnswer: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isAnswered = selectedAnswer != null
    val isThisCorrect = text == correctAnswer
    val isThisSelected = text == selectedAnswer

    val bgColor = when {
        !isAnswered    -> MaterialTheme.colorScheme.surface
        isThisCorrect  -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f)
        isThisSelected -> MaterialTheme.colorScheme.error.copy(alpha = 0.10f)
        else           -> MaterialTheme.colorScheme.surface
    }
    val borderColor = when {
        !isAnswered    -> MaterialTheme.colorScheme.outline
        isThisCorrect  -> MaterialTheme.colorScheme.tertiary
        isThisSelected -> MaterialTheme.colorScheme.error
        else           -> MaterialTheme.colorScheme.outline
    }
    val textColor = when {
        !isAnswered    -> MaterialTheme.colorScheme.onSurface
        isThisCorrect  -> MaterialTheme.colorScheme.tertiary
        isThisSelected -> MaterialTheme.colorScheme.error
        else           -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(0.5.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = !isAnswered,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
        )
    }
}

@Composable
private fun ResultCard(
    correctCount: Int,
    total: Int,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            .padding(vertical = 40.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DuckImage(
            mood     = DuckMood.COMPLETE,
            modifier = Modifier.size(110.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.quiz_result_score, correctCount, total),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (correctCount == total) stringResource(R.string.quiz_result_perfect)
                   else stringResource(R.string.quiz_result_retry),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(28.dp))
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text(
                text = stringResource(R.string.quiz_back_button),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
