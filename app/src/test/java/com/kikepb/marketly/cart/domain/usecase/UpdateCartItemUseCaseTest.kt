package com.kikepb.marketly.cart.domain.usecase

import com.kikepb.marketly.core.builders.cartItem
import com.kikepb.marketly.core.builders.product
import com.kikepb.marketly.core.domain.model.AppError.NotFoundError
import com.kikepb.marketly.core.domain.model.AppError.Validation.InsufficientStock
import com.kikepb.marketly.core.domain.model.AppError.Validation.QuantityMustBePositive
import com.kikepb.marketly.core.fakes.FakeCartRepository
import com.kikepb.marketly.core.fakes.FakeProductRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class UpdateCartItemUseCaseTest {

    @Test
    fun `GIVEN negative quantity WHEN invokes THEN throws quantity must be positive`() = runTest {
        // GIVEN
        val fakeCartRepository = FakeCartRepository()
        val fakeProductRepository = FakeProductRepository()
        val useCase = UpdateCartItemUseCase(cartRepository = fakeCartRepository, productRepository = fakeProductRepository)

        // WHEN
        val exception = runCatching { useCase(productId = "id", quantity = -1) }.exceptionOrNull()

        // THEN
        assertTrue(exception is QuantityMustBePositive)
    }

    @Test
    fun `GIVEN zero quantity WHEN invokes THEN removes items from cart`() = runTest {
        // GIVEN
        val productId = "product-id"
        val product = product {
            withId(id = productId)
        }
        val cartItemProduct = cartItem {
            withProductId(productId = productId)
            withQuantity(quantity = 3)
        }
        val fakeCartRepository = FakeCartRepository().apply { setCartItems(items = listOf(cartItemProduct)) }
        val fakeProductRepository = FakeProductRepository().apply { setProducts(products = listOf(product)) }
        val useCase = UpdateCartItemUseCase(cartRepository = fakeCartRepository, productRepository = fakeProductRepository)

        // WHEN
        useCase(productId = productId, quantity = 0)

        // THEN
        val items = fakeCartRepository.getCartItems().first()
        assertEquals(0, items.size)
    }

    @Test
    fun `GIVEN missing product WHEN invokes THEN throws not found error`() = runTest {
        // GIVEN
        val fakeProductRepository = FakeProductRepository().apply { setProducts(products = emptyList()) }
        val fakeCartRepository = FakeCartRepository()
        val useCase = UpdateCartItemUseCase(cartRepository = fakeCartRepository, productRepository = fakeProductRepository)

        // WHEN
        val exception = runCatching { useCase(productId = "not", quantity = 1) }.exceptionOrNull()

        // THEN
        assertTrue(exception is NotFoundError)
    }

    @Test
    fun `GIVEN request quantity greater than stock WHEN invokes THEN throws insufficient stock error`() = runTest {
        // GIVEN
        val productId = "product-id"
        val product = product {
            withId(id = productId)
            withStock(stock = 3)
        }
        val cartItem = cartItem {
            withProductId(productId = productId)
            withQuantity(quantity = 1)
        }
        val fakeProductRepository = FakeProductRepository().apply { setProducts(products = listOf(product)) }
        val fakeCartRepository = FakeCartRepository().apply { setCartItems(items = listOf(cartItem)) }
        val useCase = UpdateCartItemUseCase(cartRepository = fakeCartRepository, productRepository = fakeProductRepository)

        // WHEN
        val exception = runCatching { useCase(productId = productId, quantity = 5) }.exceptionOrNull()

        // THEN
        assertTrue(exception is InsufficientStock)
    }

    @Test
    fun `GIVEN valid product quantity WHEN invokes THEN updates cart item`() = runTest {
        // GIVEN
        val productId = "product-id"
        val product = product {
            withId(id = productId)
            withStock(stock = 20)
        }
        val cartItem = cartItem {
            withProductId(productId = productId)
            withQuantity(quantity = 1)
        }
        val fakeProductRepository = FakeProductRepository().apply { setProducts(products = listOf(product)) }
        val fakeCartRepository = FakeCartRepository().apply { setCartItems(items = listOf(cartItem)) }
        val useCase = UpdateCartItemUseCase(cartRepository = fakeCartRepository, productRepository = fakeProductRepository)

        // WHEN
        runCatching { useCase(productId = productId, quantity = 5) }.exceptionOrNull()

        // THEN
        val items = fakeCartRepository.getCartItems().first()
        assertEquals(1, items.size)
        assertEquals(5, items.first().quantity)
    }
}