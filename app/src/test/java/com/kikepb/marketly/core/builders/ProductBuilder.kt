package com.kikepb.marketly.core.builders

import com.kikepb.marketly.productlist.domain.model.ProductModel

class ProductBuilder {
    private var id = "product-1"
    private var name = "Product name test"
    private var description = "Product description test"
    private var price = 10.0
    private var category = "Product category test"
    private var stock = 10
    private var imageUrl: String? = null

    fun withId(id: String) = apply { this.id = id }
    fun withName(name: String) = apply { this.name = name }
    fun withDescription(description: String) = apply { this.description = description }
    fun withPrice(price: Double) = apply { this.price = price }
    fun withCategory(category: String) = apply { this.category = category }
    fun withStock(stock: Int) = apply { this.stock = stock }
    fun withImageUrl(imageUrl: String) = apply { this.imageUrl = imageUrl }

    fun build() = ProductModel(
        id = id,
        name = name,
        description = description,
        price = price,
        category = category,
        stock = stock,
        imageUrl = imageUrl
    )
}

fun product(block: ProductBuilder.() -> Unit = {}) = ProductBuilder().apply(block = block).build()