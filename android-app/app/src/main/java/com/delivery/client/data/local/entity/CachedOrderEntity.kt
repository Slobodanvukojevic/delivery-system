package com.delivery.client.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_orders")
data class CachedOrderEntity(
    @PrimaryKey val id: Long,
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
    val createdAt: String?,
    val cachedAt: Long = System.currentTimeMillis()
)