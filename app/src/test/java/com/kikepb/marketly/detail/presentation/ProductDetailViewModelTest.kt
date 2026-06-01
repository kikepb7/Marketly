package com.kikepb.marketly.detail.presentation

import app.cash.turbine.test
import com.kikepb.marketly.cart.domain.usecase.AddToCartUseCase
import com.kikepb.marketly.core.builders.product
import com.kikepb.marketly.core.dispatcher.MainDispatcherRule
import com.kikepb.marketly.core.fakes.FakeCartRepository
import com.kikepb.marketly.core.fakes.FakeClockRepository
import com.kikepb.marketly.core.fakes.FakeProductRepository
import com.kikepb.marketly.core.fakes.FakePromotionRepository
import com.kikepb.marketly.detail.domain.usecase.GetProductDetailWithPromotionUseCase
import com.kikepb.marketly.detail.presentation.ProductDetailEvent.SuccessAddToCart
import com.kikepb.marketly.productlist.domain.usecase.GetPromotionForProductUseCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ProductDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val fakeProductRepository: FakeProductRepository = FakeProductRepository()
    private val fakePromotionRepository: FakePromotionRepository = FakePromotionRepository()
    private val fakeCartRepository: FakeCartRepository = FakeCartRepository()
    private val fakeClockRepository: FakeClockRepository = FakeClockRepository()

    fun createViewModel(

    ): ProductDetailViewModel =
        ProductDetailViewModel(
            getProductDetailWithPromotionUseCase = GetProductDetailWithPromotionUseCase(
                productRepository = fakeProductRepository,
                promotionRepository = fakePromotionRepository,
                getPromotionForProductUseCase = GetPromotionForProductUseCase(),
                clock = fakeClockRepository
            ),
            addToCartUseCase = AddToCartUseCase(
                cartRepository = fakeCartRepository,
                productRepository = fakeProductRepository
            )
        )

    @Test
    fun `GIVEN valid productId WHEN load product THEN emits item`() =
        runTest(context = mainDispatcherRule.scheduler) {
            // GIVEN
            val product = product { withId(id = "1"); withName(name = "productName") }
            fakeProductRepository.setProducts(products = listOf(product))
            val viewModel = createViewModel()

            viewModel.state.test {
                // WHEN
                awaitItem()

                viewModel.loadProduct(productId = "1")

                // THEN
                val finalState = awaitItem()
                assertEquals("1", finalState.item?.product?.id)
                assertEquals("productName", finalState.item?.product?.name)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN missing productId WHEN load product THEN ends with item null`() =
        runTest(context = mainDispatcherRule.scheduler) {
            // GIVEN
            fakeProductRepository.setProducts(emptyList())
            val viewModel = createViewModel()

            viewModel.state.test {
                // WHEN
                awaitItem()

                viewModel.loadProduct(productId = "1")
                val state = awaitItem()

                // THEN
                assertNull(state.item)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN loaded product WHEN add to cart success THEN emits success event`() =
        runTest(context = mainDispatcherRule.scheduler) {
            // GIVEN
            val product = product { withId(id = "1"); withStock(stock = 10) }
            fakeProductRepository.setProducts(products = listOf(product))
            val viewModel = createViewModel()

            // WHEN
            viewModel.loadProduct(productId = "1")

            // THEN
            viewModel.state.test {
                viewModel.addProductToCart()
                val result = awaitItem()

                assertEquals(SuccessAddToCart, result)
                cancelAndIgnoreRemainingEvents()
            }
        }
}