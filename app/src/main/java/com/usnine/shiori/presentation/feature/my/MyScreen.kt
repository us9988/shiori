package com.usnine.shiori.presentation.feature.my

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import dagger.hilt.android.EntryPointAccessors
import com.usnine.shiori.presentation.billing.BillingEntryPoint
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.usnine.shiori.presentation.analytics.AnalyticsManager
import com.usnine.shiori.presentation.analytics.CrashlyticsManager
import com.usnine.shiori.ui.theme.NotoSerifJpFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScreen(viewModel: MyViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        AnalyticsManager.logScreenView(AnalyticsManager.SCREEN_MY)
        CrashlyticsManager.setCurrentScreen(AnalyticsManager.SCREEN_MY)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                MyContract.UiEffect.LaunchPlayStore -> {
                    try {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    } catch (e: ActivityNotFoundException) {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                }
                MyContract.UiEffect.LaunchPurchaseFlow -> {
                    val billingManager = EntryPointAccessors
                        .fromApplication(context.applicationContext, BillingEntryPoint::class.java)
                        .billingManager()
                    (context as? Activity)?.let { billingManager.launchPurchaseFlow(it) }
                }
                MyContract.UiEffect.ShowRestoreSuccess ->
                    snackbarHostState.showSnackbar(context.getString(R.string.my_restore_success))
                MyContract.UiEffect.ShowRestoreFailure ->
                    snackbarHostState.showSnackbar(context.getString(R.string.my_restore_failure))
            }
        }
    }

    BackHandler(enabled = state.showWordList) {
        viewModel.onEvent(MyContract.UiEvent.WordListClosed)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.showWordList) {
            WordListScreen(
                state    = state,
                onBack   = { viewModel.onEvent(MyContract.UiEvent.WordListClosed) },
                onFlip   = { viewModel.onEvent(MyContract.UiEvent.CardFlipped) },
                onNext   = { viewModel.onEvent(MyContract.UiEvent.NextTapped) },
                onRemove = { viewModel.onEvent(MyContract.UiEvent.BookmarkRemoved(it)) },
            )
        } else {
            MainScreen(
                state              = state,
                onWordListOpen     = { viewModel.onEvent(MyContract.UiEvent.WordListOpened) },
                onPurchaseTapped   = { viewModel.onEvent(MyContract.UiEvent.PurchaseTapped) },
                onRestorePurchase  = { viewModel.onEvent(MyContract.UiEvent.RestorePurchaseTapped) },
                onDarkModeToggle   = { viewModel.onEvent(MyContract.UiEvent.DarkModeToggled) },
                onRateTapped       = { viewModel.onEvent(MyContract.UiEvent.RateTapped) },
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter),
        )
    }

    if (state.showPremiumModal) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest  = { viewModel.onEvent(MyContract.UiEvent.PremiumModalClosed) },
            sheetState        = sheetState,
            containerColor    = MaterialTheme.colorScheme.background,
        ) {
            PremiumModalContent(
                onBuy     = {
                    viewModel.onEvent(MyContract.UiEvent.PremiumModalClosed)
                    viewModel.onEvent(MyContract.UiEvent.PurchaseTapped)
                },
                onRestore = { viewModel.onEvent(MyContract.UiEvent.RestorePurchaseTapped) },
                onCancel  = { viewModel.onEvent(MyContract.UiEvent.PremiumModalClosed) },
            )
        }
    }
}

// ── 메인 화면 ─────────────────────────────────────────────────────────────────

