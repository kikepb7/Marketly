package com.kikepb.marketly.cart.domain.usecase

import com.kikepb.marketly.cart.domain.repository.CartRepository
import com.kikepb.marketly.core.builders.product
import com.kikepb.marketly.core.domain.model.AppError.NotFoundError
import com.kikepb.marketly.core.domain.model.AppError.Validation.InsufficientStock
import com.kikepb.marketly.core.domain.model.AppError.Validation.QuantityMustBePositive
import com.kikepb.marketly.core.fakes.FakeCartRepository
import com.kikepb.marketly.core.fakes.FakeProductRepository
import com.kikepb.marketly.productlist.domain.repository.ProductRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AddToCartUseCaseTest {

    @Test
    fun `GIVEN zero quantity WHEN adding product to cart THEN throws QuantityMustBePositive exception`() = runTest {
        // GIVEN
        val fakeCartRepository = FakeCartRepository()
        val fakeProductRepository = FakeProductRepository()
        val useCase = AddToCartUseCase(cartRepository = fakeCartRepository, productRepository = fakeProductRepository)

        // WHEN
        val exception = runCatching { useCase(productId = "id", quantity = 0) }.exceptionOrNull()

        // THEN
        assertTrue(exception is QuantityMustBePositive)
    }

    @Test
    fun `GIVEN negative quantity WHEN adding product to cart THEN throws QuantityMustBePositive exception`() = runTest {
        // GIVEN
        val fakeCartRepository = FakeCartRepository()
        val fakeProductRepository = FakeProductRepository()
        val useCase = AddToCartUseCase(cartRepository = fakeCartRepository, productRepository = fakeProductRepository)

        // WHEN
        val exception = runCatching { useCase(productId = "id", quantity = -2) }.exceptionOrNull()

        // THEN
        assertTrue(exception is QuantityMustBePositive)
    }

    @Test
    fun `GIVEN non existing products WHEN adding product to cart THEN throws NotFoundError exception`() = runTest {
        // GIVEN
        val fakeCartRepository = FakeCartRepository()
        val fakeProductRepository = FakeProductRepository().apply { setProducts(products = emptyList()) }
        val useCase = AddToCartUseCase(cartRepository = fakeCartRepository, productRepository = fakeProductRepository)

        // WHEN
        val exception = runCatching { useCase(productId = "id", quantity = 1) }.exceptionOrNull()

        // THEN
        assertTrue(exception is NotFoundError)
    }

    @Test
    fun `GIVEN product with stock of two WHEN adding five quantity to cart THEN throws InsufficientStock exception`() = runTest {
        // GIVEN
        val productId = "id-test-1"
        val product = product {
            withId(id = productId)
            withStock(stock = 2)
        }
        val fakeCartRepository = FakeCartRepository()
        val fakeProductRepository = FakeProductRepository().apply { setProducts(listOf(element = product)) }
        val useCase = AddToCartUseCase(cartRepository = fakeCartRepository, productRepository = fakeProductRepository)

        // WHEN
        val exception = runCatching { useCase(productId = productId, quantity = 5) }.exceptionOrNull()

        // THEN
        assertTrue(exception is InsufficientStock)
        assertEquals(2, (exception as InsufficientStock).available)
    }

    @Test
    fun `GIVEN product with stock of ten WHEN adding three quantity to cart THEN cart contains one item with three quantity`() = runTest {
        // GIVEN
        val productId = "id-test-1"
        val product = product {
            withId(id = productId)
            withStock(stock = 10)
        }
        val fakeCartRepository = FakeCartRepository()
        val fakeProductRepository = FakeProductRepository().apply { setProducts(listOf(element = product)) }
        val useCase = AddToCartUseCase(cartRepository = fakeCartRepository, productRepository = fakeProductRepository)

        // WHEN
        useCase(productId = productId, quantity = 3)

        // THEN
        val items = fakeCartRepository.getCartItems().first()
        assertEquals(productId, items.first().productId)
        assertEquals(1, items.size)
        assertEquals(3, items.first().quantity)
    }

    @Test
    fun `GIVEN default quantity WHEN adding product to cart THEN cart contains one item with one quantity`() = runTest {
        // GIVEN
        val productId = "id-test-1"
        val product = product {
            withId(id = productId)
            withStock(stock = 10)
        }
        val fakeCartRepository = FakeCartRepository()
        val fakeProductRepository = FakeProductRepository().apply { setProducts(listOf(element = product)) }
        val useCase = AddToCartUseCase(cartRepository = fakeCartRepository, productRepository = fakeProductRepository)

        // WHEN
        useCase(productId = productId)

        // THEN
        val items = fakeCartRepository.getCartItems().first()
        assertEquals(1, items.size)
        assertEquals(1, items.first().quantity)
    }

    @Test
    fun `GIVEN zero quantity WHEN adding product to cart THEN does not call product repository`() = runTest {
        // GIVEN
        val cartRepository = mockk<CartRepository>()
        val productRepository = mockk<ProductRepository>()
        val useCase = AddToCartUseCase(cartRepository = cartRepository, productRepository = productRepository)

        // WHEN
        runCatching { useCase(productId = "id", quantity = 0) }.exceptionOrNull()

        // THEN
        coVerify(exactly = 0) { productRepository.getProductById(productId = any()) }
        coVerify(exactly = 0) { cartRepository.getCartItemById(productId = any()) }
        coVerify(exactly = 0) { cartRepository.addToCart(productId = any(), quantity = any()) }
    }

    @Test
    fun `GIVEN valid product WHEN adding product to cart THEN calls addToCart with expect values`() = runTest {
        // GIVEN
        val cartRepository = mockk<CartRepository>()
        val productRepository = mockk<ProductRepository>()
        val productId = "custom-id"
        val product = product {
            withId(productId)
            withStock(10)
        }

        coEvery { productRepository.getProductById(productId = productId) } returns flowOf(value = product)
        coEvery { cartRepository.getCartItemById(productId = productId) } returns null
        coEvery { cartRepository.addToCart(productId = productId, quantity = 3) } just Runs

        val useCase = AddToCartUseCase(cartRepository = cartRepository, productRepository = productRepository)

        // WHEN
        useCase(productId = productId, quantity = 3)

        // THEN
        coVerify(exactly = 1) { productRepository.getProductById(productId = productId) }
        coVerify(exactly = 1) { cartRepository.getCartItemById(productId = productId) }
        coVerify(exactly = 1) { cartRepository.addToCart(productId = productId, quantity = 3) }
    }
}