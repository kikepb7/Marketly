package com.kikepb.marketly.productlist.presentation

import com.kikepb.marketly.core.dispatcher.MainDispatcherRule
import com.kikepb.marketly.core.fakes.FakeCartRepository
import com.kikepb.marketly.core.fakes.FakeClockRepository
import com.kikepb.marketly.core.fakes.FakeProductRepository
import com.kikepb.marketly.core.fakes.FakePromotionRepository
import com.kikepb.marketly.productlist.domain.model.SortOptionModel.NONE
import com.kikepb.marketly.productlist.domain.model.SortOptionModel.PRICE_ASC
import com.kikepb.marketly.productlist.domain.repository.ProductRepository
import com.kikepb.marketly.productlist.domain.repository.SettingsRepository
import com.kikepb.marketly.productlist.domain.usecase.GetCartItemCountUseCase
import com.kikepb.marketly.productlist.domain.usecase.GetProductsUseCase
import com.kikepb.marketly.productlist.domain.usecase.GetPromotionForProductUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ProductListViewModelMockTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val settingsRepository: SettingsRepository = mockk(relaxed = true) {
        every { selectedCategory } returns flowOf(value = null)
        every { sortOption } returns flowOf(value = NONE)
        every { inStockOnly } returns flowOf(value = false)
        every { filtersVisible } returns flowOf(value = true)
    }

    private fun createViewModel(
        fakeProductRepository: ProductRepository = FakeProductRepository(),
        fakePromotionRepository: FakePromotionRepository = FakePromotionRepository(),
        fakeClockRepository: FakeClockRepository = FakeClockRepository(),
        fakeCartRepository: FakeCartRepository = FakeCartRepository()
    ): ProductListViewModel = ProductListViewModel(
            getProductsUseCase = GetProductsUseCase(
                productRepository = fakeProductRepository,
                promotionRepository = fakePromotionRepository,
                getPromotionForProductUseCase = GetPromotionForProductUseCase(),
                settingsRepository = settingsRepository,
                clockRepository = fakeClockRepository
            ),
            settingsRepository = settingsRepository,
            getCartItemCountUseCase = GetCartItemCountUseCase(cartRepository = fakeCartRepository)
        )

    @Test
    fun `GIVEN category WHEN set category THEN delegates to settings repository`() =
        runTest(context = mainDispatcherRule.scheduler) {
            // GIVEN
            val viewModel = createViewModel()
            val category = "pasta"

            // WHEN
            viewModel.setCategory(category = category)

            // THEN
            coVerify(exactly = 1) { settingsRepository.setSelectedCategory(value = category) }
        }

    @Test
    fun `GIVEN sortOption WHEN set sortOption THEN delegates to settings repository`() =
        runTest(context = mainDispatcherRule.scheduler) {
            // GIVEN
            val viewModel = createViewModel()
            val sortOption = PRICE_ASC

            // WHEN
            viewModel.setSortOption(sortOption = PRICE_ASC)

            // THEN
            coVerify(exactly = 1) { settingsRepository.setSortOption(value = sortOption) }
        }

    @Test
    fun `GIVEN filter visible WHEN set filter visible THEN delegates to settings repository`() =
        runTest(context = mainDispatcherRule.scheduler) {
            // GIVEN
            val viewModel = createViewModel()
            val filterVisible = true

            // WHEN
            viewModel.setFilterVisible(showFilters = true)

            // THEN
            coVerify(exactly = 1) { settingsRepository.setFiltersVisible(value = filterVisible) }
        }
}