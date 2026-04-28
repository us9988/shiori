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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.usnine.shiori.R
import com.usnine.shiori.data.local.KanaData
import com.usnine.shiori.data.local.KanaItem
import com.usnine.shiori.presentation.analytics.AnalyticsManager
import com.usnine.shiori.presentation.analytics.CrashlyticsManager
import com.usnine.shiori.data.local.KanaRow
import com.usnine.shiori.ui.components.DuckImage
import com.usnine.shiori.ui.components.DuckMood

private const val ALL_KANA_MAX = 117

@Composable
fun KanaScreen(
    viewModel: KanaViewModel = hiltViewModel(),
    onNavigateToQuiz: (KanaTab, Set<String>) -> Unit = { _, _ -> },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.selectedTab) {
        val screenName = when (state.selectedTab) {
            KanaTab.HIRAGANA -> AnalyticsManager.SCREEN_KANA
            KanaTab.KATAKANA -> AnalyticsManager.SCREEN_KANA_KATAKANA
            KanaTab.DAKUTEN  -> AnalyticsManager.SCREEN_KANA_DAKUTEN
            KanaTab.ALL      -> AnalyticsManager.SCREEN_KANA_ALL
        }
        AnalyticsManager.logScreenView(screenName)
        CrashlyticsManager.setCurrentScreen(screenName)
    }

    val currentTabKana = remember(state.rows) {
        state.rows.flatMap { row -> row.items.filterNotNull().map { it.kana } }.toSet()
    }
    val selectedInTab = remember(state.learnedSet, currentTabKana) {
        state.learnedSet.intersect(currentTabKana)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        KanaHeader(
            selectedTab = state.selectedTab,
            onTabClick = { viewModel.onEvent(KanaContract.UiEvent.TabChanged(it)) },
        )

        if (state.selectedTab == KanaTab.ALL) {
            AllTabContent(
                allQuizCount = state.allQuizCount,
                onCountChanged = { viewModel.onEvent(KanaContract.UiEvent.AllQuizCountChanged(it)) },
                modifier = Modifier.weight(1f),
            )
        } else {
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
        }

        QuizButton(
            selectedTab = state.selectedTab,
            selectedCount = selectedInTab.size,
            allQuizCount = state.allQuizCount,
            onClick = {
                if (state.selectedTab == KanaTab.ALL) {
                    val allKana = (KanaData.hiraganaRows + KanaData.katakanaRows + KanaData.dakutenRows)
                        .flatMap { row -> row.items.filterNotNull().map { it.kana } }
                    val count = state.allQuizCount.coerceIn(1, allKana.size)
                    onNavigateToQuiz(KanaTab.ALL, allKana.shuffled().take(count).toSet())
                } else {
                    onNavigateToQuiz(state.selectedTab, selectedInTab)
                }
            },
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
private fun AllTabContent(
    allQuizCount: Int,
    onCountChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val chipValues = listOf(10, 20, 30, 50)
    var countText by remember(allQuizCount) { mutableStateOf(allQuizCount.toString()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DuckImage(
            mood = DuckMood.DEFAULT,
            modifier = Modifier.size(80.dp),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.kana_all_duck_title),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = stringResource(R.string.kana_all_duck_desc, ALL_KANA_MAX),
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RangeBadge(stringResource(R.string.kana_all_badge_hira), MaterialTheme.colorScheme.primary)
            RangeBadge(stringResource(R.string.kana_all_badge_kata), MaterialTheme.colorScheme.tertiary)
            RangeBadge(stringResource(R.string.kana_all_badge_daku), MaterialTheme.colorScheme.secondary)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                .padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.kana_all_count_label),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.05.sp,
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                chipValues.forEach { chipVal ->
                    val isActive = allQuizCount == chipVal
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isActive) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                0.5.dp,
                                if (isActive) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(8.dp),
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {
                                onCountChanged(chipVal)
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "${chipVal}개",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isActive) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline,
                )
                Text(
                    text = stringResource(R.string.kana_all_count_divider),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                BasicTextField(
                    value = countText,
                    onValueChange = { input ->
                        val digits = input.filter { it.isDigit() }.take(3)
                        countText = digits
                        val parsed = digits.toIntOrNull()
                        if (parsed != null && parsed in 1..ALL_KANA_MAX) {
                            onCountChanged(parsed)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            innerTextField()
                        }
                    },
                )
                Text(
                    text = stringResource(R.string.kana_all_count_unit),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.kana_all_count_max, ALL_KANA_MAX),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
private fun RangeBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(color.copy(alpha = 0.08f))
            .border(0.5.dp, color.copy(alpha = 0.2f), RoundedCornerShape(99.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = color,
        )
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
            text = item.koreanReading,
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun QuizButton(
    selectedTab: KanaTab,
    selectedCount: Int,
    allQuizCount: Int,
    onClick: () -> Unit,
) {
    val label = when {
        selectedTab == KanaTab.ALL -> stringResource(R.string.kana_all_start_btn, allQuizCount)
        selectedCount > 0          -> stringResource(R.string.kana_quiz_count_button, selectedCount)
        else                       -> stringResource(R.string.kana_quiz_button, selectedTab.label)
    }

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
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
