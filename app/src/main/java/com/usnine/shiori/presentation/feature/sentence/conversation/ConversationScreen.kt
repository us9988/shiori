package com.usnine.shiori.presentation.feature.sentence.conversation

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.usnine.shiori.ui.theme.NotoSerifJpFamily

@Composable
fun ConversationScreen(viewModel: ConversationViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        FilterChipRow(
            selected  = state.selectedFilter,
            onSelect  = { viewModel.onEvent(ConversationContract.UiEvent.FilterChanged(it)) },
        )
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline)

        if (state.displayedPhrases.isEmpty()) {
            EmptyState(modifier = Modifier.weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                items(state.displayedPhrases, key = { it.id }) { phrase ->
                    PhraseCard(
                        phrase    = phrase,
                        isPlaying = state.playingId == phrase.id,
                        onPlayTap = { viewModel.onEvent(ConversationContract.UiEvent.PlayTapped(phrase)) },
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline)
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

// ── 필터 칩 ───────────────────────────────────────────────────────────────────

@Composable
private fun FilterChipRow(
    selected: ConversationFilter,
    onSelect: (ConversationFilter) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        ConversationFilter.entries.forEach { filter ->
            FilterChip(
                label      = filter.label,
                isSelected = filter == selected,
                onClick    = { onSelect(filter) },
            )
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val bgColor     = if (isSelected) MaterialTheme.colorScheme.primary
                      else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
                       else MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor  = if (isSelected) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.outline

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(bgColor)
            .border(0.5.dp, borderColor, RoundedCornerShape(99.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick,
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text       = label,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Medium,
            color      = contentColor,
        )
    }
}

// ── 문장 카드 ─────────────────────────────────────────────────────────────────

@Composable
private fun PhraseCard(
    phrase: PhraseUi,
    isPlaying: Boolean,
    onPlayTap: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = phrase.japanese,
                fontFamily = NotoSerifJpFamily,
                fontSize   = 17.sp,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.onBackground,
                lineHeight = 24.sp,
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text     = phrase.hiragana,
                fontSize = 11.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text     = phrase.korean,
                fontSize = 12.sp,
                color    = MaterialTheme.colorScheme.primary,
                lineHeight = 18.sp,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        PlayButton(isPlaying = isPlaying, onClick = onPlayTap)
    }
}

@Composable
private fun PlayButton(isPlaying: Boolean, onClick: () -> Unit) {
    val bgColor      = if (isPlaying) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.surface
    val iconColor    = if (isPlaying) MaterialTheme.colorScheme.onPrimary
                       else MaterialTheme.colorScheme.primary
    val borderColor  = if (isPlaying) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.outline

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(bgColor)
            .border(0.5.dp, borderColor, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector        = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "일시정지" else "재생",
            tint               = iconColor,
            modifier           = Modifier.size(18.dp),
        )
    }
}

// ── 빈 상태 ───────────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier         = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text     = "문장이 없어요",
            fontSize = 13.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
