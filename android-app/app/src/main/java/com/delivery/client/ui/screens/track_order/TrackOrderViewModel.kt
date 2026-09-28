package com.delivery.client.ui.screens.track_order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delivery.client.data.remote.ApiService
import com.delivery.client.data.remote.dto.OrderDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrackOrderState(
    val code: String = "",
    val order: OrderDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TrackOrderViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _state = MutableStateFlow(TrackOrderState())
    val state: StateFlow<TrackOrderState> = _state.asStateFlow()

    fun onCodeChange(value: String) {
        _state.value = _state.value.copy(code = value, error = null)
    }

    fun track() {
        val code = _state.value.code.trim()
        if (code.isEmpty()) {
            _state.value = _state.value.copy(error = "Unesite pickup kod")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, order = null)
            try {
                val orders = apiService.trackOrdersByCode(code)
                if (orders.isNotEmpty()) {
                    _state.value = _state.value.copy(
                        order = orders.first(),
                        isLoading = false
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Porudzbina nije pronadjena"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Greska pri pretrazi"
                )
            }
        }
    }
}