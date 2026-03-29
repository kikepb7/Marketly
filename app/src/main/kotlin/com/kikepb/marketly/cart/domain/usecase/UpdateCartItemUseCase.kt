package com.kikepb.marketly.cart.domain.usecase

import com.kikepb.marketly.cart.domain.repository.CartRepository
import com.kikepb.marketly.core.domain.model.AppError
import com.kikepb.marketly.core.domain.model.AppError.NotFoundError
import com.kikepb.marketly.core.domain.model.AppError.Validation.QuantityMustBePositive
import com.kikepb.marketly.productlist.domain.repository.ProductRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class UpdateCartItemUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(productId: String, quantity: Int) {
        if (quantity < 0) throw QuantityMustBePositive

        if (quantity == 0) {
            cartRepository.removeFromCart(productId = productId)
            return
        }

        val product = productRepository.getProductById(productId = productId).firstOrNull() ?: throw NotFoundError

        if (quantity > product.stock) throw AppError.Validation.InsufficientStock(available = product.stock)

        cartRepository.updateQuantity(productId = productId, quantity = quantity)
    }
}