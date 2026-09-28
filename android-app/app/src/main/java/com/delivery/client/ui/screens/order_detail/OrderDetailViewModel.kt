package com.delivery.client.ui.screens.order_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delivery.client.data.remote.dto.OrderDto
import com.delivery.client.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderDetailState(
    val order: OrderDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val pickupCodeInput: String = "",
    val lockerMessage: String? = null
)

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val orderId: Long = savedStateHandle.get<String>("id")?.toLongOrNull() ?: 0L

    private val _state = MutableStateFlow(OrderDetailState())
    val state: StateFlow<OrderDetailState> = _state.asStateFlow()

    init {
        loadOrder()
    }

    fun loadOrder() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = orderRepository.getOrder(orderId)

            result.fold(
                onSuccess = { order ->
                    _state.value = _state.value.copy(order = order, isLoading = false)
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Greska"
                    )
                }
            )
        }
    }

    fun onPickupCodeChange(v: String) {
        _state.value = _state.value.copy(pickupCodeInput = v, lockerMessage = null)
    }

    fun openLocker() {
        val current = _state.value
        if (current.pickupCodeInput.isBlank()) {
            _state.value = current.copy(lockerMessage = "Unesite kod")
            return
        }

        viewModelScope.launch {
            _state.value = current.copy(isLoading = true, lockerMessage = null)
            val result = orderRepository.openLocker(orderId, current.pickupCodeInput)

            result.fold(
                onSuccess = { order ->
                    _state.value = _state.value.copy(
                        order = order,
                        isLoading = false,
                        lockerMessage = "Sanduce otvoreno. Hvala!",
                        pickupCodeInput = ""
                    )
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        lockerMessage = e.message ?: "Pogresan kod"
                    )
                }
            )
        }
    }
}