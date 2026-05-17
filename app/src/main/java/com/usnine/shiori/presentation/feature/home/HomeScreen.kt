package com.usnine.shiori.presentation.feature.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.usnine.shiori.R
import com.usnine.shiori.data.local.entity.PartOfSpeech
import com.usnine.shiori.ui.components.DuckImage
import com.usnine.shiori.ui.components.DuckMood
import com.usnine.shiori.presentation.analytics.AnalyticsManager
import com.usnine.shiori.presentation.analytics.CrashlyticsManager
import com.usnine.shiori.ui.theme.NotoSerifJpFamily
import java.time.LocalDate

@Composable
fun HomeScreen(
    onNavigateToWordStudy: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        AnalyticsManager.logScreenView(AnalyticsManager.SCREEN_HOME)
        CrashlyticsManager.setCurrentScreen(AnalyticsManager.SCREEN_HOME)
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                HomeContract.UiEffect.NavigateToWordStudy -> onNavigateToWordStudy()
            }
        }
    }

    val weekDots = remember(state.streak) {
        val todayIndex = LocalDate.now().dayOfWeek.value - 1
        (0..6).map { dayIndex ->
            when {
                dayIndex > todayIndex  -> WeekDotState.EMPTY
                dayIndex == todayIndex -> WeekDotState.TODAY
                else -> {
                    val daysAgo = todayIndex - dayIndex
                    if (daysAgo < state.streak) WeekDotState.DONE else WeekDotState.EMPTY
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        HomeHeader()
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Column(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 10.dp),
                ) {
                    SectionTitle(stringResource(R.string.home_weekly_streak))
                    Spacer(modifier = Modifier.height(8.dp))
                    WeeklyStreak(dots = weekDots)
                    Spacer(modifier = Modifier.height(14.dp))
                    DuckSection(streak = state.streak)
                    Spacer(modifier = Modifier.height(14.dp))
                    SectionTitle(stringResource(R.string.home_today_study))
                    Spacer(modifier = Modifier.height(8.dp))
                    TodayMissionCard(
                        mission = state.todayMission,
                        onClick = { viewModel.onEvent(HomeContract.UiEvent.MissionTapped) },
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    SectionTitle(stringResource(R.string.home_today_word))
                    Spacer(modifier = Modifier.height(8.dp))
                    WordOfDayCard(
                        word       = state.wordOfDay,
                        onBookmark = { viewModel.onEvent(HomeContract.UiEvent.WordOfDayBookmarkToggled) },
                        onNext     = { viewModel.onEvent(HomeContract.UiEvent.NextWordOfDay) },
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    SectionTitle(stringResource(R.string.home_today_phrase))
                    Spacer(modifier = Modifier.height(8.dp))
                    PhraseOfDayCard(
                        phrase = state.phraseOfDay,
                        onNext = { viewModel.onEvent(HomeContract.UiEvent.NextPhraseOfDay) },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// ── 오늘의 학습 ───────────────────────────────────────────────────────────────

@Composable
private fun TodayMissionCard(
    mission: TodayMission?,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick,
            )
            .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 14.dp),
    ) {
        // 배경 워터마크
        Text(
            text       = "栞",
            fontFamily = NotoSerifJpFamily,
            fontSize   = 48.sp,
            color      = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.07f),
            modifier   = Modifier.align(Alignment.CenterEnd),
        )

        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(
                text      = stringResource(R.string.home_today_mission),
                fontSize  = 10.sp,
                color     = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                letterSpacing = 0.04.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text       = mission?.label ?: "…",
                fontSize   = 14.sp,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.onPrimary,
            )
        }

        // 화살표
        Text(
            text     = "→",
            fontSize = 14.sp,
            color    = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
}

// ── 헤더 ──────────────────────────────────────────────────────────────────────

@Composable
private fun HomeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 14.dp),
    ) {
        Text(
            text       = stringResource(R.string.home_app_title),
            fontSize   = 20.sp,
            fontWeight = FontWeight.Medium,
            color      = MaterialTheme.colorScheme.primary,
        )
    }
}

// ── 주간 스트릭 ───────────────────────────────────────────────────────────────

private enum class WeekDotState { EMPTY, DONE, TODAY }

private val weekLabels = listOf("월", "화", "수", "목", "금", "토", "일")

@Composable
private fun WeeklyStreak(dots: List<WeekDotState>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        dots.forEachIndexed { index, dotState ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text     = weekLabels[index],
                    fontSize = 9.sp,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(
                            when (dotState) {
                                WeekDotState.DONE,
                                WeekDotState.TODAY -> MaterialTheme.colorScheme.primary
                                WeekDotState.EMPTY -> MaterialTheme.colorScheme.surface
                            }
                        )
                        .border(
                            0.5.dp,
                            when (dotState) {
                                WeekDotState.DONE,
                                WeekDotState.TODAY -> MaterialTheme.colorScheme.primary
                                WeekDotState.EMPTY -> MaterialTheme.colorScheme.outline
                            },
                            CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (dotState == WeekDotState.TODAY) {
                        Text(
                            text       = "✓",
                            fontSize   = 9.sp,
                            lineHeight = 9.sp,
                            color      = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }
        }
    }
}


// ── 오늘의 단어 ───────────────────────────────────────────────────────────────

@Composable
private fun WordOfDayCard(
    word: WordOfDayUi?,
    onBookmark: () -> Unit,
    onNext: () -> Unit,
) {
    var rotationTick by remember { mutableIntStateOf(0) }
    val iconRotation by animateFloatAsState(
        targetValue   = rotationTick * 360f,
        animationSpec = tween(450, easing = FastOutSlowInEasing),
        label         = "word_refresh_rotation",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                        )
                    )
                ),
        )

        if (word == null) {
            Box(
                modifier         = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text     = "단어를 불러오는 중…",
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            AnimatedContent(
                targetState  = word,
                contentKey   = { it.id },
                transitionSpec = {
                    (slideInHorizontally { it / 4 } + fadeIn(tween(250))).togetherWith(
                        slideOutHorizontally { -it / 4 } + fadeOut(tween(200))
                    ).using(SizeTransform(clip = false))
                },
                label = "word_content",
            ) { animWord ->
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 16.dp),
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                .border(
                                    0.5.dp,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    RoundedCornerShape(99.dp),
                                )
                                .padding(start = 7.dp, end = 7.dp, top = 2.dp, bottom = 2.dp),
                        ) {
                            Text(
                                text       = animWord.level.name,
                                fontSize   = 9.sp,
                                fontWeight = FontWeight.Medium,
                                color      = MaterialTheme.colorScheme.secondary,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f))
                                .border(0.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f), RoundedCornerShape(99.dp))
                                .padding(start = 7.dp, end = 7.dp, top = 2.dp, bottom = 2.dp),
                        ) {
                            Text(
                                text       = animWord.partOfSpeech.displayLabel(),
                                fontSize   = 9.sp,
                                fontWeight = FontWeight.Medium,
                                color      = MaterialTheme.colorScheme.secondary,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text       = animWord.japanese,
                        fontFamily = NotoSerifJpFamily,
                        fontSize   = 32.sp,
                        fontWeight = FontWeight.Light,
                        color      = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text     = if (animWord.koreanReading.isNotBlank())
                                       "${animWord.reading} · ${animWord.koreanReading}"
                                   else
                                       animWord.reading,
                        fontSize = 12.sp,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier          = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Text(
                            text       = animWord.meaning,
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color      = MaterialTheme.colorScheme.primary,
                            modifier   = Modifier.weight(1f),
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication        = null,
                                    onClick           = onBookmark,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector        = if (animWord.isBookmarked) Icons.Filled.Bookmark
                                                     else Icons.Outlined.Bookmark,
                                contentDescription = null,
                                tint               = if (animWord.isBookmarked) MaterialTheme.colorScheme.primary
                                                     else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                modifier           = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 6.dp)
                    .size(32.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null,
                        onClick           = {
                            rotationTick++
                            onNext()
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Outlined.Refresh,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                    modifier           = Modifier.size(17.dp).rotate(iconRotation),
                )
            }
        }
    }
}

