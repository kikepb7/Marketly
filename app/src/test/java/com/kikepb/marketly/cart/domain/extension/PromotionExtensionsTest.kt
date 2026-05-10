package com.kikepb.marketly.cart.domain.extension

import com.kikepb.marketly.cart.domain.utils.activeAt
import com.kikepb.marketly.core.builders.promotion
import com.kikepb.marketly.productlist.domain.model.PromotionModel
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.time.Instant

class PromotionExtensionsTest {

    private val now = Instant.parse("2026-04-03T10:00:00Z")

    @Test
    fun `GIVEN future promotion WHEN activeAt THEN exclude`() {
        // GIVEN
        val futurePromotion = promotion {
            withStartTime(startTime = now.plusSeconds(10))
            withEndTime(endTime = now.plusSeconds(100))
        }
        val promotions = listOf(futurePromotion)

        // WHEN
        val result = promotions.activeAt(now = now)

        // THEN
        assertEquals(0, result.size)
    }

    @Test
    fun `GIVEN expire promotion WHEN activeAt THEN exclude`() {
        // GIVEN
        val expirePromotion = promotion {
            withStartTime(startTime = now.minusSeconds(100))
            withEndTime(endTime = now.minusSeconds(10))
        }
        val promotions = listOf(expirePromotion)

        // WHEN
        val result = promotions.activeAt(now = now)

        // THEN
        assertEquals(0, result.size)
    }

    @Test
    fun `GIVEN on going promotion WHEN activeAt THEN include`() {
        // GIVEN
        val activePromotion = promotion {
            withStartTime(startTime = now.minusSeconds(1))
            withEndTime(endTime = now.plusSeconds(1))
        }
        val promotions = listOf(activePromotion)

        // WHEN
        val result = promotions.activeAt(now = now)

        // THEN
        assertEquals(1, result.size)
    }

    @Test
    fun `GIVEN exact start time promotion WHEN activeAt THEN include`() {
        // GIVEN
        val activePromotion = promotion {
            withStartTime(startTime = now)
            withEndTime(endTime = now.plusSeconds(100))
        }
        val promotions = listOf(activePromotion)

        // WHEN
        val result = promotions.activeAt(now = now)

        // THEN
        assertEquals(1, result.size)
    }

    @Test
    fun `GIVEN exact end time promotion WHEN activeAt THEN include`() {
        // GIVEN
        val activePromotion = promotion {
            withStartTime(startTime = now.minusSeconds(100))
            withEndTime(endTime = now)
        }
        val promotions = listOf(activePromotion)

        // WHEN
        val result = promotions.activeAt(now = now)

        // THEN
        assertEquals(1, result.size)
    }

    @Test
    fun `GIVEN exact end time promotion WHEN activeAt THEN return empty`() {
        // GIVEN
        val promotions = emptyList<PromotionModel>()

        // WHEN
        val result = promotions.activeAt(now = now)

        // THEN
        assertEquals(0, result.size)
    }
}