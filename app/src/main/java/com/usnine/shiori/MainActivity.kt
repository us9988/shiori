package com.usnine.shiori

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.usnine.shiori.presentation.ad.AdMobManager
import com.usnine.shiori.presentation.analytics.AnalyticsManager
import com.usnine.shiori.presentation.ad.ExitDialog
import com.usnine.shiori.presentation.navigation.AppNavGraph
import com.usnine.shiori.ui.theme.ShioriTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    @Inject lateinit var adMobManager: AdMobManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AnalyticsManager.init(this)
        adMobManager.loadBannerAd()
        adMobManager.loadInterstitialAd()
        setContent {
            val isDarkMode by mainViewModel.isDarkMode.collectAsStateWithLifecycle()
            var showExitDialog by remember { mutableStateOf(false) }

            BackHandler { showExitDialog = true }

            ShioriTheme(darkTheme = isDarkMode) {
                AppNavGraph()
                if (showExitDialog) {
                    ExitDialog(
                        adMobManager = adMobManager,
                        onConfirm    = { finish() },
                        onDismiss    = { showExitDialog = false },
                    )
                }
            }
        }
    }
}
