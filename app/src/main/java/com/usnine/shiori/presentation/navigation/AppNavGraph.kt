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
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.usnine.shiori.presentation.ad.AdMobEntryPoint
import dagger.hilt.android.EntryPointAccessors
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.usnine.shiori.presentation.feature.home.HomeScreen
import com.usnine.shiori.presentation.feature.kana.KanaScreen
import com.usnine.shiori.presentation.feature.kana.KanaTab
import com.usnine.shiori.presentation.feature.my.MyScreen
import com.usnine.shiori.presentation.feature.quiz.QuizScreen
import com.usnine.shiori.presentation.feature.sentence.SentenceScreen
import com.usnine.shiori.presentation.feature.wordquiz.WordQuizFlow

@Composable
fun AppNavGraph(isPremium: Boolean = false) {
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
                    (context as? Activity)?.let { adMobManager.incrementTabCount(it, isPremium) }
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
            composable(BottomNavItem.Home.route) { HomeScreen() }
            composable(BottomNavItem.Kana.route)     {
                KanaScreen(onNavigateToQuiz = { tab, selected ->
                    val encoded = Uri.encode(selected.joinToString(","))
                    navController.navigate("quiz/${tab.name}?selected=$encoded")
                })
            }
            composable(BottomNavItem.Sentence.route) {
                SentenceScreen(onNavigateToWordQuiz = { navController.navigate("word_quiz") })
            }
            composable("word_quiz") {
                WordQuizFlow(
                    isPremium      = isPremium,
                    onNavigateBack = { navController.popBackStack() },
                )
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
                    isPremium      = isPremium,
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
