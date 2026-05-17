package com.usnine.shiori.presentation.feature.sentence.word

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.usnine.shiori.R
import com.usnine.shiori.data.local.entity.PartOfSpeech
import com.usnine.shiori.data.local.entity.WordLevel
import com.usnine.shiori.presentation.analytics.AnalyticsManager
import com.usnine.shiori.presentation.analytics.CrashlyticsManager
import com.usnine.shiori.domain.StepUtils
import com.usnine.shiori.presentation.feature.wordstudy.StudyLevelInfo
import com.usnine.shiori.presentation.feature.wordstudy.WordStudyContract
import com.usnine.shiori.presentation.feature.wordstudy.WordStudyViewModel
import com.usnine.shiori.ui.theme.NotoSerifJpFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val posFilterOptions: List<PartOfSpeech?> = listOf(
    null,
    PartOfSpeech.NOUN,
    PartOfSpeech.VERB,
    PartOfSpeech.I_ADJECTIVE,
    PartOfSpeech.NA_ADJECTIVE,
    PartOfSpeech.ADVERB,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordScreen(
    onStartQuiz: () -> Unit = {},
    onStepTapped: (WordLevel, Int, Boolean) -> Unit = { _, _, _ -> },
    viewModel: WordViewModel = hiltViewModel(),
    studyViewModel: WordStudyViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val studyState by studyViewModel.state.collectAsStateWithLifecycle()
    val detailWord = state.words.find { it.id == state.detailWordId }
    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val showScrollTop by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }

    // 화면 복귀 시 학습 진도 갱신
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                studyViewModel.onEvent(WordStudyContract.UiEvent.RefreshLevelInfo)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        AnalyticsManager.logScreenView(AnalyticsManager.SCREEN_WORD)
        CrashlyticsManager.setCurrentScreen(AnalyticsManager.SCREEN_WORD)
    }

    LaunchedEffect(state.isSearchActive) {
        if (state.isSearchActive) {
            delay(100)
            focusRequester.requestFocus()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            WordHeader(
                isListMode = state.isListMode,
                isTooltipVisible = state.isTooltipVisible,
                selectedLevel = state.selectedLevel,
                studySelectedLevel = studyState.selectedLevel,
                studyLevelInfo = studyState.levelInfoList.find { it.level == studyState.selectedLevel },
                sortType = state.sortType,
                selectedPos = state.selectedPos,
                isSearchActive = state.isSearchActive,
                searchQuery = state.searchQuery,
                focusRequester = focusRequester,
                onModeToggle = { viewModel.onEvent(WordContract.UiEvent.ModeToggled) },
                onTooltipDismiss = { viewModel.onEvent(WordContract.UiEvent.TooltipDismissed) },
                onStudyLevelSelect = { studyViewModel.onEvent(WordStudyContract.UiEvent.LevelSelected(it)) },
                onSortChange = { viewModel.onEvent(WordContract.UiEvent.SortTypeChanged(it)) },
                onLevelSelect = { viewModel.onEvent(WordContract.UiEvent.LevelSelected(it)) },
                onPosSelect = { viewModel.onEvent(WordContract.UiEvent.PosSelected(it)) },
                onSearchToggle = { viewModel.onEvent(WordContract.UiEvent.SearchToggled) },
                onSearchQueryChange = { viewModel.onEvent(WordContract.UiEvent.SearchQueryChanged(it)) },
            )
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline)

            if (state.isListMode) {
                // ── 목록 모드 ─────────────────────────────────────────────────
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 0.dp),
                ) {
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        StatsBar(words = state.words)
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                    if (state.words.isEmpty()) {
                        item {
                            EmptyState(
                                message = if (state.isSearchActive && state.searchQuery.isNotEmpty())
                                    stringResource(R.string.word_search_no_result)
                                else
                                    stringResource(R.string.word_empty)
                            )
                        }
                    } else {
                        items(state.words, key = { it.id }) { word ->
                            WordCard(
                                word = word,
                                onTap = { viewModel.onEvent(WordContract.UiEvent.WordTapped(word.id)) },
                                onBookmark = { viewModel.onEvent(WordContract.UiEvent.BookmarkToggled(word.id)) },
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
                WordQuizStartButton(onClick = onStartQuiz)
            } else {
                // ── 학습 모드 ─────────────────────────────────────────────────
                val info = studyState.levelInfoList.find { it.level == studyState.selectedLevel }
                if (info != null) {
                    val allSteps = remember(info.totalCount) {
                        StepUtils.allSteps(info.totalCount)
                    }
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 0.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(top = 6.dp, bottom = 16.dp),
                    ) {
                        items(allSteps) { step ->
                            StudyStepCard(
                                step           = step,
                                completedCount  = info.completedCount,
                                unlockedStep   = info.unlockedStep,
                                reviewCount    = info.reviewCounts[step] ?: 0,
                                cycleProgress  = info.currentCycleProgress[step] ?: 0,
                                onTap          = { onStepTapped(studyState.selectedLevel, step, false) },
                                onReviewTap    = { onStepTapped(studyState.selectedLevel, step, true) },
                            )
                        }
                    }
                }
            }
        }

        // 가이드 FAB (학습 모드만)
        if (!state.isListMode) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 20.dp),
            ) {
                GuideButton(onClick = { viewModel.onEvent(WordContract.UiEvent.GuideTapped) })
            }
        }

        // FAB (목록 모드만)
        if (state.isListMode) {
            AnimatedVisibility(
                visible = showScrollTop,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 80.dp),
            ) {
                FloatingActionButton(
                    onClick = { scope.launch { listState.animateScrollToItem(0) } },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(44.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowUp,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }
    }

    // BottomSheet (목록 모드만)
    if (detailWord != null && state.isListMode) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onEvent(WordContract.UiEvent.DetailDismissed) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            WordDetailSheet(
                word = detailWord,
                onBookmark = { viewModel.onEvent(WordContract.UiEvent.BookmarkToggled(detailWord.id)) },
                onClose = { viewModel.onEvent(WordContract.UiEvent.DetailDismissed) },
            )
        }
    }

    if (state.isGuideVisible) {
        WordGuideSheet(onDismiss = { viewModel.onEvent(WordContract.UiEvent.GuideDismissed) })
    }
}

