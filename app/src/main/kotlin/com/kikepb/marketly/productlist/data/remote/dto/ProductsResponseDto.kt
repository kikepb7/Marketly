package com.kikepb.marketly.productlist.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductsResponseDto(
    @SerialName("products")
    val products: List<ProductResponseDto>
)

@Serializable
data class ProductResponseDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("priceCents")
    val priceCents: Int? = null,
    @SerialName("category")
    val category: String? = null,
    @SerialName("stock")
    val stock: Int? = null,
    @SerialName("imageUrl")
    val imageUrl: String? = null
)