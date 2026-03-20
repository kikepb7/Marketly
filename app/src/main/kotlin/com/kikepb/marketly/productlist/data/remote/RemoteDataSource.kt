package com.kikepb.marketly.productlist.data.remote

import com.kikepb.marketly.productlist.data.remote.dto.ProductResponseDto
import com.kikepb.marketly.productlist.data.remote.dto.PromotionResponseDto
import com.kikepb.marketly.productlist.data.mapper.mapToDomainError
import javax.inject.Inject

class RemoteDataSource
@Inject constructor(val marketlyService: MarketlyService) {

    suspend fun getProducts(): Result<List<ProductResponseDto>> {
        return try {
            val response = marketlyService.getProducts()
            Result.success(value = response.products)
        } catch (e: Exception) {
            Result.failure(exception = mapToDomainError(e = e))
        }
    }

    suspend fun getPromotions(): Result<List<PromotionResponseDto>> {
        return try {
            val response = marketlyService.getPromotions()
            Result.success(value = response.promotions)
        } catch (e: Exception) {
            Result.failure(exception = mapToDomainError(e = e))
        }
    }
}