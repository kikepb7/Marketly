package com.kikepb.marketly.cart.domain.usecase

import com.kikepb.marketly.core.builders.cartItem
import com.kikepb.marketly.core.builders.product
import com.kikepb.marketly.core.builders.promotion
import com.kikepb.marketly.core.fakes.FakeCartRepository
import com.kikepb.marketly.core.fakes.FakeClockRepository
import com.kikepb.marketly.core.fakes.FakeProductRepository
import com.kikepb.marketly.core.fakes.FakePromotionRepository
import com.kikepb.marketly.productlist.domain.model.PromotionType.BUY_X_PAY_Y
import com.kikepb.marketly.productlist.domain.model.PromotionType.PERCENT
import com.kikepb.marketly.productlist.domain.usecase.GetPromotionForProductUseCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Instant

class GetCartSummaryUseCaseTest {

    private lateinit var cartRepository: FakeCartRepository
    private lateinit var productRepository: FakeProductRepository
    private lateinit var promotionsRepository: FakePromotionRepository
    private lateinit var clock: FakeClockRepository

    @Before
    fun setUp() {
        clock = FakeClockRepository().apply { setTime(time = Instant.parse("2026-04-03T10:00:00Z")) }
        cartRepository = FakeCartRepository()
        productRepository = FakeProductRepository()
        promotionsRepository = FakePromotionRepository()
    }

    private fun useCase() = GetCartSummaryUseCase(
        cartRepository = cartRepository,
        productRepository = productRepository,
        promotionRepository = promotionsRepository,
        getPromotionForProductUseCase = GetPromotionForProductUseCase(),
        clock = clock
    )

    @Test
    fun `GIVEN percent promotion WHEN invoke THEN calculate correctly`() = runTest {
        // GIVEN
        val productId = "productId"
        val product = product { withId(id = productId); withPrice(price = 100.0) }
        val promotion = promotion {
            withProductIds(productIds = listOf(productId))
            withType(type = PERCENT)
            withValue(value = 10.0)
            withStartTime(startTime = clock.now().minusSeconds(10))
            withEndTime(endTime = clock.now().plusSeconds(10))
        }
        val cartItem = cartItem { withProductId(productId = productId); withQuantity(quantity = 2) }

        // WHEN
        productRepository.setProducts(products = listOf(product))
        promotionsRepository.setPromotions(promotions = listOf(promotion))
        cartRepository.setCartItems(items = listOf(cartItem))

        val summary = (useCase()()).first()

        // THEN
        assertEquals(180.0, summary.finalTotal)
        assertEquals(20.0, summary.totalDiscount)
        assertEquals(200.0, summary.subtotal)
    }

    @Test
    fun `GIVEN 3 items in 2x1 promotion WHEN invoke THEN only discounts 1 unit`() = runTest {
        // GIVEN
        val productId = "productId"
        val product = product { withId(id = productId); withPrice(price = 100.0) }
        val promotion = promotion {
            withProductIds(productIds = listOf(productId))
            withType(type = BUY_X_PAY_Y)
            withBuyQuantity(buyQuantity = 2)
            withValue(value = 1.0)
            withStartTime(startTime = clock.now().minusSeconds(10))
            withEndTime(endTime = clock.now().plusSeconds(10))
        }
        val cartItem = cartItem { withProductId(productId = productId); withQuantity(quantity = 3) }

        // WHEN
        productRepository.setProducts(products = listOf(product))
        promotionsRepository.setPromotions(promotions = listOf(promotion))
        cartRepository.setCartItems(items = listOf(cartItem))

        val summary = (useCase()()).first()

        // THEN
        assertEquals(300.0, summary.subtotal)
        assertEquals(200.0, summary.finalTotal)
        assertEquals(100.0, summary.totalDiscount)
    }

    @Test
    fun `GIVEN multiple products with different promotions WHEN invoke THEN sums all correctly`() = runTest {
        // GIVEN
        val now = clock.now()
        val product1 = product { withId(id = "product1"); withPrice(price = 100.0) }
        val product2 = product { withId(id = "product2"); withPrice(price = 50.0) }
        val promotionPercent = promotion {
            withProductIds(productIds = listOf("product1"))
            withType(type = PERCENT)
            withValue(value = 10.0)
            withStartTime(startTime = now.minusSeconds(10))
            withEndTime(endTime = now.plusSeconds(10))
        }
        val cart = listOf(
            cartItem { withProductId(productId = "product1"); withQuantity(quantity = 1) },
            cartItem { withProductId(productId = "product2"); withQuantity(quantity = 1) },
        )

        // WHEN
        productRepository.setProducts(products = listOf(product1, product2))
        promotionsRepository.setPromotions(promotions = listOf(promotionPercent))
        cartRepository.setCartItems(items = cart)

        val summary = (useCase()()).first()

        // THEN
        assertEquals(150.0, summary.subtotal)
        assertEquals(140.0, summary.finalTotal)
        assertEquals(10.0, summary.totalDiscount)
    }

    @Test
    fun `GIVEN expired promotion WHEN invoke THEN discount is zero`() = runTest {
        // GIVEN
        val now = clock.now()
        val product1 = product { withId(id = "product1"); withPrice(price = 100.0) }
        val promotionPercent = promotion {
            withProductIds(productIds = listOf("product1"))
            withType(type = PERCENT)
            withValue(value = 10.0)
            withStartTime(startTime = now.minusSeconds(10))
            withEndTime(endTime = now.minusSeconds(5))
        }

        // WHEN
        productRepository.setProducts(products = listOf(product1))
        promotionsRepository.setPromotions(promotions = listOf(promotionPercent))
        cartRepository.setCartItems(items = listOf(cartItem { withProductId(productId = "product1") }))

        val summary = (useCase()()).first()

        // THEN
        assertEquals(0.0, summary.totalDiscount)
        assertEquals(100.0, summary.finalTotal)
    }

    @Test
    fun `GIVEN active promotion WHEN time advances THEN summary update automatically`() = runTest {
        // GIVEN
        val now = clock.now()
        val product1 = product { withId(id = "product1"); withPrice(price = 100.0) }
        val promotionPercent = promotion {
            withProductIds(productIds = listOf("product1"))
            withType(type = PERCENT)
            withValue(value = 10.0)
            withStartTime(startTime = now.minusSeconds(10))
            withEndTime(endTime = now.plusSeconds(5))
        }

        // WHEN
        productRepository.setProducts(products = listOf(product1))
        promotionsRepository.setPromotions(promotions = listOf(promotionPercent))
        cartRepository.setCartItems(items = listOf(cartItem { withProductId(productId = "product1"); withQuantity(quantity = 1) }))

        val summary = useCase()()

        // THEN
        assertEquals(10.0, summary.first().totalDiscount)
        clock.advanceTime(seconds = 6)
        assertEquals(0.0, summary.first().totalDiscount)
    }
}