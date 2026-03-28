package com.kikepb.marketly.core.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import com.kikepb.marketly.productlist.domain.model.ProductModel

@Composable
fun MarketlyAddToCartButton(
    modifier: Modifier = Modifier,
    product: ProductModel?,
    isLoading: Boolean,
    addProductToCart: () -> Unit
) {
    product?.let {
        if (it.stock > 0) MarketlyAddToCartButtonWithStock(modifier = modifier, product = it, isLoading = isLoading, addProductToCart = addProductToCart)
        else MarketlyAddToCartButtonNoStock(modifier = modifier)
    }
}

@Composable
fun MarketlyAddToCartButtonWithStock(
    modifier: Modifier = Modifier,
    product: ProductModel,
    isLoading: Boolean,
    addProductToCart: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        tonalElevation = 2.dp
    ) {
        Box(modifier = Modifier.padding(all = 16.dp)) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = addProductToCart,
                enabled = !isLoading,
                shape = RoundedCornerShape(size = 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(size = 20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(text = "Agregar al carrito", fontWeight = Bold)
            }
        }
    }
}

@Composable
fun MarketlyAddToCartButtonNoStock(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        tonalElevation = 2.dp
    ) {
        Box(modifier = Modifier.padding(all = 16.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {},
            enabled = false,
            shape = RoundedCornerShape(size = 12.dp),
            colors = ButtonDefaults.buttonColors(disabledContainerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(size = 20.dp)
            )
            Spacer(modifier = Modifier.width(width = 8.dp))
            Text(text = "Sin stock disponible", fontWeight = Bold)
        }
    }
}