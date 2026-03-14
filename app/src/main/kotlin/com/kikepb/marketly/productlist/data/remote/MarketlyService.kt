package com.kikepb.marketly.productlist.data.remote

import com.kikepb.marketly.productlist.data.remote.dto.ProductsResponseDto
import com.kikepb.marketly.productlist.data.remote.dto.PromotionsResponseDto
import retrofit2.http.GET

interface MarketlyService {

    @GET("data/products.json")
    suspend fun getProducts(): ProductsResponseDto

    @GET("data/promotions.json")
    suspend fun getPromotions(): PromotionsResponseDto
}