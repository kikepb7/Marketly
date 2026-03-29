package com.kikepb.marketly.cart.data.repository

import com.kikepb.marketly.cart.data.mapper.toCartItemEntity
import com.kikepb.marketly.cart.data.mapper.toCartItemModel
import com.kikepb.marketly.cart.domain.model.CartItemModel
import com.kikepb.marketly.cart.domain.repository.CartRepository
import com.kikepb.marketly.core.domain.model.AppError.NotFoundError
import com.kikepb.marketly.productlist.data.local.LocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource
) : CartRepository {
    override fun getCartItems(): Flow<List<CartItemModel>> =
        localDataSource.getAllCartItems()
            .map { entities -> entities.map { it.toCartItemModel() }}

    override suspend fun addToCart(productId: String, quantity: Int) {
        val existingItem = localDataSource.getCartItemById(productId = productId)
        if (existingItem != null) {
            val newQuantity = existingItem.quantity + quantity
            localDataSource.updateCartItem(cartItemEntity = existingItem.copy(quantity = newQuantity))
        } else {
            localDataSource.insertCartItem(cartItemEntity = CartItemModel(productId = productId, quantity = quantity).toCartItemEntity())
        }
    }

    override suspend fun removeFromCart(productId: String) {
        val cartItemEntity = localDataSource.getCartItemById(productId = productId) ?: throw NotFoundError
        localDataSource.deleteCartItem(cartItemEntity = cartItemEntity)
    }

    override suspend fun updateQuantity(productId: String, quantity: Int) {
        val cartItemEntity = localDataSource.getCartItemById(productId = productId) ?: throw NotFoundError
        localDataSource.updateCartItem(cartItemEntity = cartItemEntity.copy(quantity = quantity))
    }

    override suspend fun clearCart() { localDataSource.clearCart() }

    override suspend fun getCartItemById(productId: String): CartItemModel? = localDataSource.getCartItemById(productId = productId)?.toCartItemModel()
}