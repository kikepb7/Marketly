package com.kikepb.marketly.detail.domain.usecase

import com.kikepb.marketly.core.builders.product
import com.kikepb.marketly.core.builders.promotion
import com.kikepb.marketly.core.fakes.FakeClockRepository
import com.kikepb.marketly.core.fakes.FakeProductRepository
import com.kikepb.marketly.core.fakes.FakePromotionRepository
import com.kikepb.marketly.productlist.domain.model.PromotionType.PERCENT
import com.kikepb.marketly.productlist.domain.usecase.GetPromotionForProductUseCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Instant

class GetProductDetailWithPromotionUseCaseTest {

    private lateinit var productRepository: FakeProductRepository
    private lateinit var promotionsRepository: FakePromotionRepository
    private lateinit var clock: FakeClockRepository

    @Before
    fun setUp() {
        clock = FakeClockRepository().apply { setTime(time = Instant.parse("2026-04-03T10:00:00Z")) }
        productRepository = FakeProductRepository()
        promotionsRepository = FakePromotionRepository()
    }

    private fun useCase() = GetProductDetailWithPromotionUseCase(
        productRepository = productRepository,
        promotionRepository = promotionsRepository,
        getPromotionForProductUseCase = GetPromotionForProductUseCase(),
        clock = clock
    )

    @Test
    fun `GIVEN product with active promotion WHEN invoke THEN returns product with promotion`() = runTest {
        // GIVEN
        val now = clock.now()
        val product = product { withId(id = "productId") }
        val promotion = promotion {
            withProductIds(listOf("productId"))
            withType(type = PERCENT)
            withValue(value = 10.0)
            withStartTime(startTime = now.minusSeconds(10))
            withEndTime(endTime = now.plusSeconds(20))
        }

        // WHEN
        productRepository.setProducts(products = listOf(product))
        promotionsRepository.setPromotions(promotions = listOf(promotion))

        val result = useCase()(productId = product.id).first()

        // THEN
        assertNotNull(result)
        assertNotNull(result?.promotion)
        assertEquals(product.id, result?.product?.id)
    }

    @Test
    fun `GIVEN expired promotion WHEN invoke THEN returns product without promotion`() = runTest {
        // GIVEN
        val now = clock.now()
        val product = product { withId(id = "productId") }
        val promotion = promotion {
            withProductIds(listOf("productId"))
            withType(type = PERCENT)
            withValue(value = 10.0)
            withStartTime(startTime = now.minusSeconds(10))
            withEndTime(endTime = now.minusSeconds(5))
        }

        // WHEN
        productRepository.setProducts(products = listOf(product))
        promotionsRepository.setPromotions(promotions = listOf(promotion))

        val result = useCase()(productId = product.id).first()

        // THEN
        assertNotNull(result?.product)
        assertNull(result?.promotion)
    }

    @Test
    fun `GIVEN non existing product id WHEN invokes THEN returns null`() = runTest {
        // GIVEN
        productRepository.setProducts(products = emptyList())

        // WHEN
        val result = useCase()(productId = "productId").first()

        // THEN
        assertNull(result)
    }

    @Test
    fun `GIVEN active promotion WHEN time advances THEN product promotion becomes null`() = runTest {
        // GIVEN
        val product = product { withId(id = "productId"); withName(name = "productName") }
        val now = clock.now()
        val promotion = promotion {
            withProductIds(productIds = listOf("productId"))
            withStartTime(startTime = now.minusSeconds(10))
            withEndTime(endTime = now.plusSeconds(5))
        }
        productRepository.setProducts(products = listOf(product))
        promotionsRepository.setPromotions(promotions = listOf(promotion))

        // WHEN
        val result = useCase()(productId = "productId")

        // THEN
        assertNotNull(result.first()?.promotion)
        clock.advanceTime(seconds = 6)
        assertNull(result.first()?.promotion)
    }
}