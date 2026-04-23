package com.usnine.shiori.presentation.feature.my

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.usnine.shiori.R
import com.usnine.shiori.ui.components.DuckImage
import com.usnine.shiori.ui.components.DuckMood
import com.usnine.shiori.ui.theme.NotoSerifJpFamily

@Composable
fun MyScreen(viewModel: MyViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LazyColumn(modifier = Modifier.fillMaxSize()) {

        // ── 오리 + 헤더 ───────────────────────────────────
        item {
            MyHeader(isEmpty = state.bookmarks.isEmpty())
        }

        // ── 플래시카드 섹션 ────────────────────────────────
        if (state.bookmarks.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                ) {
                    SectionLabel(stringResource(R.string.my_section_bookmarks))
                    Spacer(modifier = Modifier.height(10.dp))
                    FlashCard(
                        card      = state.currentCard,
                        isFlipped = state.isCardFlipped,
                        onCardTap = { viewModel.onEvent(MyContract.UiEvent.CardFlipped) },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ReviewButtons(
                        enabled = state.isCardFlipped && state.currentCard != null,
                        onKnow  = { viewModel.onEvent(MyContract.UiEvent.KnowTapped) },
                        onAgain = { viewModel.onEvent(MyContract.UiEvent.AgainTapped) },
                    )
                }
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline)
            }

            // ── 북마크 목록 ────────────────────────────────
            item {
                SectionLabel(
                    text     = stringResource(R.string.my_section_saved_words),
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 8.dp),
                )
            }
            items(state.bookmarks, key = { it.id }) { word ->
                BookmarkRow(
                    word      = word,
                    onRemove  = { viewModel.onEvent(MyContract.UiEvent.BookmarkRemoved(word.id)) },
                    modifier  = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                )
            }
        }

        // ── 학습통계 ───────────────────────────────────────
        item {
            HorizontalDivider(
                modifier  = Modifier.padding(top = if (state.bookmarks.isEmpty()) 0.dp else 16.dp),
                thickness = 0.5.dp,
                color     = MaterialTheme.colorScheme.outline,
            )
            SectionLabel(
                text     = stringResource(R.string.my_section_stats),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 10.dp),
            )
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatCard(label = stringResource(R.string.my_stats_kana),     value = "${state.totalBookmarks}",  modifier = Modifier.weight(1f))
                StatCard(label = stringResource(R.string.my_stats_streak),  value = "${state.streakDays}일",    modifier = Modifier.weight(1f))
                StatCard(label = stringResource(R.string.my_stats_complete), value = "${state.completedWords}", modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── 헤더 (오리 이미지) ────────────────────────────────────────────────────────

@Composable
private fun MyHeader(isEmpty: Boolean) {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(top = 24.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text     = stringResource(R.string.my_title),
            style    = MaterialTheme.typography.titleMedium,
            fontSize = 20.sp,
            color    = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
        )

        DuckImage(
            mood     = if (isEmpty) DuckMood.EMPTY else DuckMood.DEFAULT,
            modifier = Modifier.size(120.dp),
        )

        if (isEmpty) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text      = stringResource(R.string.my_empty_words),
                fontSize  = 13.sp,
                fontWeight = FontWeight.Medium,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text      = stringResource(R.string.my_empty_words_subtitle),
                fontSize  = 11.sp,
                color     = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
            )
        }
    }
    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline)
}

// ── 플래시카드 ─────────────────────────────────────────────────────────────────

@Composable
private fun FlashCard(
    card: WordUi?,
    isFlipped: Boolean,
    onCardTap: () -> Unit,
) {
    val rotation by animateFloatAsState(
        targetValue   = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label         = "cardFlip",
    )
    val isShowingBack = rotation > 90f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .graphicsLayer { rotationY = rotation; cameraDistance = 12f * density }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onCardTap,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (card == null) {
            Text(
                text       = stringResource(R.string.vocab_card_done),
                fontSize   = 18.sp,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.primary,
            )
        } else if (!isShowingBack) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text       = card.japanese,
                    fontFamily = NotoSerifJpFamily,
                    fontSize   = 48.sp,
                    fontWeight = FontWeight.Medium,
                    color      = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text     = stringResource(R.string.vocab_card_hint),
                    fontSize = 10.sp,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .graphicsLayer { rotationY = 180f }
                    .padding(horizontal = 20.dp),
            ) {
                Text(
                    text      = card.reading,
                    fontSize  = 12.sp,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text       = card.meaning,
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color      = MaterialTheme.colorScheme.onBackground,
                    textAlign  = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text(
                        text     = card.partOfSpeech.name,
                        fontSize = 10.sp,
                        color    = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewButtons(enabled: Boolean, onKnow: () -> Unit, onAgain: () -> Unit) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ActionButton(
            text           = stringResource(R.string.vocab_btn_again),
            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.10f),
            contentColor   = MaterialTheme.colorScheme.error,
            borderColor    = MaterialTheme.colorScheme.error.copy(alpha = 0.30f),
            enabled        = enabled,
            onClick        = onAgain,
            modifier       = Modifier.weight(1f),
        )
        ActionButton(
            text           = stringResource(R.string.vocab_btn_know),
            containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.10f),
            contentColor   = MaterialTheme.colorScheme.tertiary,
            borderColor    = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.30f),
            enabled        = enabled,
            onClick        = onKnow,
            modifier       = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ActionButton(
    text: String,
    containerColor: Color,
    contentColor: Color,
    borderColor: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor by animateColorAsState(targetValue = if (enabled) containerColor else MaterialTheme.colorScheme.surface, label = "bg")
    val fgColor by animateColorAsState(targetValue = if (enabled) contentColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), label = "fg")
    val bdColor by animateColorAsState(targetValue = if (enabled) borderColor else MaterialTheme.colorScheme.outline, label = "bd")

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(0.5.dp, bdColor, RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                enabled           = enabled,
                onClick           = onClick,
            )
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = fgColor)
    }
}

// ── 북마크 목록 ───────────────────────────────────────────────────────────────

@Composable
private fun BookmarkRow(
    word: WordUi,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier          = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text       = word.japanese,
            fontFamily = NotoSerifJpFamily,
            fontSize   = 18.sp,
            fontWeight = FontWeight.Medium,
            color      = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text     = word.reading,
                fontSize = 10.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text       = word.meaning,
                fontSize   = 12.sp,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.onBackground,
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(99.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(99.dp))
                .padding(horizontal = 7.dp, vertical = 2.dp),
        ) {
            Text(
                text     = word.level.name,
                fontSize = 9.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onRemove,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.Default.Close,
                contentDescription = stringResource(R.string.my_bookmark_remove_desc),
                tint               = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier           = Modifier.size(14.dp),
            )
        }
    }
}

// ── 통계 카드 ─────────────────────────────────────────────────────────────────

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text       = value,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Medium,
            color      = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text     = label,
            fontSize = 10.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ── 공통 ─────────────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text          = text.uppercase(),
        fontSize      = 11.sp,
        fontWeight    = FontWeight.Medium,
        color         = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 0.06.sp,
        modifier      = modifier,
    )
}
