package com.kikepb.marketly.core.fakes

import com.kikepb.marketly.cart.domain.model.CartItemModel
import com.kikepb.marketly.cart.domain.repository.CartRepository
import com.kikepb.marketly.core.domain.model.AppError.NotFoundError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.collections.emptyList

class FakeCartRepository: CartRepository {
    private val _cartItems = MutableStateFlow<List<CartItemModel>>(value = emptyList())

    override fun getCartItems(): Flow<List<CartItemModel>> = _cartItems.asStateFlow()

    override suspend fun addToCart(productId: String, quantity: Int) {
        val currentItems = _cartItems.value.toMutableList()
        val existingIndex = currentItems.indexOfFirst { it.productId == productId }

        if (existingIndex >= 0) {
            val item = currentItems[existingIndex]
            currentItems[existingIndex] = item.copy(quantity = item.quantity + quantity)
        }
        else currentItems.add(CartItemModel(productId = productId, quantity =  quantity))

        _cartItems.value = currentItems
    }

    override suspend fun removeFromCart(productId: String) {
        val currentItems = _cartItems.value.toMutableList()
        val existingIndex = currentItems.indexOfFirst { it.productId == productId }

        if (existingIndex >= 0) {
            currentItems.removeAt(index = existingIndex)
            _cartItems.value = currentItems
        }
        else throw NotFoundError
    }

    override suspend fun updateQuantity(productId: String, quantity: Int) {
        val currentItems = _cartItems.value.toMutableList()
        val existingIndex = currentItems.indexOfFirst { it.productId == productId }

        if (existingIndex >= 0) {
            currentItems[existingIndex] = currentItems[existingIndex].copy(quantity = quantity)
            _cartItems.value = currentItems
        }
        else throw NotFoundError
    }

    override suspend fun clearCart() {_cartItems.value = emptyList() }

    override suspend fun getCartItemById(productId: String): CartItemModel? =
        _cartItems.value.find { it.productId == productId }
}