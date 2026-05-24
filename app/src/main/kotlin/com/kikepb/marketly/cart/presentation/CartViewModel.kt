package com.kikepb.marketly.cart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kikepb.marketly.cart.domain.model.CartSummaryModel
import com.kikepb.marketly.cart.domain.repository.CartRepository
import com.kikepb.marketly.cart.domain.usecase.GetCartItemsWithPromotionsUseCase
import com.kikepb.marketly.cart.domain.usecase.GetCartSummaryUseCase
import com.kikepb.marketly.cart.domain.usecase.UpdateCartItemUseCase
import com.kikepb.marketly.cart.presentation.CartUiState.Loading
import com.kikepb.marketly.cart.presentation.CartUiState.Success
import com.kikepb.marketly.cart.presentation.model.CartItemWithPromotionsUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    getCartSummaryUseCase: GetCartSummaryUseCase,
    getCartItemsWithPromotionsUseCase: GetCartItemsWithPromotionsUseCase
) : ViewModel() {

    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val state = combine(
        flow = refreshTrigger.onStart { emit(value = Unit) },
        flow2 = getCartItemsWithPromotionsUseCase(),
        flow3 = getCartSummaryUseCase()
    ) { _, cartItemWithPromotion, summary ->
        Success(summary = summary, cartItems = cartItemWithPromotion, isLoading = false)
    }.catch { e ->
        _events.emit(value = CartEvent.ShowMessage(message = e.message.orEmpty()))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = Loading
    )

    private val _events = MutableSharedFlow<CartEvent>(extraBufferCapacity = 1)
    val event = _events

    fun updateCartItem(productId: String, quantity: Int) {
        viewModelScope.launch {
            try {
                updateCartItemUseCase(productId = productId, quantity = quantity)
            } catch (e: Exception) {
                _events.emit(value = CartEvent.ShowMessage(message = e.message.orEmpty()))            }
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            try {
                cartRepository.removeFromCart(productId = productId)
            } catch (e: Exception) {
                _events.emit(value = CartEvent.ShowMessage(message = e.message.orEmpty()))            }
            }
    }

    fun increaseQuantity(productId: String, currentQuantity: Int) =
        updateCartItem(productId = productId, quantity = currentQuantity + 1)

    fun reduceQuantity(productId: String, currentQuantity: Int) {
        if (currentQuantity > 1) updateCartItem(productId = productId, quantity = currentQuantity - 1)
        else removeFromCart(productId = productId)
    }

    fun refresh() = refreshTrigger.tryEmit(value = Unit)
}

sealed class CartUiState {
    data class Success(
        val summary: CartSummaryModel? = null,
        val cartItems: List<CartItemWithPromotionsUiModel>,
        val isLoading: Boolean
    ) : CartUiState()

    data class Error(val message: String): CartUiState()

    data object Loading: CartUiState()
}

sealed interface CartEvent {
    data class ShowMessage(val message: String) : CartEvent
}