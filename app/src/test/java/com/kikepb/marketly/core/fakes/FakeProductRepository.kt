package com.kikepb.marketly.core.fakes

import com.kikepb.marketly.productlist.domain.model.ProductModel
import com.kikepb.marketly.productlist.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeProductRepository: ProductRepository {
    private val _products = MutableStateFlow<List<ProductModel>>(value = emptyList())

    override fun getProducts(): Flow<List<ProductModel>> = _products.asStateFlow()

    override fun getProductById(productId: String): Flow<ProductModel?> =
        _products.asStateFlow().map { products ->
            products.find { it.id == productId }
        }

    override fun getProductsByIds(ids: Set<String>): Flow<List<ProductModel>> =
        _products.asStateFlow().map { products ->
            products.filter { it.id in ids }
        }

    override suspend fun refreshProducts() {
        // No effect
    }

    fun setProducts(products: List<ProductModel>) { _products.value = products }
}