package com.kikepb.marketly.detail.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.kikepb.marketly.detail.presentation.components.MarketlyAddToCartButton
import com.kikepb.marketly.R.drawable as RD
import com.kikepb.marketly.core.presentation.components.MarketlyTopAppBar
import com.kikepb.marketly.productlist.domain.model.ProductPromotion.BuyXPayY
import com.kikepb.marketly.productlist.domain.model.ProductPromotion.Percent

@Composable
fun ProductDetailRoot(
    productDetailViewModel: DetailViewModel = hiltViewModel(),
    productId: String,
    onBack: () -> Unit
) {
    val state by productDetailViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = productId) {
        productDetailViewModel.loadProduct(productId = productId)
    }

    ProductDetailScreen(
        state = state,
        onBack = onBack,
        addProductToCart = productDetailViewModel::addProductToCart
    )
}

@Composable
fun ProductDetailScreen(state: ProductDetailUiState, onBack: () -> Unit, addProductToCart: () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = { MarketlyTopAppBar(title = state.item?.product?.name.orEmpty(), onBackSelected = onBack) },
        bottomBar = { MarketlyAddToCartButton(product = state.item?.product, isLoading = state.isLoading, addProductToCart = addProductToCart) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(all = 16.dp)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                state.item?.let {
                    val product = it.product
                    val promotion = it.promotion
                    val hasStock = product.stock > 0
                    val stockContainerColor =
                        if (hasStock) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.errorContainer
                    val stockContentColor =
                        if (hasStock) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onErrorContainer
                    val discountedPrice = when (promotion) {
                        is Percent -> promotion.discountedPrice
                        is BuyXPayY -> null
                        null -> null
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(state = rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(space = 16.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            shape = RoundedCornerShape(size = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(all = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(space = 16.dp)
                            ) {
                                AsyncImage(
                                    model = product.imageUrl,
                                    contentDescription = product.name,
                                    contentScale = Crop,
                                    placeholder = painterResource(RD.ic_launcher_foreground),
                                    error = painterResource(RD.ic_launcher_background),
                                )

                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = Bold
                                )

                                Surface(
                                    shape = RoundedCornerShape(size = 8.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Text(
                                        text = product.category,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }

                                if (product.description.isNotBlank()) {
                                    Text(
                                        text = product.description,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                HorizontalDivider()

                                if (discountedPrice != null) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(space = 12.dp)
                                    ) {
                                        Text(
                                            text = product.price.toString(),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textDecoration = TextDecoration.LineThrough
                                        )
                                        Text(
                                            text = discountedPrice.toString(),
                                            style = MaterialTheme.typography.displaySmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = Bold
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(size = 8.dp),
                                        color = MaterialTheme.colorScheme.errorContainer
                                    ) {
                                        Text(
                                            text = "${(promotion as Percent).percent.toInt()}% OFF ",
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = Bold,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                } else {
                                    Text(
                                        text = product.price.toString(),
                                        style = MaterialTheme.typography.displaySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = Bold
                                    )
                                }

                                if (promotion is BuyXPayY) {
                                    Surface(
                                        shape = RoundedCornerShape(size = 8.dp),
                                        color = MaterialTheme.colorScheme.errorContainer
                                    ) {
                                        Text(
                                            text = "PROMOTION: ${promotion.label}",
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = Bold,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }

                                HorizontalDivider()

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Stock disponible",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(size = 12.dp),
                                        color = stockContainerColor
                                    ) {
                                        Text(
                                            text = if (hasStock) "${product.stock} unidades" else "Sin stock",
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = Bold,
                                            color = stockContentColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}