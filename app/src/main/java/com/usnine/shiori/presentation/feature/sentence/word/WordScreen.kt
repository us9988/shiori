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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.usnine.shiori.R
import com.usnine.shiori.presentation.analytics.AnalyticsManager
import com.usnine.shiori.presentation.analytics.CrashlyticsManager
import com.usnine.shiori.data.local.entity.PartOfSpeech
import com.usnine.shiori.data.local.entity.WordLevel
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
    onStartQuiz: () -> Unit = {}, // TODO: 단어 퀴즈 화면 구현 시 연결
    viewModel: WordViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val detailWord = state.words.find { it.id == state.detailWordId }
    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val showScrollTop by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }

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
                sortType            = state.sortType,
                selectedLevel       = state.selectedLevel,
                selectedPos         = state.selectedPos,
                isSearchActive      = state.isSearchActive,
                searchQuery         = state.searchQuery,
                focusRequester      = focusRequester,
                onSortChange        = { viewModel.onEvent(WordContract.UiEvent.SortTypeChanged(it)) },
                onLevelSelect       = { viewModel.onEvent(WordContract.UiEvent.LevelSelected(it)) },
                onPosSelect         = { viewModel.onEvent(WordContract.UiEvent.PosSelected(it)) },
                onSearchToggle      = { viewModel.onEvent(WordContract.UiEvent.SearchToggled) },
                onSearchQueryChange = { viewModel.onEvent(WordContract.UiEvent.SearchQueryChanged(it)) },
            )
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline)

            LazyColumn(
                state    = listState,
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
                            word       = word,
                            onTap      = { viewModel.onEvent(WordContract.UiEvent.WordTapped(word.id)) },
                            onBookmark = { viewModel.onEvent(WordContract.UiEvent.BookmarkToggled(word.id)) },
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
            WordQuizStartButton(onClick = onStartQuiz)
        }

        AnimatedVisibility(
            visible = showScrollTop,
            enter   = fadeIn(),
            exit    = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 96.dp),
        ) {
            FloatingActionButton(
                onClick          = { scope.launch { listState.animateScrollToItem(0) } },
                containerColor   = MaterialTheme.colorScheme.primary,
                contentColor     = MaterialTheme.colorScheme.onPrimary,
                modifier         = Modifier.size(44.dp),
            ) {
                Icon(
                    imageVector        = Icons.Filled.KeyboardArrowUp,
                    contentDescription = null,
                    modifier           = Modifier.size(22.dp),
                )
            }
        }
    }

    if (detailWord != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onEvent(WordContract.UiEvent.DetailDismissed) },
            sheetState       = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor   = MaterialTheme.colorScheme.background,
        ) {
            WordDetailSheet(
                word       = detailWord,
                onBookmark = { viewModel.onEvent(WordContract.UiEvent.BookmarkToggled(detailWord.id)) },
                onClose    = { viewModel.onEvent(WordContract.UiEvent.DetailDismissed) },
            )
        }
    }
}

// ── 헤더 ──────────────────────────────────────────────────────────────────────

