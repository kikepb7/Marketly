package com.kikepb.marketly.detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kikepb.marketly.cart.domain.usecase.AddToCartUseCase
import com.kikepb.marketly.core.domain.model.AppError
import com.kikepb.marketly.core.domain.model.AppError.DatabaseError
import com.kikepb.marketly.core.domain.model.AppError.NetworkError
import com.kikepb.marketly.core.domain.model.AppError.NotFoundError
import com.kikepb.marketly.core.domain.model.AppError.Validation.InsufficientStock
import com.kikepb.marketly.core.domain.model.AppError.Validation.QuantityMustBePositive
import com.kikepb.marketly.detail.domain.usecase.GetProductDetailWithPromotionUseCase
import com.kikepb.marketly.detail.presentation.ProductDetailEvent.NotEnoughStock
import com.kikepb.marketly.detail.presentation.ProductDetailEvent.SuccessAddToCart
import com.kikepb.marketly.detail.presentation.ProductDetailEvent.UnknownError
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getProductDetailWithPromotionUseCase: GetProductDetailWithPromotionUseCase,
    private val addToCartUseCase: AddToCartUseCase
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
                if (e is AppError) handleError(error = e)
                else handleError(error = AppError.UnknownError(message = e.message))
            }
            .launchIn(scope = viewModelScope)
    }

    fun addProductToCart() {
        val productId = _state.value.item?.product?.id ?: return

        viewModelScope.launch {
            try {
                addToCartUseCase(productId = productId)
                _events.emit(value = SuccessAddToCart)
            } catch (e: AppError) {
                handleError(e)
            } catch (e: Exception) {
                handleError(error = AppError.UnknownError(message = e.message))
            }
        }
    }

    private suspend fun handleError(error: AppError) {
        val newEvent = when (error) {
            NetworkError -> ProductDetailEvent.NetworkError
            is InsufficientStock -> NotEnoughStock
            is AppError.UnknownError, DatabaseError, NotFoundError, QuantityMustBePositive -> UnknownError
        }
        _events.emit(value = newEvent)
    }
}

data class ProductDetailUiState(
    val item: ProductWithPromotionModel? = null,
    val isLoading: Boolean = true,
)

sealed interface ProductDetailEvent {
    data object UnknownError: ProductDetailEvent
    data object NetworkError: ProductDetailEvent
    data object NotEnoughStock: ProductDetailEvent
    data object SuccessAddToCart: ProductDetailEvent
}