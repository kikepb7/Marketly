package com.kikepb.marketly.detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kikepb.marketly.detail.domain.usecase.GetProductDetailWithPromotionUseCase
import com.kikepb.marketly.detail.presentation.ProductDetailEvent.ShowError
import com.kikepb.marketly.productlist.domain.model.ProductWithPromotionModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getProductDetailWithPromotionUseCase: GetProductDetailWithPromotionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(value = ProductDetailUiState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<ProductDetailEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProductDetailEvent> = _events

    private var productJob: Job? = null

    fun loadProduct(productId: String) {
        _state.update { it.copy(isLoading = true) }
        productJob?.cancel()
        productJob = getProductDetailWithPromotionUseCase(productId = productId)
            .onEach { product ->
                _state.update { it.copy(isLoading = false, item = product) }
            }
            .catch { e ->
                _state.update { it.copy(isLoading = false) }
                _events.emit(value = ShowError(message = e.message.orEmpty()))
            }
            .launchIn(scope = viewModelScope)

    }

    fun addProductToCart() {

    }
}

data class ProductDetailUiState(
    val item: ProductWithPromotionModel? = null,
    val isLoading: Boolean = true,
)

sealed interface ProductDetailEvent {
    data class ShowMessage(val message: String): ProductDetailEvent
    data class ShowError(val message: String): ProductDetailEvent
}