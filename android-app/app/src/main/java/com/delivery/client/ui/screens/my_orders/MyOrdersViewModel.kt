package com.delivery.client.ui.screens.my_orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delivery.client.data.remote.dto.OrderDto
import com.delivery.client.data.repository.OrderRepository
import com.delivery.client.notifications.NotificationHelper
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
    private val orderRepository: OrderRepository,
    private val notificationHelper: NotificationHelper
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
                onSuccess = { syncResult ->
                    syncResult.changes.forEach { change ->
                        val message = buildNotificationMessage(change)
                        notificationHelper.showNotification(
                            title = "Porudzbina #${change.orderId}",
                            message = message
                        )
                    }

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

    private fun buildNotificationMessage(change: OrderRepository.StatusChange): String {
        return when (change.newStatus) {
            "ACCEPTED_AT_BRANCH" -> "Vas paket je primljen u poslovnici"
            "IN_SORTING" -> "Vas paket je u fazi sortiranja"
            "IN_TRANSIT" -> "Vas paket je na putu"
            "PLACED_IN_LOCKER" -> "Vas paket je smesten u paketomat"
            "READY_FOR_BRANCH_PICKUP" -> "Vas paket je spreman za preuzimanje"
            "OUT_FOR_DELIVERY" -> "Kurir je na putu do vas"
            "DELIVERED" -> "Vas paket je uspesno isporucen"
            "PICKED_UP_BY_CUSTOMER" -> "Paket je preuzet. Hvala!"
            "RETURN_TO_SENDER" -> "Paket se vraca posiljaocu"
            else -> "Status porudzbine promenjen: ${change.newStatus}"
        }
    }
}
