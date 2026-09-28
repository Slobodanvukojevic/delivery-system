package com.delivery.client.data.remote.dto

data class AuthResponse(
    val token: String,
    val userId: Long,
    val fullName: String,
    val role: String,
    val branchId: Long?,
    val message: String
)