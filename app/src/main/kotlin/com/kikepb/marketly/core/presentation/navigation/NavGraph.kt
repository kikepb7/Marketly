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
import com.kikepb.marketly.detail.presentation.ProductDetailRoot
import com.kikepb.marketly.productlist.presentation.ProductListRoot
import com.kikepb.marketly.settings.presentation.SettingsScreenRoot

@Composable
fun NavGraph() {
    val backStack = rememberNavBackStack(ProductList)
    val entries = entryProvider<NavKey> {
        entry<ProductList> {
            ProductListRoot(
                navigateToSettings = { backStack.add(element = Setting) },
                navigateToProductDetail = { backStack.add(element = ProductDetail(productId = it)) }
            )
        }
        entry<Cart> {}
        entry<Setting> { SettingsScreenRoot(onBack = { backStack.removeLastOrNull() }) }
        entry<ProductDetail> {
            ProductDetailRoot(
                productId = it.productId,
                onBack = { backStack.removeLastOrNull() }
            )
        }
    }

    NavDisplay(backStack = backStack, entryProvider = entries, onBack = { backStack.removeLastOrNull() })
}