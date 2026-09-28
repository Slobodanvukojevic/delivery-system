package com.delivery.client.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LocationDto(
    val id: Long,
    @SerializedName(value = "name", alternate = ["locationName"])
    val name: String? = null,
    val address: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

data class NearbyLocationsResponse(
    val branches: List<LocationDto> = emptyList(),
    val lockers: List<LocationDto> = emptyList()
)