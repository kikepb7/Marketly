package com.kikepb.marketly.productlist.domain.repository

import com.kikepb.marketly.productlist.domain.model.ProductModel
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<ProductModel>>
    fun getProductById(productId: String): Flow<ProductModel?>
    fun getProductsByIds(ids: Set<String>): Flow<List<ProductModel>>
    suspend fun refreshProducts()
}