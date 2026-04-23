package com.usnine.shiori.presentation.feature.kana

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.usnine.shiori.R
import com.usnine.shiori.data.local.KanaItem
import com.usnine.shiori.data.local.KanaRow

@Composable
fun KanaScreen(
    viewModel: KanaViewModel = hiltViewModel(),
    onNavigateToQuiz: (KanaTab) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        KanaHeader(
            selectedTab = state.selectedTab,
            onTabClick = { viewModel.onEvent(KanaContract.UiEvent.TabChanged(it)) },
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        ) {
            items(state.rows) { row ->
                KanaRowSection(
                    row = row,
                    learnedSet = state.learnedSet,
                    onKanaTap = { viewModel.onEvent(KanaContract.UiEvent.KanaTapped(it)) },
                )
            }
            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
        QuizButton(
            selectedTab = state.selectedTab,
            onClick = { onNavigateToQuiz(state.selectedTab) },
        )
    }
}

@Composable
private fun KanaHeader(
    selectedTab: KanaTab,
    onTabClick: (KanaTab) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 14.dp),
    ) {
        Text(
            text = stringResource(R.string.kana_title),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(12.dp))
        KanaTabSelector(selectedTab = selectedTab, onTabClick = onTabClick)
    }
    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline)
}

@Composable
private fun KanaTabSelector(
    selectedTab: KanaTab,
    onTabClick: (KanaTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        KanaTab.entries.forEach { tab ->
            val isActive = tab == selectedTab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (isActive) Modifier
                            .shadow(elevation = 1.dp, shape = RoundedCornerShape(6.dp))
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        else Modifier
                            .clip(RoundedCornerShape(6.dp))
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onTabClick(tab) },
                    )
                    .padding(vertical = 5.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = tab.label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isActive) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun KanaRowSection(
    row: KanaRow,
    learnedSet: Set<String>,
    onKanaTap: (String) -> Unit,
) {
    Column(modifier = Modifier.padding(top = 10.dp)) {
        Text(
            text = row.label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.06.sp,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            row.items.forEach { item ->
                Box(modifier = Modifier.weight(1f)) {
                    if (item != null) {
                        KanaCell(item = item, isLearned = item.kana in learnedSet, onTap = onKanaTap)
                    }
                }
            }
        }
    }
}

@Composable
private fun KanaCell(
    item: KanaItem,
    isLearned: Boolean,
    onTap: (String) -> Unit,
) {
    val bgColor = if (isLearned) MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                  else MaterialTheme.colorScheme.surface
    val borderColor = if (isLearned) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                      else MaterialTheme.colorScheme.outline
    val kanaColor = if (isLearned) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(width = 0.5.dp, color = borderColor, shape = RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onTap(item.kana) },
            )
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = item.kana,
            fontSize = 16.sp,
            color = kanaColor,
            lineHeight = 19.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = item.romaji,
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun QuizButton(selectedTab: KanaTab, onClick: () -> Unit) {
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
                .padding(horizontal = 20.dp, vertical = 12.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text(
                text = stringResource(R.string.kana_quiz_button, selectedTab.label),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
