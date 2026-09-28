package com.delivery.client.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delivery.client.data.remote.dto.LocationDto
import com.delivery.client.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapState(
    val lockers: List<LocationDto> = emptyList(),
    val branches: List<LocationDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedLocation: LocationDto? = null
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state.asStateFlow()

    private val defaultLat = 44.8178
    private val defaultLng = 20.4569

    fun loadLocations(lat: Double = defaultLat, lng: Double = defaultLng) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = locationRepository.getNearbyLocations(lat, lng)

            result.fold(
                onSuccess = { response ->
                    _state.value = _state.value.copy(
                        lockers = response.lockers,
                        branches = response.branches,
                        isLoading = false
                    )
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Greska pri ucitavanju lokacija"
                    )
                }
            )
        }
    }

    fun onMarkerClick(location: LocationDto) {
        _state.value = _state.value.copy(selectedLocation = location)
    }

    fun clearSelection() {
        _state.value = _state.value.copy(selectedLocation = null)
    }
}