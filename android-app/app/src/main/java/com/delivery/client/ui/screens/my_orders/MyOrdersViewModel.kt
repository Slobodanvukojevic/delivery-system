package com.delivery.client.ui.screens.my_orders

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

data class MyOrdersState(
    val orders: List<OrderDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val fromCache: Boolean = false
)

@HiltViewModel
class MyOrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MyOrdersState())
    val state: StateFlow<MyOrdersState> = _state.asStateFlow()

    init {
        observeCachedOrders()
        loadOrders()
    }

    private fun observeCachedOrders() {
        viewModelScope.launch {
            orderRepository.getCachedOrders().collect { orders ->
                if (orders.isNotEmpty()) {
                    _state.value = _state.value.copy(orders = orders)
                }
            }
        }
    }

    fun loadOrders() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = orderRepository.syncOrders()

            result.fold(
                onSuccess = {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = null,
                        fromCache = false
                    )
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Nema interneta. Prikazujem kesirane podatke.",
                        fromCache = true
                    )
                }
            )
        }
    }
}