package com.delivery.client.data.remote.dto

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val phone: String,
    val role: String,
    val branchId: Long? = null
)