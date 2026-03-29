package com.kikepb.marketly.productlist.data.local

import com.kikepb.marketly.cart.data.local.database.dao.CartItemDao
import com.kikepb.marketly.cart.data.local.database.entity.CartItemEntity
import com.kikepb.marketly.productlist.data.local.database.dao.ProductDao
import com.kikepb.marketly.productlist.data.local.database.dao.PromotionDao
import com.kikepb.marketly.productlist.data.local.database.entity.ProductEntity
import com.kikepb.marketly.productlist.data.local.database.entity.PromotionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val productDao: ProductDao,
    private val promotionDao: PromotionDao,
    private val cartItemDao: CartItemDao
) {

    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()

    fun getProductById(productId: String): Flow<ProductEntity?> = productDao.getProductById(id = productId)

    fun getProductsByIds(productsIds: Set<String>): Flow<List<ProductEntity>> {
        if (productsIds.isEmpty()) return flowOf(value = emptyList())

        return productDao.getProductsByIds(productsIds = productsIds.toList())
    }

    fun getAllPromotions(): Flow<List<PromotionEntity>> = promotionDao.getAllPromotions()

    fun getAllCartItems(): Flow<List<CartItemEntity>> = cartItemDao.getAllCartItems()

    suspend fun saveProducts(productsEntity: List<ProductEntity>) = productDao.replaceAll(products = productsEntity)

    suspend fun savePromotions(promotionsEntity: List<PromotionEntity>) = promotionDao.replaceAll(promotions = promotionsEntity)

    suspend fun getCartItemById(productId: String) = cartItemDao.getCartItemById(productId = productId)

    suspend fun updateCartItem(cartItemEntity: CartItemEntity) =
        try {
            cartItemDao.updateCartItem(cartItemEntity = cartItemEntity)
            Result.success(value = Unit)
        } catch (e: Exception) {
            Result.failure(exception = e)
        }

    suspend fun deleteCartItem(cartItemEntity: CartItemEntity) =
        try {
            cartItemDao.deleteCartItem(cartItemEntity = cartItemEntity)
            Result.success(value = Unit)
        } catch (e: Exception) {
            Result.failure(exception = e)
        }

    suspend fun clearCart() =
        try {
            cartItemDao.clearCart()
            Result.success(value = Unit)
        } catch (e: Exception) {
            Result.failure(exception = e)
        }

    suspend fun insertCartItem(cartItemEntity: CartItemEntity) =
        try {
            cartItemDao.insertCartItem(cartItemEntity = cartItemEntity)
            Result.success(value = Unit)
        } catch (e: Exception) {
            Result.failure(exception = e)
        }
}