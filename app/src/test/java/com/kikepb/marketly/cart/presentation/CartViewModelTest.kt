package com.kikepb.marketly.cart.presentation

import app.cash.turbine.test
import com.kikepb.marketly.cart.domain.usecase.GetCartItemsWithPromotionsUseCase
import com.kikepb.marketly.cart.domain.usecase.GetCartSummaryUseCase
import com.kikepb.marketly.cart.domain.usecase.UpdateCartItemUseCase
import com.kikepb.marketly.cart.presentation.CartEvent.ShowMessage
import com.kikepb.marketly.cart.presentation.CartUiState.Success
import com.kikepb.marketly.core.builders.cartItem
import com.kikepb.marketly.core.builders.product
import com.kikepb.marketly.core.dispatcher.MainDispatcherRule
import com.kikepb.marketly.core.fakes.FakeCartRepository
import com.kikepb.marketly.core.fakes.FakeClockRepository
import com.kikepb.marketly.core.fakes.FakeProductRepository
import com.kikepb.marketly.core.fakes.FakePromotionRepository
import com.kikepb.marketly.productlist.domain.repository.ProductRepository
import com.kikepb.marketly.productlist.domain.usecase.GetPromotionForProductUseCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class CartViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun createViewModel(
        fakeCartRepository: FakeCartRepository = FakeCartRepository(),
        fakeProductRepository: ProductRepository = FakeProductRepository(),
        fakePromotionRepository: FakePromotionRepository = FakePromotionRepository(),
        fakeClockRepository: FakeClockRepository = FakeClockRepository()
    ): CartViewModel =
        CartViewModel(
            cartRepository = fakeCartRepository,
            getCartSummaryUseCase = GetCartSummaryUseCase(
                cartRepository = fakeCartRepository,
                productRepository = fakeProductRepository,
                promotionRepository = fakePromotionRepository,
                getPromotionForProductUseCase = GetPromotionForProductUseCase(),
                clock = fakeClockRepository
            ),
            updateCartItemUseCase = UpdateCartItemUseCase(
                cartRepository = fakeCartRepository,
                productRepository = fakeProductRepository
            ),
            getCartItemsWithPromotionsUseCase = GetCartItemsWithPromotionsUseCase(
                cartRepository = fakeCartRepository,
                productRepository = fakeProductRepository,
                promotionRepository = fakePromotionRepository,
                getPromotionForProductUseCase = GetPromotionForProductUseCase(),
                clock = fakeClockRepository
            )
        )

    @Test
    fun `GIVEN cart data WHEN initialized THEN emit success state`() =
        runTest(context = mainDispatcherRule.scheduler) {
            // GIVEN
            val productId = "productId"
            val product = product { withId(id = productId); withName(name = "productName"); withPrice(price = 2.0) }
            val item = cartItem { withProductId(productId = productId); withQuantity(quantity = 3) }
            val fakeProductRepository = FakeProductRepository().apply { setProducts(products = listOf(product)) }
            val fakeCartRepository = FakeCartRepository().apply { setCartItems(items = listOf(item)) }
            val viewModel = createViewModel(fakeProductRepository = fakeProductRepository, fakeCartRepository = fakeCartRepository)

            viewModel.state.test {
                // WHEN
                val state = awaitItem() as Success

                // THEN
                assertEquals(1, state.cartItems.size)
                assertEquals(6.0, state.summary?.subtotal)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN quantity one WHEN decrease quantity THEN removes item from cart`() =
        runTest(context = mainDispatcherRule.scheduler) {
            // GIVEN
            val productId = "productId"
            val product = product { withId(id = productId); withStock(stock = 5); withPrice(price = 2.0) }
            val item = cartItem { withProductId(productId = productId); withQuantity(quantity = 3) }
            val fakeProductRepository = FakeProductRepository().apply { setProducts(products = listOf(product)) }
            val fakeCartRepository = FakeCartRepository().apply { setCartItems(items = listOf(item)) }
            val viewModel = createViewModel(fakeProductRepository = fakeProductRepository, fakeCartRepository = fakeCartRepository)

            viewModel.state.test {
                // WHEN
                awaitItem()
                viewModel.reduceQuantity(productId = productId, currentQuantity = 1)
                val state = awaitItem() as Success

                // THEN
                assertTrue(state.cartItems.isEmpty())
                assertEquals(0.0, state.summary?.finalTotal ?: 0.0, 0.001)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN insufficient stock WHEN update quantity THEN emits error event`() =
        runTest(context = mainDispatcherRule.scheduler) {
            // GIVEN
            val productId = "productId"
            val product = product { withId(id = productId); withStock(stock = 2) }
            val item = cartItem { withProductId(productId = productId); withQuantity(quantity = 1) }
            val fakeProductRepository = FakeProductRepository().apply { setProducts(products = listOf(product)) }
            val fakeCartRepository = FakeCartRepository().apply { setCartItems(items = listOf(item)) }
            val viewModel = createViewModel(fakeProductRepository = fakeProductRepository, fakeCartRepository = fakeCartRepository)

            viewModel.event.test {
                // WHEN
                viewModel.increaseQuantity(productId = productId, currentQuantity = 5)
                val event = awaitItem()

                // THEN
                assertTrue(event is ShowMessage)
                cancelAndIgnoreRemainingEvents()
            }
        }
}