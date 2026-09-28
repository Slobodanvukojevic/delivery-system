package com.delivery.client.data.repository

import com.delivery.client.data.remote.ApiService
import com.delivery.client.data.remote.dto.NearbyLocationsResponse
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getNearbyLocations(lat: Double, lng: Double): Result<NearbyLocationsResponse> {
        return try {
            Result.success(apiService.getNearbyLocations(lat, lng, 10))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}