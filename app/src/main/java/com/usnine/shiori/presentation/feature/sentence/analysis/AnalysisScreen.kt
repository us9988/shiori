package com.usnine.shiori.presentation.feature.sentence.analysis

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Bookmark as BookmarkOutlined
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.usnine.shiori.R
import com.usnine.shiori.ui.theme.NotoSerifJpFamily

@Composable
fun AnalysisScreen(viewModel: AnalysisViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AnalysisContract.UiEffect.ShowSnackbar ->
                    snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                InputSection()
            }

            if (state.words.isNotEmpty()) {
                item {
                    Text(
                        text          = stringResource(R.string.analysis_words_section).uppercase(),
                        fontSize      = 11.sp,
                        fontWeight    = FontWeight.Medium,
                        color         = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.06.sp,
                        modifier      = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp),
                    )
                }
                items(state.words, key = { it.id }) { word ->
                    WordCard(
                        word          = word,
                        onBookmarkTap = { viewModel.onEvent(AnalysisContract.UiEvent.BookmarkTapped(word.id)) },
                        modifier      = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                    )
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
        )
    }
}

@Composable
private fun InputSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text     = stringResource(R.string.analysis_input_hint),
                fontSize = 13.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    // TODO: Phase 2 — Claude API 연결 후 활성화
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text       = stringResource(R.string.analysis_button),
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color      = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

@Composable
private fun WordCard(
    word: WordUi,
    onBookmarkTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 일본어 + 읽기
        Column {
            Text(
                text       = word.japanese,
                fontFamily = NotoSerifJpFamily,
                fontSize   = 22.sp,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.primary,
            )
            Text(
                text     = word.reading,
                fontSize = 10.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 뜻 + 품사/레벨 뱃지
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = word.meaning,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                WordBadge(word.partOfSpeech.name)
                WordBadge(word.level.name)
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // 북마크 아이콘
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onBookmarkTap,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = if (word.isBookmarked) Icons.Filled.Bookmark
                                     else Icons.Outlined.Bookmark,
                contentDescription = stringResource(R.string.analysis_bookmark_action),
                tint               = if (word.isBookmarked) MaterialTheme.colorScheme.primary
                                     else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier           = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun WordBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
            .padding(horizontal = 7.dp, vertical = 2.dp),
    ) {
        Text(
            text     = text,
            fontSize = 9.sp,
            color    = MaterialTheme.colorScheme.primary,
        )
    }
}
