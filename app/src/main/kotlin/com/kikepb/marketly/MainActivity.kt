package com.kikepb.marketly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kikepb.marketly.core.presentation.navigation.NavGraph
import com.kikepb.marketly.ui.theme.MarketlyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MarketlyTheme {
                NavGraph()
            }
        }
    }
}
