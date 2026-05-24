package com.kikepb.marketly.core.stubs

import com.kikepb.marketly.productlist.domain.model.ProductModel
import com.kikepb.marketly.productlist.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FailingProductRepositoryStub(private val exception: Throwable) : ProductRepository {

    override fun getProducts(): Flow<List<ProductModel>> = flow { throw exception }

    override fun getProductById(productId: String): Flow<ProductModel?> = flowOf()

    override fun getProductsByIds(ids: Set<String>): Flow<List<ProductModel>> = flowOf()

    override suspend fun refreshProducts() {}
}