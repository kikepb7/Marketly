package com.kikepb.marketly.productlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kikepb.marketly.productlist.domain.model.ProductPromotion.Percent
import com.kikepb.marketly.productlist.domain.model.ProductWithPromotionModel
import com.kikepb.marketly.productlist.domain.model.SortOptionModel
import com.kikepb.marketly.productlist.domain.model.SortOptionModel.DISCOUNT
import com.kikepb.marketly.productlist.domain.model.SortOptionModel.NONE
import com.kikepb.marketly.productlist.domain.model.SortOptionModel.PRICE_ASC
import com.kikepb.marketly.productlist.domain.model.SortOptionModel.PRICE_DESC
import com.kikepb.marketly.productlist.domain.repository.SettingsRepository
import com.kikepb.marketly.productlist.domain.usecase.GetProductsUseCase
import com.kikepb.marketly.productlist.presentation.ProductListUiState.Error
import com.kikepb.marketly.productlist.presentation.ProductListUiState.Loading
import com.kikepb.marketly.productlist.presentation.ProductListUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val settingsRepository: SettingsRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<ProductListUiState>(value = Loading)
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()
    private val _events = MutableSharedFlow<ProductListEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProductListEvent> = _events
    val filterVisible: StateFlow<Boolean> = settingsRepository.filtersVisible.stateIn(
        scope = viewModelScope,
        initialValue = true,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000)
    )
    private var productsJob: Job? = null

    init { loadProducts() }

    fun loadProducts() {
        _uiState.update { Loading }
        productsJob?.cancel()
        productsJob = combine(
            flow = getProductsUseCase(),
            flow2 = settingsRepository.selectedCategory,
            flow3 = settingsRepository.sortOption
        ) { products, selectedCategory, sortOption ->
            var filteredProducts = products
            if (selectedCategory != null) filteredProducts = filteredProducts.filter { it.product.category == selectedCategory }

            val productsSorted = when (sortOption) {
                PRICE_ASC -> filteredProducts.sortedBy { effectivePrice(item = it) }
                PRICE_DESC -> filteredProducts.sortedByDescending { effectivePrice(item = it) }
                NONE -> filteredProducts
                DISCOUNT -> filteredProducts.sortedWith(
                    comparator = compareByDescending<ProductWithPromotionModel> {
                        effectiveDiscountPercent(item = it)
                    }.thenBy { it.promotion == null }
                )
            }

            val categories = products.map { it.product.category }.distinct().sorted()

            Success(
                products = productsSorted,
                categories = categories,
                selectedCategory = selectedCategory,
                sortOption = sortOption
            )
        }.onEach { state ->
            _uiState.value = state
        } .catch { e ->
            _uiState.update { Error(message = e.message.orEmpty()) }
        }.launchIn(scope = viewModelScope)
    }

    fun setCategory(category: String?) {
        viewModelScope.launch {
            settingsRepository.setSelectedCategory(value = category)
        }
    }

    fun setSortOption(sortOption: SortOptionModel) {
        viewModelScope.launch {
            settingsRepository.setSortOption(value = sortOption)
        }
    }

    fun setFilterVisible(showFilters: Boolean) {
        viewModelScope.launch {
            settingsRepository.setFiltersVisible(value = showFilters)
        }
    }

    private fun effectiveDiscountPercent(item: ProductWithPromotionModel): Double =
        when (item) {
            is Percent -> item.percent
            else -> 0.0
        }

    private fun effectivePrice(item: ProductWithPromotionModel): Double =
        when (item) {
            is Percent -> item.discountedPrice
            else -> item.product.price
        }
}

sealed class ProductListUiState {
    data object Loading: ProductListUiState()
    data class Error(val message: String): ProductListUiState()
    data class Success(
        val products: List<ProductWithPromotionModel>,
        val categories: List<String>,
        val selectedCategory: String?,
        val sortOption: SortOptionModel
    ): ProductListUiState()
}

sealed interface ProductListEvent {
    data class ShowMessage(val message: String): ProductListEvent
}