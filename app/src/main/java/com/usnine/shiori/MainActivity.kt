package com.usnine.shiori

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.usnine.shiori.presentation.feature.sample.SampleScreen
import com.usnine.shiori.ui.theme.ShioriTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShioriTheme {
                SampleScreen()
            }
        }
    }
}