package com.delivery.client.data.remote.dto

data class OrderDto(
    val id: Long,
    val senderName: String?,
    val senderPhone: String?,
    val customerName: String,
    val customerPhone: String,
    val weight: Double,
    val pickupAddress: String,
    val dropoffAddress: String,
    val status: String,
    val deliveryMethod: String,
    val pickupCode: String?,
    val selectedBranchId: Long?,
    val selectedLockerId: Long?,
    val assignedCourierId: Long?,
    val price: Double?,
    val createdAt: String?
)