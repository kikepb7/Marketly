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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val getCartSummaryUseCase: GetCartSummaryUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val getCartItemsWithPromotionsUseCase: GetCartItemsWithPromotionsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<CartUiState>(Loading)
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<CartEvent>(extraBufferCapacity = 1)
    val event = _events

    var cartJob: Job? = null

    init {
        loadCart()
    }

    fun loadCart() {
        _state.update { Loading }
        cartJob?.cancel()

        cartJob = combine(
            flow = getCartItemsWithPromotionsUseCase(),
            flow2 = getCartSummaryUseCase()
        ) { cartItemWithPromotion, summary ->
            _state.update { Success(summary = summary, cartItems = cartItemWithPromotion, isLoading = false) }
        }.catch { e ->
            _events.emit(value = CartEvent.ShowMessage(message = e.message.orEmpty()))
        }.launchIn(scope = viewModelScope)
    }

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