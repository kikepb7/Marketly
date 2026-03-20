package com.kikepb.marketly.productlist.domain.usecase

import com.kikepb.marketly.productlist.domain.model.ProductModel
import com.kikepb.marketly.productlist.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(): Flow<List<ProductModel>> = productRepository.getProducts()
}