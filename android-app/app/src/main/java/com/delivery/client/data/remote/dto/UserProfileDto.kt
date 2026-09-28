package com.delivery.client.data.remote.dto

data class UserProfileDto(
    val id: Long,
    val email: String?,
    val fullName: String?,
    val phone: String?,
    val role: String?,
    val branchId: Long?,
    val active: Boolean?
)