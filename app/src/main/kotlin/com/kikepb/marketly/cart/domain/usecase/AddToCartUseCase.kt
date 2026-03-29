package com.kikepb.marketly.cart.domain.usecase

import com.kikepb.marketly.cart.domain.repository.CartRepository
import com.kikepb.marketly.core.domain.model.AppError.NotFoundError
import com.kikepb.marketly.core.domain.model.AppError.Validation.InsufficientStock
import com.kikepb.marketly.core.domain.model.AppError.Validation.QuantityMustBePositive
import com.kikepb.marketly.productlist.domain.repository.ProductRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(productId: String, quantity: Int = 1) {
        if (quantity <= 0) throw QuantityMustBePositive

        val product = productRepository.getProductById(productId = productId).firstOrNull() ?: throw NotFoundError

        val existingItem = cartRepository.getCartItemById(productId = productId)
        val newQuantity = (existingItem?.quantity ?: 0) + quantity

        if (newQuantity > product.stock) throw InsufficientStock(available = product.stock)

        cartRepository.addToCart(productId = productId, quantity = quantity)
    }
}