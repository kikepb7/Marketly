package com.kikepb.marketly.cart.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.kikepb.marketly.cart.presentation.model.CartItemWithPromotionsUiModel
import com.kikepb.marketly.productlist.domain.model.ProductPromotion.BuyXPayY
import com.kikepb.marketly.productlist.domain.model.ProductPromotion.Percent
import java.text.NumberFormat

@Composable
fun MarketlyCartItemCard(
    modifier: Modifier,
    itemWithProduct: CartItemWithPromotionsUiModel,
    currencyFormatter: NumberFormat,
    onIncreaseQuantity: (String, Int) -> Unit,
    onDecreaseQuantity: (String, Int) -> Unit,
    onRemove: (String) -> Unit
) {
    val product = itemWithProduct.item.product
    val promotion = itemWithProduct.item.promotion
    val cartItem = itemWithProduct.cartItem

    val unitPrice = when (promotion) {
        is Percent -> promotion.discountedPrice
        is BuyXPayY -> product.price
        null -> product.price
    }

    val hasDiscount = promotion is Percent
    val itemTotal = unitPrice * cartItem.quantity

    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(key1 = dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd) {
            onRemove(cartItem.productId)
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    SwipeToDismissBox(
        modifier = modifier,
        state = dismissState,
        enableDismissFromEndToStart = false,
        backgroundContent = {
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(size = 16.dp))
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(size = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
                    .padding(all = 8.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .weight(1.5f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(size = 16.dp)),
                    model = product.imageUrl,
                    contentDescription = null,
                    contentScale = Crop
                )
                Spacer(modifier = Modifier.width(width = 24.dp))
                Column(modifier = Modifier.weight(3f), verticalArrangement = Arrangement.spacedBy(space = 8.dp)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
                    ) {
                        if (hasDiscount) {
                            Text(
                                text = currencyFormatter.format(product.price),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough
                            )

                            Text(
                                text = "${currencyFormatter.format(unitPrice)} c/u",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = Bold
                            )
                        } else {
                            Text(
                                text = "${currencyFormatter.format(unitPrice)} c/u",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }


                    Text(
                        "Total: ${currencyFormatter.format(itemTotal)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    MarketlyQuantitySelector(
                        modifier = Modifier
                            .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(size = 8.dp)),
                        quantity = cartItem.quantity.toString(),
                        canDecrease = cartItem.quantity > 1,
                        canIncrease = cartItem.quantity < product.stock,
                        onDecreaseSelected = { onDecreaseQuantity(product.id, cartItem.quantity) },
                        onIncreaseSelected = { onIncreaseQuantity(product.id, cartItem.quantity) },
                    )
                }
            }
        }
    }
}

@Composable
fun MarketlyQuantitySelector(
    modifier: Modifier = Modifier,
    quantity: String,
    canDecrease: Boolean,
    canIncrease: Boolean,
    onDecreaseSelected: () -> Unit,
    onIncreaseSelected: () -> Unit
) {
    Row(
        modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = { onDecreaseSelected() },
            modifier = Modifier.size(36.dp),
            enabled = canDecrease
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = null,
                modifier = modifier.size(size = 20.dp)
            )
        }
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(
                    text = quantity,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = Bold
                )
            }
        }
        IconButton(
            onClick = { onIncreaseSelected() },
            modifier = Modifier.size(36.dp),
            enabled = canIncrease
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = modifier.size(size = 20.dp)
            )
        }
    }
}