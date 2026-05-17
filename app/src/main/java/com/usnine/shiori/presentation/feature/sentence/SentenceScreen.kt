package com.usnine.shiori.presentation.feature.sentence

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.usnine.shiori.R
import com.usnine.shiori.data.local.entity.WordLevel
import com.usnine.shiori.presentation.feature.sentence.conversation.ConversationScreen
import com.usnine.shiori.presentation.feature.sentence.word.WordScreen

@Composable
fun SentenceScreen(
    onNavigateToWordQuiz: () -> Unit = {},
    onNavigateToWordStudyStep: (WordLevel, Int, Boolean) -> Unit = { _, _, _ -> },
    viewModel: SentenceViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        SentenceHeader(
            selectedTab = state.selectedTab,
            onTabClick  = { viewModel.onEvent(SentenceContract.UiEvent.TabChanged(it)) },
        )
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline)
        when (state.selectedTab) {
            SentenceTab.WORD -> WordScreen(
                onStartQuiz  = onNavigateToWordQuiz,
                onStepTapped = onNavigateToWordStudyStep,
            )
            SentenceTab.CONVERSATION -> ConversationScreen()
        }
    }
}

@Composable
private fun SentenceHeader(
    selectedTab: SentenceTab,
    onTabClick: (SentenceTab) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 14.dp),
    ) {
        Text(
            text     = stringResource(R.string.sentence_title),
            style    = MaterialTheme.typography.titleMedium,
            fontSize = 20.sp,
            color    = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(12.dp))
        SentenceTabSelector(selectedTab = selectedTab, onTabClick = onTabClick)
    }
}

@Composable
private fun SentenceTabSelector(
    selectedTab: SentenceTab,
    onTabClick: (SentenceTab) -> Unit,
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(3.dp),
    ) {
        SentenceTab.entries.forEach { tab ->
            val isActive = tab == selectedTab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (isActive) Modifier
                            .shadow(elevation = 1.dp, shape = RoundedCornerShape(6.dp))
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        else Modifier.clip(RoundedCornerShape(6.dp))
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null,
                        onClick           = { onTabClick(tab) },
                    )
                    .padding(vertical = 5.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text       = tab.label,
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color      = if (isActive) MaterialTheme.colorScheme.primary
                                 else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign  = TextAlign.Center,
                )
            }
        }
    }
}
