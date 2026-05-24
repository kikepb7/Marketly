package com.kikepb.marketly.productlist.presentation

import app.cash.turbine.test
import com.kikepb.marketly.core.builders.product
import com.kikepb.marketly.core.dispatcher.MainDispatcherRule
import com.kikepb.marketly.core.fakes.FakeCartRepository
import com.kikepb.marketly.core.fakes.FakeClockRepository
import com.kikepb.marketly.core.fakes.FakeProductRepository
import com.kikepb.marketly.core.fakes.FakePromotionRepository
import com.kikepb.marketly.core.fakes.FakeSettingsRepository
import com.kikepb.marketly.core.stubs.FailingProductRepositoryStub
import com.kikepb.marketly.productlist.domain.model.SortOptionModel.PRICE_ASC
import com.kikepb.marketly.productlist.domain.repository.ProductRepository
import com.kikepb.marketly.productlist.domain.usecase.GetCartItemCountUseCase
import com.kikepb.marketly.productlist.domain.usecase.GetProductsUseCase
import com.kikepb.marketly.productlist.domain.usecase.GetPromotionForProductUseCase
import com.kikepb.marketly.productlist.presentation.ProductListUiState.Error
import com.kikepb.marketly.productlist.presentation.ProductListUiState.Success
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun createViewModel(
        fakeProductRepository: ProductRepository = FakeProductRepository(),
        fakeSettingsRepository: FakeSettingsRepository = FakeSettingsRepository(),
        fakePromotionRepository: FakePromotionRepository = FakePromotionRepository(),
        fakeClockRepository: FakeClockRepository = FakeClockRepository(),
        fakeCartRepository: FakeCartRepository = FakeCartRepository()
    ): ProductListViewModel =
        ProductListViewModel(
            getProductsUseCase = GetProductsUseCase(
                productRepository = fakeProductRepository,
                promotionRepository = fakePromotionRepository,
                getPromotionForProductUseCase = GetPromotionForProductUseCase(),
                settingsRepository = fakeSettingsRepository,
                clockRepository = fakeClockRepository
            ),
            settingsRepository = fakeSettingsRepository,
            getCartItemCountUseCase = GetCartItemCountUseCase(cartRepository = fakeCartRepository)
        )

    @Test
    fun `GIVEN products WHEN initialized THEN emits success state`() =
        runTest(context = mainDispatcherRule.scheduler) {
            // GIVEN
            val productId = "productId"
            val product = product { withId(id = productId) }
            val fakeProductRepository = FakeProductRepository().apply { setProducts(products = listOf(product)) }
            val viewModel = createViewModel(fakeProductRepository = fakeProductRepository)

            viewModel.uiState.test {
                // WHEN
                val state = awaitItem()

                // THEN
                assertTrue(state is Success)
                assertEquals(1, (state as Success).products.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN selected category WHEN set category THEN filters products`() =
        runTest(context = mainDispatcherRule.scheduler) {
            // GIVEN
            val product1 = product { withId(id = "product1"); withCategory(category = "carne") }
            val product2 = product { withId(id = "product2"); withCategory(category = "pasta") }
            val fakeProductRepository = FakeProductRepository().apply { setProducts(products = listOf(product1, product2)) }
            val viewModel = createViewModel(fakeProductRepository = fakeProductRepository)

            viewModel.uiState.test {
                // WHEN
                awaitItem()
                viewModel.setCategory(category = "pasta")
                val state = awaitItem()

                // THEN
                assertTrue(state is Success)
                assertEquals(1, (state as Success).products.size)
                assertEquals("pasta", (state).selectedCategory)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN price asc sort option WHEN set sort option THEN sorts by effective price`() =
        runTest(context = mainDispatcherRule.scheduler) {
            // GIVEN
            val product1 = product { withId(id = "product1"); withPrice(price = 30.0) }
            val product2 = product { withId(id = "product2"); withPrice(price = 15.0) }
            val fakeProductRepository = FakeProductRepository().apply { setProducts(products = listOf(product1, product2)) }
            val viewModel = createViewModel(fakeProductRepository = fakeProductRepository)

            viewModel.uiState.test {
                // WHEN
                awaitItem()
                viewModel.setSortOption(sortOption = PRICE_ASC)
                val state = awaitItem() as Success

                // THEN
                assertEquals(15.0, state.products[0].product.price, 0.0)
                assertEquals(30.0, state.products[1].product.price, 0.0)
                assertEquals(PRICE_ASC, state.sortOption)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN repository error WHEN loading products THEN emits error state`() =
        runTest(context = mainDispatcherRule.scheduler) {
            // GIVEN
            val failingRepository = FailingProductRepositoryStub(exception = Exception("Prueba test"))
            val viewModel = createViewModel(fakeProductRepository = failingRepository)

            viewModel.uiState.test {
                // WHEN
                val state = awaitItem()

                // THEN
                assertTrue(state is Error)
                assertTrue((state as Error).message == "Prueba test")
                cancelAndIgnoreRemainingEvents()
            }
        }
}