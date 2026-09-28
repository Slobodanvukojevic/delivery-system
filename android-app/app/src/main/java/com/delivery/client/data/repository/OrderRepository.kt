package com.delivery.client.data.repository

import com.delivery.client.data.preferences.UserPreferences
import com.delivery.client.data.remote.ApiService
import com.delivery.client.data.remote.dto.CreateOrderRequest
import com.delivery.client.data.remote.dto.OpenLockerRequest
import com.delivery.client.data.remote.dto.OrderDto
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {

    suspend fun getMyOrders(): Result<List<OrderDto>> {
        return try {
            val phone = userPreferences.getPhone() ?: return Result.success(emptyList())
            val response = apiService.trackOrders(phone)
            Result.success(response.orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrder(id: Long): Result<OrderDto> {
        return try {
            Result.success(apiService.getOrder(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createOrder(request: CreateOrderRequest): Result<OrderDto> {
        return try {
            Result.success(apiService.createOrder(request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun openLocker(orderId: Long, pickupCode: String): Result<OrderDto> {
        return try {
            Result.success(apiService.openLocker(orderId, OpenLockerRequest(pickupCode)))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}