@Composable
private fun MainScreen(
    state: MyContract.UiState,
    onWordListOpen: () -> Unit,
    onPurchaseTapped: () -> Unit,
    onRestorePurchase: () -> Unit,
    onDarkModeToggle: () -> Unit,
    onRateTapped: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        MyHeader(title = stringResource(R.string.my_title), onBack = null)
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
        ) {
            // 프리미엄 배너
            item {
                Spacer(modifier = Modifier.height(16.dp))
                PremiumBanner(
                    isPremium        = state.isPremium,
                    onPurchaseTapped = onPurchaseTapped,
                    onRestorePurchase = onRestorePurchase,
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // 개인 섹션
            item {
                SectionLabel(stringResource(R.string.my_section_personal))
                Spacer(modifier = Modifier.height(6.dp))
                SettingItem(
                    title    = stringResource(R.string.my_section_bookmarks),
                    subtitle = stringResource(R.string.my_bookmarks_count, state.bookmarks.size),
                    trailing = { SettingArrow() },
                    onClick  = onWordListOpen,
                )
                Spacer(modifier = Modifier.height(5.dp))
                SettingItem(
                    title    = stringResource(R.string.my_dark_mode),
                    subtitle = stringResource(R.string.my_dark_mode_sub),
                    trailing = {
                        DarkModeToggle(
                            isOn    = state.isDarkMode,
                            onClick = onDarkModeToggle,
                        )
                    },
                    onClick  = onDarkModeToggle,
                )
                Spacer(modifier = Modifier.height(5.dp))
                SettingItem(
                    title    = stringResource(R.string.my_premium_restore),
                    subtitle = stringResource(R.string.my_restore_sub),
                    trailing = { SettingArrow() },
                    onClick  = onRestorePurchase,
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // 앱 정보 섹션
            item {
                SectionLabel(stringResource(R.string.my_section_app_info))
                Spacer(modifier = Modifier.height(6.dp))
                SettingItem(
                    title    = stringResource(R.string.my_rate),
                    subtitle = stringResource(R.string.my_rate_sub),
                    trailing = { SettingArrow() },
                    onClick  = onRateTapped,
                )
                Spacer(modifier = Modifier.height(5.dp))
                SettingItem(
                    title    = stringResource(R.string.my_version),
                    subtitle = stringResource(R.string.my_version_value),
                    trailing = { VersionBadge() },
                    onClick  = {},
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ── 내 단어장 화면 ─────────────────────────────────────────────────────────────

@Composable
private fun WordListScreen(
    state: MyContract.UiState,
    onBack: () -> Unit,
    onFlip: () -> Unit,
    onNext: () -> Unit,
    onRemove: (Long) -> Unit,
) {
    LaunchedEffect(Unit) {
        AnalyticsManager.logScreenView(AnalyticsManager.SCREEN_MY_WORDLIST)
        CrashlyticsManager.setCurrentScreen(AnalyticsManager.SCREEN_MY_WORDLIST)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text     = stringResource(R.string.my_back),
                    fontSize = 22.sp,
                    color    = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null,
                        onClick           = onBack,
                    ),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text      = stringResource(
                        R.string.my_word_list_subtitle,
                        state.bookmarks.size,
                        state.todayReviewCount,
                    ),
                    fontSize  = 10.sp,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier  = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(12.dp))
                FlashCard(
                    card      = state.currentCard,
                    isFlipped = state.isCardFlipped,
                    onCardTap = onFlip,
                )
                Spacer(modifier = Modifier.height(10.dp))
                NextButton(
                    enabled = state.currentCard != null,
                    onClick = onNext,
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (state.bookmarks.isNotEmpty()) {
                    Text(
                        text          = stringResource(R.string.vocab_words_section).uppercase(),
                        fontSize      = 10.sp,
                        fontWeight    = FontWeight.Medium,
                        color         = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.06.sp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            items(state.bookmarks, key = { it.id }) { word ->
                WordListItem(word = word, onRemove = { onRemove(word.id) })
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

// ── 헤더 ──────────────────────────────────────────────────────────────────────

@Composable
private fun MyHeader(title: String, onBack: (() -> Unit)?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (onBack != null) {
            Text(
                text     = stringResource(R.string.my_back),
                fontSize = 22.sp,
                color    = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onBack,
                ),
            )
        }
        Text(
            text       = title,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Medium,
            color      = MaterialTheme.colorScheme.primary,
        )
    }
}

// ── 프리미엄 배너 ──────────────────────────────────────────────────────────────

@Composable
private fun PremiumBanner(
    isPremium: Boolean,
    onPurchaseTapped: () -> Unit,
    onRestorePurchase: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(start = 16.dp, end = 56.dp, top = 14.dp, bottom = 14.dp),
    ) {
        if (isPremium) {
            Column {
                Text(
                    text     = stringResource(R.string.my_premium_label),
                    fontSize = 10.sp,
                    color    = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text       = stringResource(R.string.my_premium_title),
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color      = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                ) {
                    Text(
                        text       = stringResource(R.string.my_premium_active),
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color      = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        } else {
            Column {
                Text(
                    text     = stringResource(R.string.my_premium_label),
                    fontSize = 10.sp,
                    color    = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text       = stringResource(R.string.my_premium_title),
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color      = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text     = stringResource(R.string.my_premium_sub),
                    fontSize = 10.sp,
                    color    = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = null,
                                onClick           = onPurchaseTapped,
                            )
                            .padding(horizontal = 10.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text       = stringResource(R.string.my_premium_buy),
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color      = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = null,
                                onClick           = onRestorePurchase,
                            )
                            .padding(horizontal = 10.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text       = stringResource(R.string.my_premium_restore),
                            fontSize   = 11.sp,
                            color      = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                        )
                    }
                }
            }
        }
        // 오리 워터마크
        Text(
            text     = "🦆",
            fontSize = 36.sp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .graphicsLayer { alpha = 0.15f },
        )
    }
}

// ── 설정 아이템 ────────────────────────────────────────────────────────────────

@Composable
private fun SettingItem(
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick,
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = title,
                fontSize   = 12.sp,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text     = subtitle,
                fontSize = 10.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        trailing()
    }
}

@Composable
private fun SettingArrow() {
    Text(
        text     = "›",
        fontSize = 18.sp,
        color    = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun VersionBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
            .border(
                0.5.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                RoundedCornerShape(99.dp),
            )
            .padding(horizontal = 7.dp, vertical = 2.dp),
    ) {
        Text(
            text       = stringResource(R.string.my_version_badge),
            fontSize   = 9.sp,
            fontWeight = FontWeight.Medium,
            color      = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun DarkModeToggle(isOn: Boolean, onClick: () -> Unit) {
    val trackColor by animateColorAsState(
        targetValue   = if (isOn) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(200),
        label         = "trackColor",
    )
    Box(
        modifier = Modifier
            .width(36.dp)
            .height(20.dp)
            .clip(RoundedCornerShape(99.dp))
            .background(trackColor)
            .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(99.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick,
            ),
    ) {
        val knobOffset by animateFloatAsState(
            targetValue   = if (isOn) 17.dp.value else 1.dp.value,
            animationSpec = tween(200),
            label         = "knobOffset",
        )
        Box(
            modifier = Modifier
                .padding(start = knobOffset.dp, top = 1.dp)
                .size(18.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(Color.White),
        )
    }
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
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .graphicsLayer { rotationY = rotation; cameraDistance = 12f * density }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onCardTap,
            ),
    ) {
        // 하단 accent 라인
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                        )
                    )
                ),
        )

        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (card == null) {
                Text(
                    text       = stringResource(R.string.vocab_card_done),
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color      = MaterialTheme.colorScheme.primary,
                )
            } else if (!isShowingBack) {
                Text(
                    text       = card.japanese,
                    fontFamily = NotoSerifJpFamily,
                    fontSize   = 36.sp,
                    fontWeight = FontWeight.Light,
                    color      = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text     = stringResource(R.string.vocab_card_hint),
                    fontSize = 10.sp,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier            = Modifier.graphicsLayer { rotationY = 180f },
                ) {
                    Text(
                        text      = card.reading,
                        fontSize  = 11.sp,
                        color     = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    if (card.koreanReading.isNotBlank()) {
                        Text(
                            text      = card.koreanReading,
                            fontSize  = 11.sp,
                            color     = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text       = card.meaning,
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color      = MaterialTheme.colorScheme.primary,
                        textAlign  = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun NextButton(enabled: Boolean, onClick: () -> Unit) {
    ActionButton(
        text           = stringResource(R.string.my_next_card),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor   = MaterialTheme.colorScheme.onPrimary,
        borderColor    = MaterialTheme.colorScheme.primary,
        enabled        = enabled,
        onClick        = onClick,
        modifier       = Modifier.fillMaxWidth(),
    )
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
    val bgColor by animateColorAsState(
        targetValue = if (enabled) containerColor else MaterialTheme.colorScheme.surface,
        label       = "bg",
    )
    val fgColor by animateColorAsState(
        targetValue = if (enabled) contentColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        label       = "fg",
    )
    val bdColor by animateColorAsState(
        targetValue = if (enabled) borderColor else MaterialTheme.colorScheme.outline,
        label       = "bd",
    )

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
            .padding(vertical = 11.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = fgColor)
    }
}

// ── 단어 목록 아이템 ───────────────────────────────────────────────────────────

@Composable
private fun WordListItem(word: WordUi, onRemove: () -> Unit) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = word.japanese,
                fontFamily = NotoSerifJpFamily,
                fontSize   = 17.sp,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(2.dp))
            val readingLine = buildString {
                append(word.reading)
                if (word.koreanReading.isNotBlank()) append(" · ${word.koreanReading}")
            }
            Text(
                text     = readingLine,
                fontSize = 11.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text     = word.meaning,
                fontSize = 12.sp,
                color    = MaterialTheme.colorScheme.primary,
            )
        }
        Box(
            modifier = Modifier
                .size(28.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onRemove,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.Filled.Bookmark,
                contentDescription = stringResource(R.string.my_bookmark_remove_desc),
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(18.dp),
            )
        }
    }
    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline)
}

// ── 프리미엄 모달 ──────────────────────────────────────────────────────────────

@Composable
private fun PremiumModalContent(onBuy: () -> Unit, onRestore: () -> Unit, onCancel: () -> Unit) {
    Column(
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 28.dp),
    ) {
        Text(
            text       = stringResource(R.string.my_premium_modal_title),
            fontSize   = 15.sp,
            fontWeight = FontWeight.Medium,
            color      = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text       = stringResource(R.string.my_premium_modal_sub),
            fontSize   = 12.sp,
            color      = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 19.sp,
        )
        Spacer(modifier = Modifier.height(14.dp))

        listOf(
            stringResource(R.string.my_premium_feature_1),
            stringResource(R.string.my_premium_feature_2),
            stringResource(R.string.my_premium_feature_3),
        ).forEach { feature ->
            Row(
                modifier          = Modifier.padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text  = "✓",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text     = feature,
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onBuy,
                )
                .padding(vertical = 13.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text       = stringResource(R.string.my_premium_buy),
                fontSize   = 13.sp,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.onPrimary,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onCancel,
                )
                .padding(vertical = 13.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text     = stringResource(R.string.my_premium_cancel),
                fontSize = 13.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text     = stringResource(R.string.my_premium_restore),
            fontSize = 11.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onRestore,
                ),
        )
    }
}

// ── 공통 ─────────────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text          = text.uppercase(),
        fontSize      = 10.sp,
        fontWeight    = FontWeight.Medium,
        color         = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 0.08.sp,
    )
}
