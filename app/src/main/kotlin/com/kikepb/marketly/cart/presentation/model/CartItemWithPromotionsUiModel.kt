package com.kikepb.marketly.cart.presentation.model

import com.kikepb.marketly.cart.domain.model.CartItemModel
import com.kikepb.marketly.productlist.domain.model.ProductWithPromotionModel

data class CartItemWithPromotionsUiModel(
    val cartItem: CartItemModel,
    val product: ProductWithPromotionModel
)
