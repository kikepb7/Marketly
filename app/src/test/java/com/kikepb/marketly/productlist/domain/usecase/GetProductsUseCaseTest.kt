package com.kikepb.marketly.productlist.domain.usecase

import com.kikepb.marketly.core.builders.product
import com.kikepb.marketly.core.builders.promotion
import com.kikepb.marketly.core.fakes.FakeClockRepository
import com.kikepb.marketly.core.fakes.FakeProductRepository
import com.kikepb.marketly.core.fakes.FakePromotionRepository
import com.kikepb.marketly.core.fakes.FakeSettingsRepository
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Instant

class GetProductsUseCaseTest {

    private fun useCase(
        products: FakeProductRepository = FakeProductRepository(),
        promotions: FakePromotionRepository = FakePromotionRepository(),
        settings: FakeSettingsRepository = FakeSettingsRepository(),
        clock: FakeClockRepository = FakeClockRepository()
    ) = GetProductsUseCase(
        productRepository = products,
        promotionRepository = promotions,
        getPromotionForProductUseCase = GetPromotionForProductUseCase(),
        settingsRepository = settings,
        clockRepository = clock
    )

    @Test
    fun `GIVEN promotion ending now WHEN invoke THEN it should be included`() = runTest {
        // GIVEN
        val now = Instant.parse("2026-04-03T10:00:00Z")
        val clock = FakeClockRepository().apply { setTime(time = now) }
        val productId = "product-id"
        val product = product {
            withId(id = productId)
        }
        val promotion = promotion {
            withProductIds(productIds = listOf(productId))
            withStartTime(startTime = now.minusSeconds(60))
            withEndTime(endTime = now)
        }

        val productRepository = FakeProductRepository().apply { setProducts(products = listOf(product)) }
        val promotionRepository = FakePromotionRepository().apply { setPromotions(promotions = listOf(promotion)) }

        // WHEN
        val result = (useCase(products = productRepository, promotions = promotionRepository, clock = clock)()).first()

        // THEN
        assertNotNull(result.first())
    }

    @Test
    fun `GIVEN active promotion WHEN time advances THEN promotion should no be longer be returned`() = runTest {
        // GIVEN
        val now = Instant.parse("2026-04-03T10:00:00Z")
        val clock = FakeClockRepository().apply { setTime(time = now) }
        val productId = "product-id"
        val product = product {
            withId(id = productId)
        }
        val promotion = promotion {
            withProductIds(productIds = listOf(productId))
            withStartTime(startTime = now)
            withEndTime(endTime = now.plusSeconds(5))
        }

        val productRepository = FakeProductRepository().apply { setProducts(products = listOf(product)) }
        val promotionRepository = FakePromotionRepository().apply { setPromotions(promotions = listOf(promotion)) }

        // WHEN
        val firstResult = (useCase(products = productRepository, promotions = promotionRepository, clock = clock)()).first()
        clock.advanceTime(seconds = 6)
        val secondResult = (useCase(products = productRepository, promotions = promotionRepository, clock = clock)()).first()

        // THEN
        assertNotNull(firstResult.first().promotion)
        assertNull(secondResult.first().promotion)
    }

    @Test
    fun `GIVEN inStockOnly enabled WHEN product goes out of stock THEN it should be filtered`() = runTest{
        // GIVEN
        val productId = "product-id"
        val product = product {
            withId(id = productId)
            withStock(stock = 0)
        }
        val settings = FakeSettingsRepository().apply { setInStockOnly(value = true) }
        val productRepository = FakeProductRepository().apply { setProducts(products = listOf(product)) }

        val useCase = useCase(products = productRepository, settings = settings)

        // WHEN
        val result = useCase().first()

        // THEN
        assertTrue(result.isEmpty())
    }
}