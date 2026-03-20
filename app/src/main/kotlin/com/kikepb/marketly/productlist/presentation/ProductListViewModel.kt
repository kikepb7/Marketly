package com.kikepb.marketly.productlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kikepb.marketly.productlist.domain.model.ProductModel
import com.kikepb.marketly.productlist.domain.model.SortOptionModel
import com.kikepb.marketly.productlist.domain.model.SortOptionModel.NONE
import com.kikepb.marketly.productlist.domain.usecase.GetProductsUseCase
import com.kikepb.marketly.productlist.presentation.ProductListUiState.Error
import com.kikepb.marketly.productlist.presentation.ProductListUiState.Loading
import com.kikepb.marketly.productlist.presentation.ProductListUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow<ProductListUiState>(value = Loading)
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProductListEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProductListEvent> = _events

    private val _filtersVisible = MutableStateFlow(value = true)
    val filtersVisible: StateFlow<Boolean> = _filtersVisible.asStateFlow()

    init { loadProducts() }

    fun loadProducts() {
        _uiState.update { Loading }

        getProductsUseCase()
            .onEach { products ->
                val categories = products.map { it.category }.distinct().sorted()
                _uiState.update {
                    Success(
                        products = products,
                        categories = categories,
                        selectedCategory = null,
                        sortOption = NONE
                    )
                }
            }
            .catch { e -> _uiState.update { Error(message = e.message.orEmpty()) } }
            .launchIn(scope = viewModelScope)
    }

    fun setCategory(category: String?) {
        viewModelScope.launch {}
    }

    fun setSortOption(sortOption: SortOptionModel?) {
        viewModelScope.launch {}
    }

    fun setFilterVisible(showFilters: Boolean) {
        _filtersVisible.update { showFilters }
    }
}

sealed class ProductListUiState {
    data object Loading: ProductListUiState()
    data class Error(val message: String): ProductListUiState()
    data class Success(
        val products: List<ProductModel>,
        val categories: List<String>,
        val selectedCategory: String?,
        val sortOption: SortOptionModel
    ): ProductListUiState()
}

sealed interface ProductListEvent {
    data class ShowMessage(val message: String): ProductListEvent
}