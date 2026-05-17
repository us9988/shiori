package com.usnine.shiori.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Activity
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.usnine.shiori.presentation.ad.AdMobEntryPoint
import dagger.hilt.android.EntryPointAccessors
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.usnine.shiori.data.local.entity.WordLevel
import com.usnine.shiori.presentation.feature.home.HomeScreen
import com.usnine.shiori.presentation.feature.kana.KanaScreen
import com.usnine.shiori.presentation.feature.kana.KanaTab
import com.usnine.shiori.presentation.feature.my.MyScreen
import com.usnine.shiori.presentation.feature.quiz.QuizScreen
import com.usnine.shiori.presentation.feature.sentence.SentenceScreen
import com.usnine.shiori.presentation.feature.wordquiz.WordQuizFlow
import com.usnine.shiori.presentation.feature.wordstudy.WordStudyCardScreen
import com.usnine.shiori.presentation.feature.wordstudy.WordStudyContract
import com.usnine.shiori.presentation.feature.wordstudy.WordStudyListScreen
import com.usnine.shiori.presentation.feature.wordstudy.WordStudyResultScreen
import com.usnine.shiori.presentation.feature.wordstudy.WordStudyViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val context = LocalContext.current
    val adMobManager = remember(context) {
        EntryPointAccessors
            .fromApplication(context.applicationContext, AdMobEntryPoint::class.java)
            .adMobManager()
    }

    Scaffold(
        bottomBar = {
            ShioriNavBar(
                currentRoute = currentDestination?.route,
                onItemClick  = { item ->
                    (context as? Activity)?.let { adMobManager.incrementTabCount(it) }
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState    = true
                    }
                },
            )
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = BottomNavItem.Home.route,
            modifier         = Modifier.padding(innerPadding),
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    onNavigateToWordStudy = { navController.navigate("word_study") },
                )
            }
            composable(BottomNavItem.Kana.route)     {
                KanaScreen(onNavigateToQuiz = { tab, selected ->
                    val encoded = Uri.encode(selected.joinToString(","))
                    navController.navigate("quiz/${tab.name}?selected=$encoded")
                })
            }
            composable(BottomNavItem.Sentence.route) {
                SentenceScreen(
                    onNavigateToWordQuiz      = { navController.navigate("word_quiz") },
                    onNavigateToWordStudyStep = { level, step, isReview ->
                        navController.navigate("word_study/card/${level.name}/$step?isReview=$isReview")
                    },
                )
            }
            composable("word_quiz") {
                WordQuizFlow(
                    onNavigateBack = { navController.popBackStack() },
                )
            }

            // ── Word Study ──────────────────────────────────────────────────
            navigation(
                startDestination = "word_study/list",
                route            = "word_study",
            ) {
                // 1. 단계 목록
                composable("word_study/list") { entry ->
                    val parentEntry = remember(entry) {
                        navController.getBackStackEntry("word_study")
                    }
                    val viewModel: WordStudyViewModel = hiltViewModel(parentEntry)
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    WordStudyListScreen(
                        state   = state,
                        onEvent = { event ->
                            if (event is WordStudyContract.UiEvent.StepTapped) {
                                viewModel.onEvent(event)
                                navController.navigate(
                                    "word_study/card/${state.selectedLevel.name}/${event.step}?isReview=${event.isReview}"
                                )
                            } else {
                                viewModel.onEvent(event)
                            }
                        },
                    )
                }

                // 2. 플래시카드 세션
                composable(
                    route     = "word_study/card/{level}/{step}?isReview={isReview}",
                    arguments = listOf(
                        navArgument("level")    { type = NavType.StringType },
                        navArgument("step")     { type = NavType.IntType },
                        navArgument("isReview") { type = NavType.StringType; defaultValue = "false" },
                    ),
                ) { entry ->
                    val parentEntry = remember(entry) {
                        navController.getBackStackEntry("word_study")
                    }
                    val viewModel: WordStudyViewModel = hiltViewModel(parentEntry)
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    val levelName = entry.arguments?.getString("level") ?: return@composable
                    val step      = entry.arguments?.getInt("step")     ?: return@composable
                    val isReview  = entry.arguments?.getString("isReview") == "true"
                    val level     = runCatching { WordLevel.valueOf(levelName) }
                                        .getOrElse { return@composable }

                    // 세션이 없으면 nav arg로 직접 로드 (딥링크·복원·WordScreen 진입 대응)
                    // isLoading 체크 제거: init의 loadLevelInfoList()와 경쟁 시 LaunchedEffect가
                    // isLoading=true를 보고 StepTapped를 건너뛰어 영구 빈화면이 되는 버그 방지
                    LaunchedEffect(level, step, isReview) {
                        if (state.currentSession.isEmpty()) {
                            viewModel.onEvent(WordStudyContract.UiEvent.LevelSelected(level))
                            viewModel.onEvent(WordStudyContract.UiEvent.StepTapped(step, isReview))
                        }
                    }

                    // Effect 수집 → 결과 화면 이동
                    val cardDestId = entry.destination.id
                    LaunchedEffect(viewModel) {
                        viewModel.effect.collect { effect ->
                            when (effect) {
                                WordStudyContract.UiEffect.ShowInterstitialAd -> {
                                    // 결과 화면으로 먼저 이동 후 광고 표시
                                    viewModel.onEvent(WordStudyContract.UiEvent.SessionFinishConfirmed)
                                }
                                is WordStudyContract.UiEffect.NavigateToResult -> {
                                    val nextStr = effect.nextStep?.toString() ?: ""
                                    navController.navigate(
                                        "word_study/result/${effect.completedCount}?nextStep=$nextStr"
                                    ) {
                                        // 카드 화면을 백스택에서 제거해 결과→뒤로 시 목록으로 이동
                                        popUpTo(cardDestId) { inclusive = true }
                                    }
                                }
                            }
                        }
                    }

                    WordStudyCardScreen(
                        state   = state,
                        onEvent = viewModel::onEvent,
                        onBack  = { navController.popBackStack() },
                    )
                }

                // 3. 결과 화면
                composable(
                    route     = "word_study/result/{completedCount}?nextStep={nextStep}",
                    arguments = listOf(
                        navArgument("completedCount") { type = NavType.IntType },
                        navArgument("nextStep") {
                            type         = NavType.StringType
                            defaultValue = ""
                        },
                    ),
                ) { entry ->
                    val parentEntry = remember(entry) {
                        navController.getBackStackEntry("word_study")
                    }
                    val viewModel: WordStudyViewModel = hiltViewModel(parentEntry)
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    val completedCount = entry.arguments?.getInt("completedCount") ?: 0
                    val nextStep       = entry.arguments?.getString("nextStep")
                                            ?.toIntOrNull()
                    val info = state.levelInfoList.find { it.level == state.selectedLevel }

                    LaunchedEffect(Unit) {
                        adMobManager.showInterstitialAd(context as Activity) {}
                    }

                    WordStudyResultScreen(
                        completedCount      = completedCount,
                        nextStep            = nextStep,
                        level               = state.selectedLevel,
                        currentStep         = state.currentStep,
                        totalCount          = info?.totalCount ?: 0,
                        levelCompletedCount = info?.completedCount ?: 0,
                        onHome = {
                            navController.navigate(BottomNavItem.Home.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        onNextStep = { step ->
                            viewModel.onEvent(WordStudyContract.UiEvent.StepTapped(step))
                            navController.navigate(
                                "word_study/card/${state.selectedLevel.name}/$step?isReview=false"
                            ) {
                                popUpTo("word_study/list") { inclusive = false }
                            }
                        },
                        onRetry = {
                            val step = state.currentStep
                            viewModel.onEvent(WordStudyContract.UiEvent.StepTapped(step, isReview = true))
                            navController.navigate(
                                "word_study/card/${state.selectedLevel.name}/$step?isReview=true"
                            ) {
                                popUpTo("word_study/list") { inclusive = false }
                            }
                        },
                    )
                }
            }

            composable(BottomNavItem.My.route)       { MyScreen() }
            composable(
                route     = "quiz/{tab}?selected={selected}",
                arguments = listOf(
                    navArgument("tab")      { type = NavType.StringType },
                    navArgument("selected") { type = NavType.StringType; defaultValue = "" },
                ),
            ) { backStackEntry ->
                val tabName       = backStackEntry.arguments?.getString("tab") ?: KanaTab.HIRAGANA.name
                val tab           = KanaTab.valueOf(tabName)
                val selectedParam = backStackEntry.arguments?.getString("selected") ?: ""
                val selectedKana  = if (selectedParam.isEmpty()) emptySet()
                                    else selectedParam.split(",").toSet()
                QuizScreen(
                    tab            = tab,
                    selectedKana   = selectedKana,
                    onNavigateBack = { navController.popBackStack() },
                )
            }
        }
    }
}

@Composable
private fun ShioriNavBar(
    currentRoute: String?,
    onItemClick: (BottomNavItem) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest),
    ) {
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(top = 8.dp, bottom = 10.dp),
        ) {
            BottomNavItem.items.forEach { item ->
                val isActive     = currentRoute == item.route
                val contentColor = if (isActive) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.onSurfaceVariant

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null,
                            onClick           = { onItemClick(item) },
                        )
                        .padding(vertical = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 48.dp, height = 28.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0f)
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text       = item.icon,
                            fontSize   = 16.sp,
                            color      = contentColor,
                            lineHeight = 20.sp,
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text       = item.label,
                        fontSize   = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color      = contentColor,
                        lineHeight = 12.sp,
                    )
                }
            }
        }
    }
}
