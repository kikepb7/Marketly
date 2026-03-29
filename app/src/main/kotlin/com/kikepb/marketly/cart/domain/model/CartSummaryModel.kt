package com.kikepb.marketly.cart.domain.model

data class CartSummaryModel(
    val subtotal: Double,
    val totalDiscount: Double,
    val finalTotal: Double
)