// ── 오늘의 회화 ───────────────────────────────────────────────────────────────

@Composable
private fun PhraseOfDayCard(
    phrase: PhraseOfDayUi?,
    onNext: () -> Unit,
) {
    var rotationTick by remember { mutableIntStateOf(0) }
    val iconRotation by animateFloatAsState(
        targetValue   = rotationTick * 360f,
        animationSpec = tween(450, easing = FastOutSlowInEasing),
        label         = "phrase_refresh_rotation",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                        )
                    )
                ),
        )

        if (phrase == null) {
            Box(
                modifier         = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text     = "회화를 불러오는 중…",
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            AnimatedContent(
                targetState  = phrase,
                contentKey   = { it.japanese },
                transitionSpec = {
                    (slideInHorizontally { it / 4 } + fadeIn(tween(250))).togetherWith(
                        slideOutHorizontally { -it / 4 } + fadeOut(tween(200))
                    ).using(SizeTransform(clip = false))
                },
                label = "phrase_content",
            ) { animPhrase ->
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 14.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f))
                            .border(
                                0.5.dp,
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                                RoundedCornerShape(99.dp),
                            )
                            .padding(start = 7.dp, end = 7.dp, top = 2.dp, bottom = 2.dp),
                    ) {
                        Text(
                            text       = animPhrase.category.label,
                            fontSize   = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color      = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text       = animPhrase.japanese,
                        fontFamily = NotoSerifJpFamily,
                        fontSize   = 14.sp,
                        color      = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 24.sp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text     = animPhrase.hiragana,
                        fontSize = 10.sp,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text       = animPhrase.korean,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color      = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector        = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint               = MaterialTheme.colorScheme.onPrimary,
                                modifier           = Modifier.size(16.dp),
                            )
                        }
                        Text(
                            text     = stringResource(R.string.home_phrase_play_label),
                            fontSize = 10.sp,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 6.dp)
                    .size(32.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null,
                        onClick           = {
                            rotationTick++
                            onNext()
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Outlined.Refresh,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.45f),
                    modifier           = Modifier.size(17.dp).rotate(iconRotation),
                )
            }
        }
    }
}

