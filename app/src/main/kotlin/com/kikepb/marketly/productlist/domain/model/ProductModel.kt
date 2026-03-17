package com.kikepb.marketly.productlist.domain.model

data class ProductModel(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val stock: Int,
    val imageUrl: String?
)
