package com.kikepb.marketly.cart.domain.usecase

import com.kikepb.marketly.core.builders.cartItem
import com.kikepb.marketly.core.builders.product
import com.kikepb.marketly.core.builders.promotion
import com.kikepb.marketly.core.fakes.FakeCartRepository
import com.kikepb.marketly.core.fakes.FakeClockRepository
import com.kikepb.marketly.core.fakes.FakeProductRepository
import com.kikepb.marketly.core.fakes.FakePromotionRepository
import com.kikepb.marketly.productlist.domain.usecase.GetPromotionForProductUseCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Instant

class GetCartItemsWithPromotionsUseCaseTest {

    private val clock = FakeClockRepository().apply { setTime(Instant.parse("2026-04-03T10:00:00Z")) }

    private fun useCase(
        cartRepository: FakeCartRepository = FakeCartRepository(),
        productRepository: FakeProductRepository = FakeProductRepository(),
        promotionRepository : FakePromotionRepository = FakePromotionRepository(),
        clock: FakeClockRepository = this.clock
    ) = GetCartItemsWithPromotionsUseCase(
        cartRepository = cartRepository,
        productRepository = productRepository,
        promotionRepository = promotionRepository,
        getPromotionForProductUseCase = GetPromotionForProductUseCase(),
        clock = clock
    )

    @Test
    fun `GIVEN empty cart WHEN invokes THEN returns empty list`() = runTest {
        // GIVEN
        val cart = FakeCartRepository().apply { setCartItems(items = emptyList()) }

        // WHEN
        val result = (useCase(cartRepository = cart)()).first()

        // THEN
        assertTrue("The initial result should always return an empty list", result.isEmpty())
    }

    @Test
    fun `GIVEN existing cart item with active promotion WHEN invokes THEN returns item with promotion `() = runTest {
        // GIVEN
        val productId = "productId"
        val product = product {
            withId(id = productId)
        }
        val now = clock.now()
        val promotion = promotion {
            withProductIds(productIds = listOf(productId))
            withStartTime(startTime = now.minusSeconds(10))
            withEndTime(endTime = now.plusSeconds(10))
        }
        val cartItem = cartItem {
            withProductId(productId = productId)
            withQuantity(quantity = 2)
        }

        val cart = FakeCartRepository().apply { setCartItems(items = listOf(cartItem)) }
        val products = FakeProductRepository().apply { setProducts(products = listOf(product)) }
        val promotions = FakePromotionRepository().apply { setPromotions(promotions = listOf(promotion)) }

        // WHEN
        val result = useCase(cartRepository = cart, productRepository = products, promotionRepository = promotions)().first()

        // THEN
        assertEquals(1, result. size)
        assertNotNull(result.first().item.promotion)
    }

    @Test
    fun `GIVEN cart item without matching product WHEN invoke THEN skip item`() = runTest {
        // GIVEN
        val cart = FakeCartRepository().apply { setCartItems(items = listOf(cartItem { withProductId(productId = "ghost-product-id") })) }
        val products = FakeProductRepository().apply { setProducts(products = listOf(product { withId(id = "product-id") })) }

        // WHEN
        val result = useCase(productRepository = products, cartRepository = cart)().first()

        // THEN
        assertTrue(result.isEmpty())
    }

    @Test
    fun `GIVEN promotion ending exactly now WHEN invoke THEN it must be include`() = runTest {
        // GIVEN
        val now = clock.now()
        val productId = "productId"
        val product = product { withId(id = productId) }
        val endingPromotion = promotion {
            withProductIds(productIds = listOf(productId))
            withStartTime(startTime = now.minusSeconds(100))
            withEndTime(endTime = now)
        }

        // WHEN
        val cart = FakeCartRepository().apply { setCartItems(items = listOf(cartItem { withProductId(productId = productId)})) }
        val products = FakeProductRepository().apply { setProducts(products = listOf(product)) }
        val promotions = FakePromotionRepository().apply { setPromotions(promotions = listOf(endingPromotion)) }

        val result = useCase(cartRepository = cart, productRepository = products, promotionRepository = promotions)().first()

        // THEN
        assertNotNull(result.first().item.promotion)
    }

    @Test
    fun `GIVEN expired promotion WHEN invoke THEN item remains but without promotion`() = runTest {
        // GIVEN
        val now = clock.now()
        val productId = "productId"
        val product = product { withId(id = productId) }
        val endingPromotion = promotion {
            withProductIds(productIds = listOf(productId))
            withStartTime(startTime = now.minusSeconds(100))
            withEndTime(endTime = now.minusSeconds(1))
        }

        // WHEN
        val cart = FakeCartRepository().apply { setCartItems(items = listOf(cartItem { withProductId(productId = productId)})) }
        val products = FakeProductRepository().apply { setProducts(products = listOf(product)) }
        val promotions = FakePromotionRepository().apply { setPromotions(promotions = listOf(endingPromotion)) }

        val result = useCase(cartRepository = cart, productRepository = products, promotionRepository = promotions)().first()

        // THEN
        assertNull(result.first().item.promotion)
    }

    @Test
    fun `GIVEN active promotion WHEN time advances THEN flow emits update list without promotion`() = runTest {
        // GIVEN
        val now = clock.now()
        val productId = "productId"
        val product = product { withId(id = productId) }
        val promotion = promotion {
            withProductIds(productIds = listOf(productId))
            withStartTime(startTime = now.minusSeconds(100))
            withEndTime(endTime = now.plusSeconds(5))
        }

        // WHEN
        val cart = FakeCartRepository().apply { setCartItems(items = listOf(cartItem { withProductId(productId = productId)})) }
        val products = FakeProductRepository().apply { setProducts(products = listOf(product)) }
        val promotions = FakePromotionRepository().apply { setPromotions(promotions = listOf(promotion)) }

        val useCase = useCase(cartRepository = cart, productRepository = products, promotionRepository = promotions)()

        // THEN
        val firstEmission = useCase.first()
        assertNotNull(firstEmission.first().item.promotion)
        clock.advanceTime(6)
        val secondEmission = useCase.first()
        assertNull(secondEmission.first().item.promotion)
    }
}