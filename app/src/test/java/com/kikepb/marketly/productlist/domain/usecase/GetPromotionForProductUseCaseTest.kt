package com.kikepb.marketly.productlist.domain.usecase

import com.kikepb.marketly.core.builders.product
import com.kikepb.marketly.core.builders.promotion
import com.kikepb.marketly.productlist.domain.model.ProductPromotion.BuyXPayY
import com.kikepb.marketly.productlist.domain.model.ProductPromotion.Percent
import com.kikepb.marketly.productlist.domain.model.PromotionType.BUY_X_PAY_Y
import com.kikepb.marketly.productlist.domain.model.PromotionType.PERCENT
import org.junit.Assert.*
import org.junit.Test

class GetPromotionForProductUseCaseTest {

    private val useCase = GetPromotionForProductUseCase()

    @Test
    fun `GIVEN no promotions WHEN invoke THEN return null`() {
        // GIVEN
        val product = product()

        // WHEN
        val response = useCase.invoke(product = product, promotions = emptyList())

        // THEN
        assertNull(response)
    }

    @Test
    fun `GIVEN percent promotion WHEN invoke THEN returns discounted price rounded to two decimals`() {
        // GIVEN
        val productId = "product-id"
        val product = product {
            withId(id = productId)
            withPrice(price = 10.0)
        }
        val promotion = promotion {
            withType(type = PERCENT)
            withProductIds(productIds = listOf(productId))
            withValue(value = 15.0)
        }

        // WHEN
        val response = useCase(product = product, promotions = listOf(promotion))

        // THEN
        assertTrue(response is Percent)
        response as Percent
        assertEquals(8.50, response.discountedPrice, 0.001)
        assertEquals(15.0, response.percent, 0.001)
    }

    @Test
    fun `GIVEN buyX payY and percent promotions WHEN invoke THEN prioritizes buyX payY`() {
        // GIVEN
        val productId = "product-id"
        val product = product {
            withId(id = productId)
            withPrice(price = 10.0)
        }
        val promotionPercent = promotion {
            withType(type = PERCENT)
            withProductIds(productIds = listOf(productId))
            withValue(value = 15.0)
        }
        val promotionBuyXPayY = promotion {
            withType(type = BUY_X_PAY_Y)
            withProductIds(productIds = listOf(productId))
            withBuyQuantity(buyQuantity = 3)
            withValue(value = 2.0)
        }

        // WHEN
        val response = useCase(product = product, promotions = listOf(promotionPercent, promotionBuyXPayY))

        // THEN
        assertTrue(response is BuyXPayY)
        response as BuyXPayY
        assertEquals(3, response.buy)
        assertEquals(2, response.pay)
        assertEquals("3x2", response.label)
    }

    @Test
    fun `GIVEN multiple percent promotions WHEN invoke THEN returns highest discount`() {
        // GIVEN
        val productId = "product-id"
        val product = product {
            withId(id = productId)
            withPrice(price = 10.0)
        }
        val promotionLow = promotion {
            withType(type = PERCENT)
            withProductIds(productIds = listOf(productId))
            withValue(value = 15.0)
        }
        val promotionHigh = promotion {
            withType(type = PERCENT)
            withProductIds(productIds = listOf(productId))
            withValue(value = 50.0)
        }

        // WHEN
        val response = useCase(product = product, promotions = listOf(promotionHigh, promotionLow))

        // THEN
        assertTrue(response is Percent)
        assertEquals(50.0, (response as Percent).percent, 0.001)
    }

    @Test
    fun `GIVEN buyX payY without buy quantity WHEN invoke THEN returns null`() {
        // GIVEN
        val productId = "product-id"
        val product = product {
            withId(id = productId)
            withPrice(price = 10.0)
        }
        val promotionLow = promotion {
            withType(type = PERCENT)
            withProductIds(productIds = listOf(productId))
            withValue(value = 15.0)
        }
        val brokenBuyXPromotion = promotion {
            withType(BUY_X_PAY_Y)
            withProductIds(productIds = listOf(productId))
            withBuyQuantity(buyQuantity = null)
        }

        // WHEN
        val response = useCase(product = product, promotions = listOf(promotionLow, brokenBuyXPromotion))

        // THEN
        assertNull(response)
    }
}