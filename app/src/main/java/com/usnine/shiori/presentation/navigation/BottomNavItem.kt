package com.usnine.shiori.presentation.navigation

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: String,
) {
    data object Home     : BottomNavItem(route = "home",     label = "홈",   icon = "🏠")
    data object Kana     : BottomNavItem(route = "kana",     label = "글자", icon = "あ")
    data object Sentence : BottomNavItem(route = "sentence", label = "문장", icon = "文")
    data object My       : BottomNavItem(route = "my",       label = "마이", icon = "マイ")

    companion object {
        val items = listOf(Home, Kana, Sentence, My)
    }
}
