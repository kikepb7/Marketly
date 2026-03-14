package com.kikepb.marketly.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.kikepb.marketly.core.presentation.navigation.Screen.Cart
import com.kikepb.marketly.core.presentation.navigation.Screen.ProductDetail
import com.kikepb.marketly.core.presentation.navigation.Screen.ProductList
import com.kikepb.marketly.core.presentation.navigation.Screen.Setting

@Composable
fun NavGraph() {
    val backStack = rememberNavBackStack(ProductList)
    val entries = entryProvider<NavKey> {
        entry<ProductList> {}
        entry<Cart> {}
        entry<Setting> {}
        entry<ProductDetail> {}
    }

    NavDisplay(backStack = backStack, entryProvider = entries, onBack = { backStack.removeLastOrNull() })
}