@Composable
private fun WordHeader(
    sortType: WordSortType,
    selectedLevel: WordLevel,
    selectedPos: PartOfSpeech?,
    isSearchActive: Boolean,
    searchQuery: String,
    focusRequester: FocusRequester,
    onSortChange: (WordSortType) -> Unit,
    onLevelSelect: (WordLevel) -> Unit,
    onPosSelect: (PartOfSpeech?) -> Unit,
    onSearchToggle: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 12.dp),
    ) {
        if (isSearchActive) {
            SearchBar(
                query          = searchQuery,
                focusRequester = focusRequester,
                onQueryChange  = onSearchQueryChange,
                onClose        = onSearchToggle,
            )
        } else {
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SortTypeToggle(
                    selected = sortType,
                    onChange = onSortChange,
                    modifier = Modifier.weight(1f),
                )
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
            Spacer(modifier = Modifier.height(10.dp))
            when (sortType) {
                WordSortType.LEVEL          -> LevelTabRow(selected = selectedLevel, onSelect = onLevelSelect)
                WordSortType.PART_OF_SPEECH -> PosChipRow(selected = selectedPos, onSelect = onPosSelect)
            }
        }
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
            imageVector        = Icons.Filled.Search,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier           = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        BasicTextField(
            value         = query,
            onValueChange = onQueryChange,
            modifier      = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            singleLine    = true,
            textStyle     = TextStyle(
                fontSize = 13.sp,
                color    = MaterialTheme.colorScheme.onBackground,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            decorationBox   = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (query.isEmpty()) {
                        Text(
                            text     = stringResource(R.string.word_search_hint),
                            fontSize = 13.sp,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
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
                    indication        = null,
                    onClick           = onClose,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.Filled.Close,
                contentDescription = stringResource(R.string.word_detail_close),
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(16.dp),
            )
        }
    }
}

// ── 수준별/품사별 세그먼트 토글 ────────────────────────────────────────────────

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
                WordSortType.LEVEL          -> stringResource(R.string.word_sort_level)
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
                        else
                            Modifier.clip(RoundedCornerShape(6.dp))
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null,
                        onClick           = { onChange(type) },
                    )
                    .padding(start = 0.dp, end = 0.dp, top = 5.dp, bottom = 5.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text       = label,
                    fontSize   = 11.sp,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
                    color      = if (isActive) MaterialTheme.colorScheme.primary
                                 else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ── 수준 탭 (N5~N1, 가로 스크롤 pill) ─────────────────────────────────────────

@Composable
private fun LevelTabRow(selected: WordLevel, onSelect: (WordLevel) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        WordLevel.entries.forEach { level ->
            val isActive = level == selected
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
                        indication        = null,
                        onClick           = { onSelect(level) },
                    )
                    .padding(start = 14.dp, end = 14.dp, top = 5.dp, bottom = 5.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text       = level.name,
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color      = if (isActive) MaterialTheme.colorScheme.onPrimary
                                 else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ── 품사 칩 (가로 스크롤) ─────────────────────────────────────────────────────

@Composable
private fun PosChipRow(selected: PartOfSpeech?, onSelect: (PartOfSpeech?) -> Unit) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        posFilterOptions.forEach { pos ->
            val isActive = pos == selected
            val label = if (pos == null) stringResource(R.string.word_pos_all)
                        else pos.displayLabel()
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
                        indication        = null,
                        onClick           = { onSelect(pos) },
                    )
                    .padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text       = label,
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color      = if (isActive) MaterialTheme.colorScheme.onPrimary
                                 else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ── 통계 바 ───────────────────────────────────────────────────────────────────

@Composable
private fun StatsBar(words: List<WordUi>) {
    val total     = words.size
    val bookmarks = words.count { it.isBookmarked }

    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text     = stringResource(R.string.vocab_stat_total) + " $total",
            fontSize = 10.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text     = stringResource(R.string.word_bookmark_desc) + " $bookmarks",
            fontSize = 10.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ── 단어 카드 ─────────────────────────────────────────────────────────────────

@Composable
private fun WordCard(
    word: WordUi,
    onTap: () -> Unit,
    onBookmark: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width  = 0.5.dp,
                color  = if (word.isBookmarked)
                             MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                         else
                             MaterialTheme.colorScheme.outline,
                shape  = RoundedCornerShape(12.dp),
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onTap,
            )
            .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = word.japanese,
                fontFamily = NotoSerifJpFamily,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Normal,
                color      = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text  = if (word.koreanReading.isNotBlank())
                            "${word.reading} · ${word.koreanReading}"
                        else
                            word.reading,
                fontSize = 11.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text       = word.meaning,
                fontSize   = 13.sp,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.primary,
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
                        indication        = null,
                        onClick           = onBookmark,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = if (word.isBookmarked) Icons.Filled.Bookmark
                                         else Icons.Outlined.Bookmark,
                    contentDescription = stringResource(R.string.word_bookmark_desc),
                    tint               = if (word.isBookmarked)
                                             MaterialTheme.colorScheme.primary
                                         else
                                             MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                    modifier           = Modifier.size(18.dp),
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
            text       = pos.displayLabel(),
            fontSize   = 9.sp,
            fontWeight = FontWeight.Medium,
            color      = MaterialTheme.colorScheme.secondary,
        )
    }
}

// ── 단어 상세 바텀시트 ────────────────────────────────────────────────────────

@Composable
private fun WordDetailSheet(
    word: WordUi,
    onBookmark: () -> Unit,
    onClose: () -> Unit,
) {
    Column(
        modifier             = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 32.dp),
        horizontalAlignment  = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text       = word.japanese,
            fontFamily = NotoSerifJpFamily,
            fontSize   = 32.sp,
            fontWeight = FontWeight.Normal,
            color      = MaterialTheme.colorScheme.onBackground,
            textAlign  = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text      = word.reading,
            fontSize  = 13.sp,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (word.koreanReading.isNotBlank()) {
            Text(
                text      = word.koreanReading,
                fontSize  = 12.sp,
                color     = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text       = word.meaning,
            fontSize   = 18.sp,
            fontWeight = FontWeight.Medium,
            color      = MaterialTheme.colorScheme.primary,
            textAlign  = TextAlign.Center,
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
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onBookmark,
                )
                .padding(start = 0.dp, end = 0.dp, top = 14.dp, bottom = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text       = if (word.isBookmarked)
                                 stringResource(R.string.word_bookmarked)
                             else
                                 stringResource(R.string.word_bookmark_save),
                fontSize   = 13.sp,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.onPrimary,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onClose,
                )
                .padding(start = 0.dp, end = 0.dp, top = 14.dp, bottom = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text       = stringResource(R.string.word_detail_close),
                fontSize   = 13.sp,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.onBackground,
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
        Text(
            text       = label,
            fontSize   = 10.sp,
            fontWeight = FontWeight.Medium,
            color      = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ── 단어 퀴즈 시작 버튼 ────────────────────────────────────────────────────────

@Composable
private fun WordQuizStartButton(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest),
    ) {
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline)
        Button(
            onClick  = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 12.dp),
            shape  = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor   = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text(
                text       = stringResource(R.string.word_quiz_start),
                fontSize   = 13.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

// ── 빈 상태 ───────────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(message: String = stringResource(R.string.word_empty)) {
    Box(
        modifier         = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text     = message,
            fontSize = 13.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ── 품사 레이블 ───────────────────────────────────────────────────────────────

@Composable
private fun PartOfSpeech.displayLabel(): String = when (this) {
    PartOfSpeech.NOUN         -> stringResource(R.string.pos_noun)
    PartOfSpeech.VERB         -> stringResource(R.string.pos_verb)
    PartOfSpeech.I_ADJECTIVE  -> stringResource(R.string.pos_i_adj)
    PartOfSpeech.NA_ADJECTIVE -> stringResource(R.string.pos_na_adj)
    PartOfSpeech.ADVERB       -> stringResource(R.string.pos_adverb)
    PartOfSpeech.CONJUNCTION  -> stringResource(R.string.pos_conjunction)
    PartOfSpeech.PARTICLE     -> stringResource(R.string.pos_particle)
    PartOfSpeech.OTHER        -> stringResource(R.string.pos_other)
}
