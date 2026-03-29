package com.kikepb.marketly.cart.data.mapper

import com.kikepb.marketly.cart.data.local.database.entity.CartItemEntity
import com.kikepb.marketly.cart.domain.model.CartItemModel

fun CartItemEntity.toCartItemModel(): CartItemModel =
    CartItemModel(
        productId = productId,
        quantity = quantity
    )

fun CartItemModel.toCartItemEntity(): CartItemEntity =
    CartItemEntity(
        productId = productId,
        quantity = quantity
    )