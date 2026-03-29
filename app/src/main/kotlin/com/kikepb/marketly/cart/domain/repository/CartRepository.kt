package com.kikepb.marketly.cart.domain.repository

import com.kikepb.marketly.cart.domain.model.CartItemModel
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    fun getCartItems(): Flow<List<CartItemModel>>
    suspend fun addToCart(productId: String, quantity: Int)
    suspend fun removeFromCart(productId: String)
    suspend fun updateQuantity(productId: String, quantity: Int)
    suspend fun clearCart()
    suspend fun getCartItemById(productId: String): CartItemModel?
}