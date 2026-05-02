package com.kikepb.marketly.productlist.domain.usecase

import com.kikepb.marketly.cart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCartItemCountUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    operator fun invoke(): Flow<Int> = cartRepository.getCartItems()
        .map { items ->
            items.sumOf { it.quantity }
        }
}