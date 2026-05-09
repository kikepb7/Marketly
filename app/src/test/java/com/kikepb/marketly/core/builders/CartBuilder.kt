package com.kikepb.marketly.core.builders

import com.kikepb.marketly.cart.domain.model.CartItemModel

class CartBuilder {

    private var productId = "product-1"
    private var quantity = 1

    fun withProductId(productId: String) = apply { this.productId = productId }
    fun withQuantity(quantity: Int) = apply { this.quantity = quantity }

    fun build() = CartItemModel(
        productId = productId,
        quantity = quantity
    )
}

fun cartItem(block: CartBuilder.() -> Unit = {}) = CartBuilder().apply(block).build()