package com.delivery.client.data.remote.dto

data class CreateOrderRequest(
    val senderName: String,
    val senderPhone: String,
    val customerName: String,
    val customerPhone: String,
    val weight: Double,
    val pickupAddress: String,
    val dropoffAddress: String,
    val deliveryMethod: String,
    val selectedLockerId: Long? = null
)