// ── 오리 메시지 섹션 ──────────────────────────────────────────────────────────

@Composable
private fun DuckSection(streak: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f))
            .border(
                0.5.dp,
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f),
                RoundedCornerShape(12.dp),
            )
            .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DuckImage(
            mood     = DuckMood.DEFAULT,
            modifier = Modifier.size(52.dp),
        )
        Column {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)) {
                        append(stringResource(R.string.home_duck_streak, streak))
                    }
                },
                fontSize   = 13.sp,
                color      = MaterialTheme.colorScheme.onBackground,
                lineHeight = 20.sp,
            )
            Text(
                text       = stringResource(R.string.home_duck_cheer),
                fontSize   = 12.sp,
                color      = MaterialTheme.colorScheme.onBackground,
                lineHeight = 20.sp,
            )
        }
    }
}

// ── 공통 ─────────────────────────────────────────────────────────────────────

@Composable
private fun SectionTitle(text: String) {
    Text(
        text          = text.uppercase(),
        fontSize      = 11.sp,
        fontWeight    = FontWeight.Medium,
        color         = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 0.06.sp,
    )
}

@Composable
private fun PartOfSpeech.displayLabel(): String = when (this) {
    PartOfSpeech.NOUN         -> "명사"
    PartOfSpeech.VERB         -> "동사"
    PartOfSpeech.I_ADJECTIVE  -> "い형용사"
    PartOfSpeech.NA_ADJECTIVE -> "な형용사"
    PartOfSpeech.ADVERB       -> "부사"
    PartOfSpeech.CONJUNCTION  -> "접속사"
    PartOfSpeech.PARTICLE     -> "조사"
    PartOfSpeech.OTHER        -> "기타"
}
