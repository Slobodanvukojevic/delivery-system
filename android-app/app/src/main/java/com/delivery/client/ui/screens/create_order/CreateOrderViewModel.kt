package com.delivery.client.ui.screens.create_order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delivery.client.data.preferences.UserPreferences
import com.delivery.client.data.remote.dto.CreateOrderRequest
import com.delivery.client.data.remote.dto.LocationDto
import com.delivery.client.data.repository.LocationRepository
import com.delivery.client.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateOrderState(
    val senderName: String = "",
    val senderPhone: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val weight: String = "1",
    val pickupAddress: String = "",
    val dropoffAddress: String = "",
    val deliveryMethod: String = "HOME_DELIVERY",
    val selectedLockerId: Long? = null,
    val selectedLockerName: String = "",
    val selectedBranchId: Long? = null,
    val selectedBranchName: String = "",
    val branches: List<LocationDto> = emptyList(),
    val isLoadingBranches: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class CreateOrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val userPreferences: UserPreferences,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateOrderState())
    val state: StateFlow<CreateOrderState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val fullName = userPreferences.getFullName() ?: ""
            val phone = userPreferences.getPhone() ?: ""
            _state.value = _state.value.copy(senderName = fullName, senderPhone = phone)
        }
        loadBranches()
    }

    private fun loadBranches() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingBranches = true)
            val result = locationRepository.getNearbyLocations(44.8178, 20.4569)
            result.fold(
                onSuccess = { response ->
                    _state.value = _state.value.copy(
                        branches = response.branches,
                        isLoadingBranches = false
                    )
                },
                onFailure = {
                    _state.value = _state.value.copy(isLoadingBranches = false)
                }
            )
        }
    }

    fun onSenderNameChange(v: String) { _state.value = _state.value.copy(senderName = v, error = null) }
    fun onSenderPhoneChange(v: String) { _state.value = _state.value.copy(senderPhone = v, error = null) }
    fun onCustomerNameChange(v: String) { _state.value = _state.value.copy(customerName = v, error = null) }
    fun onCustomerPhoneChange(v: String) { _state.value = _state.value.copy(customerPhone = v, error = null) }
    fun onWeightChange(v: String) { _state.value = _state.value.copy(weight = v, error = null) }
    fun onPickupAddressChange(v: String) { _state.value = _state.value.copy(pickupAddress = v, error = null) }
    fun onDropoffAddressChange(v: String) { _state.value = _state.value.copy(dropoffAddress = v, error = null) }
    fun onDeliveryMethodChange(v: String) { _state.value = _state.value.copy(deliveryMethod = v, error = null) }

    fun onLockerSelected(id: Long, name: String) {
        _state.value = _state.value.copy(
            selectedLockerId = id,
            selectedLockerName = name
        )
    }

    fun clearLocker() {
        _state.value = _state.value.copy(selectedLockerId = null, selectedLockerName = "")
    }

    fun onBranchSelected(branch: LocationDto) {
        _state.value = _state.value.copy(
            selectedBranchId = branch.id,
            selectedBranchName = branch.name ?: "",
            dropoffAddress = branch.address ?: ""
        )
    }

    fun clearBranch() {
        _state.value = _state.value.copy(
            selectedBranchId = null,
            selectedBranchName = "",
            dropoffAddress = ""
        )
    }

    fun submit() {
        val current = _state.value

        if (current.senderName.isBlank() || current.senderPhone.isBlank() ||
            current.customerName.isBlank() || current.customerPhone.isBlank() ||
            current.pickupAddress.isBlank()) {
            _state.value = current.copy(error = "Popunite sva polja")
            return
        }

        if (current.deliveryMethod != "BRANCH_PICKUP" && current.dropoffAddress.isBlank()) {
            _state.value = current.copy(error = "Popunite sva polja")
            return
        }

        if (current.deliveryMethod == "LOCKER_PICKUP" && current.selectedLockerId == null) {
            _state.value = current.copy(error = "Izaberite paketomat")
            return
        }

        if (current.deliveryMethod == "BRANCH_PICKUP" && current.selectedBranchId == null) {
            _state.value = current.copy(error = "Izaberite poslovnicu")
            return
        }

        val weightValue = current.weight.toDoubleOrNull()
        if (weightValue == null || weightValue < 1 || weightValue > 30) {
            _state.value = current.copy(error = "Tezina mora biti izmedju 1 i 30 kg")
            return
        }

        viewModelScope.launch {
            _state.value = current.copy(isLoading = true, error = null)
            val result = orderRepository.createOrder(
                CreateOrderRequest(
                    senderName = current.senderName,
                    senderPhone = current.senderPhone,
                    customerName = current.customerName,
                    customerPhone = current.customerPhone,
                    weight = weightValue,
                    pickupAddress = current.pickupAddress,
                    dropoffAddress = current.dropoffAddress,
                    deliveryMethod = current.deliveryMethod,
                    selectedLockerId = current.selectedLockerId,
                    selectedBranchId = current.selectedBranchId
                )
            )

            result.fold(
                onSuccess = { _state.value = _state.value.copy(isLoading = false, success = true) },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Greska pri kreiranju"
                    )
                }
            )
        }
    }
}