package com.kikepb.marketly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kikepb.marketly.core.domain.model.ThemeModeModel.DARK
import com.kikepb.marketly.core.domain.model.ThemeModeModel.LIGHT
import com.kikepb.marketly.core.domain.model.ThemeModeModel.SYSTEM
import com.kikepb.marketly.core.presentation.navigation.NavGraph
import com.kikepb.marketly.ui.theme.MarketlyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by mainViewModel.themeMode.collectAsStateWithLifecycle(initialValue = SYSTEM)
            val darkTheme = when (themeMode) {
                DARK -> true
                LIGHT -> false
                SYSTEM -> isSystemInDarkTheme()
            }
            MarketlyTheme(darkTheme = darkTheme) {
                NavGraph()
            }
        }
    }
}
