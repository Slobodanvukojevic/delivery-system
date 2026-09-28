package com.delivery.client.data.remote

import com.delivery.client.data.remote.dto.*
import retrofit2.http.*

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("api/orders/track")
    suspend fun trackOrders(@Query("phone") phone: String): TrackResponse

    @GET("api/orders/{id}")
    suspend fun getOrder(@Path("id") id: Long): OrderDto

    @POST("api/orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): OrderDto

    @POST("api/orders/{id}/open-locker")
    suspend fun openLocker(
        @Path("id") id: Long,
        @Body request: OpenLockerRequest
    ): OrderDto
}