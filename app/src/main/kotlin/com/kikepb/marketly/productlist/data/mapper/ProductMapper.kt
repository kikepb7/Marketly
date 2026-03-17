package com.kikepb.marketly.productlist.data.mapper

import com.kikepb.marketly.productlist.data.local.database.entity.ProductEntity
import com.kikepb.marketly.productlist.data.remote.dto.ProductResponseDto
import com.kikepb.marketly.productlist.domain.model.ProductModel

fun ProductResponseDto.toProductEntity(): ProductEntity {
    val finalPrice = priceCents?.div(other = 100.0) ?: 0.0

    return ProductEntity(
        id = id,
        name = name,
        description = description,
        price = finalPrice,
        category = category,
        stock = stock,
        imageUrl = imageUrl
    )
}

fun ProductEntity.toProductModel(): ProductModel? {
    if (category.isNullOrEmpty()) return null

    return ProductModel(
        id = id,
        name = name,
        description = description.orEmpty(),
        price = price,
        category = category,
        stock = stock ?: 0,
        imageUrl = imageUrl
    )
}