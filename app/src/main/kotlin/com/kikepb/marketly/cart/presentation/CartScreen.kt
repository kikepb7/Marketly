package com.kikepb.marketly.cart.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kikepb.marketly.cart.presentation.CartEvent.ShowMessage
import com.kikepb.marketly.cart.presentation.CartUiState.Error
import com.kikepb.marketly.cart.presentation.CartUiState.Loading
import com.kikepb.marketly.cart.presentation.CartUiState.Success
import com.kikepb.marketly.cart.presentation.components.MarketlyCartItemCard
import com.kikepb.marketly.cart.presentation.components.MarketlyCartSummaryCard
import com.kikepb.marketly.core.presentation.components.MarketlyTopAppBar
import java.text.NumberFormat
import java.util.Currency.getInstance

@Composable
fun CartRoot(
    cartViewModel: CartViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by cartViewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        cartViewModel.event.collect { event ->
            when (event) {
                is ShowMessage -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    CartScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        loadCart = cartViewModel::loadCart,
        onIncreaseQuantity = { productId, currentQuantity -> cartViewModel.increaseQuantity(productId = productId, currentQuantity = currentQuantity) },
        onDecreaseQuantity = { productId, currentQuantity -> cartViewModel.reduceQuantity(productId = productId, currentQuantity = currentQuantity) },
        onRemoveProduct = { productId -> cartViewModel.removeFromCart(productId = productId) }
    )
}

@Composable
fun CartScreen(
    state: CartUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    loadCart: () -> Unit,
    onIncreaseQuantity: (String, Int) -> Unit,
    onDecreaseQuantity: (String, Int) -> Unit,
    onRemoveProduct: (String) -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { MarketlyTopAppBar(title = "Carrito", onBackSelected = onBack) },
    ) { paddingValues ->

        when (state) {
            Loading -> CartLoadingScreen(modifier = Modifier.fillMaxSize().padding(paddingValues))
            is Error -> {
                CartErrorScreen(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    state = state,
                    onRetrySelected = loadCart
                )
            }
            is Success -> {
                CartSuccessScreen(
                    modifier = Modifier.padding(paddingValues),
                    state = state,
                    onIncreaseQuantity = { productId, quantity -> onIncreaseQuantity(productId, quantity) },
                    onDecreaseQuantity = { productId, quantity -> onDecreaseQuantity(productId, quantity) },
                    onRemoveProduct = { productId -> onRemoveProduct(productId) }
                )
            }
        }
    }
}

@Composable
fun CartSuccessScreen(
    modifier: Modifier,
    state: Success,
    onIncreaseQuantity: (String, Int) -> Unit,
    onDecreaseQuantity: (String, Int) -> Unit,
    onRemoveProduct: (String) -> Unit
) {
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance().apply {
            currency = getInstance("USD")
        }
    }

    Column(modifier.padding(all = 16.dp)) {
        AnimatedContent(state.cartItems.isEmpty()) { isEmpty ->
            if (isEmpty) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(Modifier.height(54.dp))
                    Text(
                        text = "🛒",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Text(
                        text = "Tu carrito está vacío",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Agrega productos para comenzar",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            } else {
                LazyColumn(
                    Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.cartItems, key = { it.cartItem.productId }) { itemWithProduct ->
                        MarketlyCartItemCard(
                            modifier = Modifier.animateItem(),
                            itemWithProduct = itemWithProduct,
                            currencyFormatter = currencyFormatter,
                            onIncreaseQuantity = { productId, quantity ->
                                onIncreaseQuantity(productId, quantity)
                            },
                            onDecreaseQuantity = { productId, quantity ->
                                onDecreaseQuantity(productId, quantity)
                            },
                            onRemove = { id -> onRemoveProduct(id) })
                    }
                }
            }
        }

        if (state.cartItems.isNotEmpty() && state.summary != null) {
            MarketlyCartSummaryCard(
                modifier = Modifier.fillMaxWidth().padding(all = 16.dp),
                summary = state.summary,
                currencyFormatter = currencyFormatter
            )
        }
    }
}

@Composable
fun CartErrorScreen(
    modifier: Modifier = Modifier,
    state: Error,
    onRetrySelected: () -> Unit
) {
    Column(
        modifier = modifier.padding(all = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error: ${state.message}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = { onRetrySelected() }) {
            Text(text = "Reintentar")
        }
    }
}

@Composable
fun CartLoadingScreen(modifier: Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