// ── 헤더 ──────────────────────────────────────────────────────────────────────

@Composable
private fun WordHeader(
    isListMode: Boolean,
    isTooltipVisible: Boolean,
    selectedLevel: WordLevel?,
    studySelectedLevel: WordLevel,
    studyLevelInfo: StudyLevelInfo?,
    sortType: WordSortType,
    selectedPos: PartOfSpeech?,
    isSearchActive: Boolean,
    searchQuery: String,
    focusRequester: FocusRequester,
    onModeToggle: () -> Unit,
    onTooltipDismiss: () -> Unit,
    onStudyLevelSelect: (WordLevel) -> Unit,
    onSortChange: (WordSortType) -> Unit,
    onLevelSelect: (WordLevel?) -> Unit,
    onPosSelect: (PartOfSpeech?) -> Unit,
    onSearchToggle: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest),
        ) {
            if (isListMode && isSearchActive) {
                // 검색 모드: 검색 바만 표시
                Box(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 12.dp)) {
                    SearchBar(
                        query = searchQuery,
                        focusRequester = focusRequester,
                        onQueryChange = onSearchQueryChange,
                        onClose = onSearchToggle,
                    )
                }
            } else if (!isListMode) {
                // ── 학습 모드: 레벨 탭 + 모드 토글 ──────────────────────────────
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier              = Modifier
                            .weight(1f)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        WordLevel.entries.forEach { level ->
                            LevelPill(
                                level    = level,
                                isActive = level == studySelectedLevel,
                                onClick  = { onStudyLevelSelect(level) },
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    ModeToggleButton(isListMode = false, onClick = onModeToggle)
                }
                // 학습 모드: 진도 바
                studyLevelInfo?.let { info ->
                    val fraction = if (info.totalCount > 0)
                        info.completedCount.toFloat() / info.totalCount else 0f
                    Row(
                        modifier              = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text     = "${info.completedCount} / ${info.totalCount}",
                            fontSize = 11.sp,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        LinearProgressIndicator(
                            progress   = { fraction },
                            modifier   = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .clip(RoundedCornerShape(99.dp)),
                            color      = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surface,
                        )
                        Text(
                            text     = "${(fraction * 100).toInt()}%",
                            fontSize = 11.sp,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } ?: Spacer(modifier = Modifier.height(10.dp))
            } else {
                // ── 목록 모드: 수준별/품사별 탭 + 모드 토글 ─────────────────────
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SortTypeToggle(
                        selected = sortType,
                        onChange = onSortChange,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ModeToggleButton(isListMode = true, onClick = onModeToggle)
                }
                Spacer(modifier = Modifier.height(8.dp))
                // 목록 모드 2행: 레벨 필 (수준별) 또는 품사 칩 (품사별) + 검색 아이콘
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (sortType == WordSortType.LEVEL) {
                        Row(
                            modifier              = Modifier
                                .weight(1f)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            // 전체 탭
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(
                                        if (selectedLevel == null) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .border(
                                        0.5.dp,
                                        if (selectedLevel == null) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outline,
                                        RoundedCornerShape(99.dp),
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication        = null,
                                        onClick           = { onLevelSelect(null) },
                                    )
                                    .padding(start = 14.dp, end = 14.dp, top = 5.dp, bottom = 5.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text       = stringResource(R.string.word_pos_all),
                                    fontSize   = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color      = if (selectedLevel == null) MaterialTheme.colorScheme.onPrimary
                                                 else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            WordLevel.entries.forEach { level ->
                                LevelPill(
                                    level    = level,
                                    isActive = level == selectedLevel,
                                    onClick  = { onLevelSelect(level) },
                                )
                            }
                        }
                    } else {
                        PosChipRow(
                            selected = selectedPos,
                            onSelect = onPosSelect,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = null,
                                onClick           = onSearchToggle,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector        = Icons.Filled.Search,
                            contentDescription = stringResource(R.string.word_search_hint),
                            tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier           = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }

        // 툴팁 오버레이 (모드 버튼 아래)
        if (isTooltipVisible && !(isListMode && isSearchActive)) {
            ModeTooltip(
                onDismiss = onTooltipDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 20.dp, top = 52.dp),
            )
        }
    }
}

// ── 레벨 필 ───────────────────────────────────────────────────────────────────

@Composable
private fun LevelPill(level: WordLevel, isActive: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(
                if (isActive) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface
            )
            .border(
                0.5.dp,
                if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(99.dp),
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(start = 14.dp, end = 14.dp, top = 5.dp, bottom = 5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = level.name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (isActive) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ── 모드 토글 버튼 ────────────────────────────────────────────────────────────

@Composable
private fun ModeToggleButton(isListMode: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isListMode) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else MaterialTheme.colorScheme.surface
            )
            .border(
                0.5.dp,
                if (isListMode) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(10.dp),
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (isListMode) "📚" else "📖",
            fontSize = 16.sp,
        )
    }
}

// ── 툴팁 ──────────────────────────────────────────────────────────────────────

@Composable
private fun ModeTooltip(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.onBackground)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss,
            )
            .padding(start = 10.dp, end = 10.dp, top = 6.dp, bottom = 6.dp),
    ) {
        Text(
            text = stringResource(R.string.word_mode_tooltip),
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.background,
        )
    }
}

// ── 검색 바 ───────────────────────────────────────────────────────────────────

@Composable
private fun SearchBar(
    query: String,
    focusRequester: FocusRequester,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
            .padding(start = 12.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            singleLine = true,
            textStyle = TextStyle(fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (query.isEmpty()) {
                        Text(
                            text = stringResource(R.string.word_search_hint),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        )
                    }
                    innerTextField()
                }
            },
        )
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .size(28.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClose,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(R.string.word_detail_close),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

// ── 정렬 탭 ───────────────────────────────────────────────────────────────────

@Composable
private fun SortTypeToggle(
    selected: WordSortType,
    onChange: (WordSortType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 3.dp, end = 3.dp, top = 3.dp, bottom = 3.dp),
    ) {
        WordSortType.entries.forEach { type ->
            val isActive = type == selected
            val label = when (type) {
                WordSortType.LEVEL -> stringResource(R.string.word_sort_level)
                WordSortType.PART_OF_SPEECH -> stringResource(R.string.word_sort_pos)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (isActive)
                            Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        else Modifier.clip(RoundedCornerShape(6.dp))
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onChange(type) },
                    )
                    .padding(start = 0.dp, end = 0.dp, top = 5.dp, bottom = 5.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ── 품사 칩 ───────────────────────────────────────────────────────────────────

@Composable
private fun PosChipRow(
    selected: PartOfSpeech?,
    onSelect: (PartOfSpeech?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        posFilterOptions.forEach { pos ->
            val isActive = pos == selected
            val label = if (pos == null) stringResource(R.string.word_pos_all) else pos.displayLabel()
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(99.dp))
                    .background(if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                    .border(
                        0.5.dp,
                        if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(99.dp),
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onSelect(pos) },
                    )
                    .padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isActive) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ── 학습 모드: 단계 카드 ───────────────────────────────────────────────────────

private enum class SStepState { COMPLETED, IN_PROGRESS, NEW, LOCKED }

private fun stepStateFor(step: Int, completedCount: Int, unlockedStep: Int): SStepState {
    if (step > unlockedStep) return SStepState.LOCKED
    return when {
        completedCount >= step                       -> SStepState.COMPLETED
        completedCount > StepUtils.stepOffset(step)  -> SStepState.IN_PROGRESS
        else                                         -> SStepState.NEW
    }
}

@Composable
private fun StudyStepCard(
    step: Int,
    completedCount: Int,
    unlockedStep: Int,
    reviewCount: Int = 0,
    cycleProgress: Int = 0,
    onTap: () -> Unit,
    onReviewTap: () -> Unit = {},
) {
    val ss = stepStateFor(step, completedCount, unlockedStep)
    val locked = ss == SStepState.LOCKED

    val fraction = when (ss) {
        SStepState.COMPLETED -> cycleProgress.toFloat() / step
        SStepState.IN_PROGRESS -> completedCount.toFloat() / step
        else -> 0f
    }
    val borderColor = if (ss == SStepState.COMPLETED)
        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f)
    else MaterialTheme.colorScheme.outline

    val cardClickable = !locked && ss != SStepState.COMPLETED
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(0.5.dp, borderColor, RoundedCornerShape(12.dp))
            .then(
                if (cardClickable) Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onTap,
                ) else Modifier
            )
            .padding(start = 13.dp, end = 13.dp, top = 11.dp, bottom = 11.dp)
            .then(if (locked) Modifier.graphicsLayer { alpha = 0.4f } else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            val inlineLabel = when (ss) {
                SStepState.COMPLETED   -> stringResource(R.string.study_badge_completed)
                SStepState.IN_PROGRESS -> stringResource(R.string.study_badge_in_progress)
                SStepState.NEW         -> stringResource(R.string.study_badge_new)
                else                   -> null
            }
            val inlineColor = when (ss) {
                SStepState.COMPLETED   -> MaterialTheme.colorScheme.tertiary
                SStepState.IN_PROGRESS -> MaterialTheme.colorScheme.primary
                SStepState.NEW         -> MaterialTheme.colorScheme.secondary
                else                   -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            if (inlineLabel != null) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text       = stringResource(R.string.study_step_range, step),
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color      = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text       = inlineLabel,
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color      = inlineColor,
                    )
                }
            } else {
                Text(
                    text       = stringResource(R.string.study_step_range, step),
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color      = MaterialTheme.colorScheme.onBackground,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress   = { fraction },
                modifier   = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(99.dp)),
                color      = if (ss == SStepState.COMPLETED) MaterialTheme.colorScheme.tertiary
                             else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Spacer(modifier = Modifier.height(5.dp))
            if (ss == SStepState.COMPLETED) {
                androidx.compose.foundation.layout.Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Text(
                        text     = stringResource(R.string.study_step_completed_count, cycleProgress, step),
                        fontSize = 10.sp,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text       = stringResource(R.string.study_review_count, reviewCount + 1),
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color      = MaterialTheme.colorScheme.tertiary,
                    )
                }
            } else {
                Text(
                    text = when (ss) {
                        SStepState.COMPLETED   -> stringResource(R.string.study_step_completed_count, step, step)
                        SStepState.IN_PROGRESS -> stringResource(R.string.study_step_completed_count, completedCount, step)
                        SStepState.NEW         -> stringResource(R.string.study_step_new_count, step)
                        SStepState.LOCKED      -> stringResource(R.string.study_step_locked_hint)
                    },
                    fontSize = 10.sp,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            if (ss == SStepState.LOCKED) SStepBadge(ss)
            when (ss) {
                SStepState.COMPLETED   -> SStepButton(
                    if (cycleProgress > 0) stringResource(R.string.study_btn_resume)
                    else stringResource(R.string.study_btn_review),
                    if (cycleProgress > 0) SSBtnStyle.OUTLINE else SSBtnStyle.GREEN,
                    onReviewTap,
                )
                SStepState.IN_PROGRESS -> SStepButton(stringResource(R.string.study_btn_resume), SSBtnStyle.OUTLINE, onTap)
                SStepState.NEW         -> SStepButton(stringResource(R.string.study_btn_start), SSBtnStyle.FILLED, onTap)
                SStepState.LOCKED      -> Unit
            }
        }
    }
}

private enum class SSBtnStyle { FILLED, OUTLINE, GREEN }

@Composable
private fun SStepBadge(ss: SStepState) {
    val (label, bg, textColor, border) = when (ss) {
        SStepState.COMPLETED -> arrayOf(
            stringResource(R.string.study_badge_completed),
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
        )
        SStepState.IN_PROGRESS -> arrayOf(
            stringResource(R.string.study_badge_in_progress),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        )
        SStepState.NEW -> arrayOf(
            stringResource(R.string.study_badge_new),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
        )
        SStepState.LOCKED -> arrayOf(
            stringResource(R.string.study_badge_locked),
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            MaterialTheme.colorScheme.outline,
        )
    }
    @Suppress("UNCHECKED_CAST")
    val bg2 = bg as androidx.compose.ui.graphics.Color

    @Suppress("UNCHECKED_CAST")
    val tc = textColor as androidx.compose.ui.graphics.Color

    @Suppress("UNCHECKED_CAST")
    val bd = border as androidx.compose.ui.graphics.Color

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(bg2)
            .border(0.5.dp, bd, RoundedCornerShape(99.dp))
            .padding(start = 7.dp, end = 7.dp, top = 2.dp, bottom = 2.dp),
    ) {
        Text(text = label as String, fontSize = 9.sp, fontWeight = FontWeight.Medium, color = tc)
    }
}

@Composable
private fun SStepButton(label: String, style: SSBtnStyle, onClick: () -> Unit = {}) {
    val bg = when (style) {
        SSBtnStyle.FILLED -> MaterialTheme.colorScheme.primary
        SSBtnStyle.OUTLINE -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        SSBtnStyle.GREEN -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
    }
    val tc = when (style) {
        SSBtnStyle.FILLED -> MaterialTheme.colorScheme.onPrimary
        SSBtnStyle.OUTLINE -> MaterialTheme.colorScheme.primary
        SSBtnStyle.GREEN -> MaterialTheme.colorScheme.tertiary
    }
    val bd = when (style) {
        SSBtnStyle.FILLED -> MaterialTheme.colorScheme.primary
        SSBtnStyle.OUTLINE -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        SSBtnStyle.GREEN -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(bg)
            .border(0.5.dp, bd, RoundedCornerShape(99.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick,
            )
            .padding(start = 11.dp, end = 11.dp, top = 4.dp, bottom = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = tc)
    }
}

// ── 통계 바 ───────────────────────────────────────────────────────────────────

@Composable
private fun StatsBar(words: List<WordUi>) {
    val total = words.size
    val bookmarks = words.count { it.isBookmarked }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(R.string.vocab_stat_total) + " $total",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.word_bookmark_desc) + " $bookmarks",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ── 단어 카드 ─────────────────────────────────────────────────────────────────

@Composable
private fun WordCard(word: WordUi, onTap: () -> Unit, onBookmark: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 0.5.dp,
                color = if (word.isBookmarked) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onTap,
            )
            .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = word.japanese,
                fontFamily = NotoSerifJpFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = if (word.koreanReading.isNotBlank()) "${word.reading} · ${word.koreanReading}"
                else word.reading,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = word.meaning,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            PosTag(pos = word.partOfSpeech)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onBookmark,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (word.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.Bookmark,
                    contentDescription = stringResource(R.string.word_bookmark_desc),
                    tint = if (word.isBookmarked) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun PosTag(pos: PartOfSpeech) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f))
            .border(0.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f), RoundedCornerShape(99.dp))
            .padding(start = 7.dp, end = 7.dp, top = 2.dp, bottom = 2.dp),
    ) {
        Text(
            text = pos.displayLabel(),
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

// ── 단어 상세 바텀시트 ────────────────────────────────────────────────────────

@Composable
private fun WordDetailSheet(word: WordUi, onBookmark: () -> Unit, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = word.japanese,
            fontFamily = NotoSerifJpFamily,
            fontSize = 32.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = word.reading, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        if (word.koreanReading.isNotBlank()) {
            Text(text = word.koreanReading, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary, textAlign = TextAlign.Center)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = word.meaning,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            DetailTag(word.level.name)
            DetailTag(word.partOfSpeech.displayLabel())
        }
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onBookmark)
                .padding(start = 0.dp, end = 0.dp, top = 14.dp, bottom = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (word.isBookmarked) stringResource(R.string.word_bookmarked)
                else stringResource(R.string.word_bookmark_save),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClose)
                .padding(start = 0.dp, end = 0.dp, top = 14.dp, bottom = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.word_detail_close),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Composable
private fun DetailTag(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(99.dp))
            .padding(start = 10.dp, end = 10.dp, top = 3.dp, bottom = 3.dp),
    ) {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ── 퀴즈 시작 버튼 ─────────────────────────────────────────────────────────────

@Composable
private fun WordQuizStartButton(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest),
    ) {
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline)
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 12.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text(text = stringResource(R.string.word_quiz_start), fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ── 빈 상태 ───────────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(message: String = stringResource(R.string.word_empty)) {
    Box(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = message, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ── 가이드 버튼 ───────────────────────────────────────────────────────────────

@Composable
private fun GuideButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "❓", fontSize = 16.sp)
    }
}

// ── 가이드 바텀시트 ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordGuideSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest  = onDismiss,
        sheetState        = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor    = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 32.dp),
        ) {
            Text(
                text       = stringResource(R.string.word_guide_title),
                fontSize   = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(20.dp))

            val items = listOf(
                Pair(stringResource(R.string.word_guide_1_title), stringResource(R.string.word_guide_1_desc)),
                Pair(stringResource(R.string.word_guide_2_title), stringResource(R.string.word_guide_2_desc)),
                Pair(stringResource(R.string.word_guide_3_title), stringResource(R.string.word_guide_3_desc)),
                Pair(stringResource(R.string.word_guide_4_title), stringResource(R.string.word_guide_4_desc)),
                Pair(stringResource(R.string.word_guide_5_title), stringResource(R.string.word_guide_5_desc)),
            )
            items.forEachIndexed { index, (title, desc) ->
                GuideItem(number = index + 1, title = title, description = desc)
                if (index < items.lastIndex) Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null,
                        onClick           = onDismiss,
                    )
                    .padding(start = 0.dp, end = 0.dp, top = 14.dp, bottom = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text       = stringResource(R.string.word_guide_close),
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color      = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}

@Composable
private fun GuideItem(number: Int, title: String, description: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text       = number.toString(),
                fontSize   = 12.sp,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.onPrimary,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = title,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text       = description,
                fontSize   = 12.sp,
                color      = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp,
            )
        }
    }
}

// ── 품사 레이블 ───────────────────────────────────────────────────────────────

@Composable
private fun PartOfSpeech.displayLabel(): String = when (this) {
    PartOfSpeech.NOUN -> stringResource(R.string.pos_noun)
    PartOfSpeech.VERB -> stringResource(R.string.pos_verb)
    PartOfSpeech.I_ADJECTIVE -> stringResource(R.string.pos_i_adj)
    PartOfSpeech.NA_ADJECTIVE -> stringResource(R.string.pos_na_adj)
    PartOfSpeech.ADVERB -> stringResource(R.string.pos_adverb)
    PartOfSpeech.CONJUNCTION -> stringResource(R.string.pos_conjunction)
    PartOfSpeech.PARTICLE -> stringResource(R.string.pos_particle)
    PartOfSpeech.OTHER -> stringResource(R.string.pos_other)
}
