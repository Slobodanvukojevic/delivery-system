package com.delivery.client.data.repository

import com.delivery.client.data.local.dao.OrderDao
import com.delivery.client.data.local.entity.CachedOrderEntity
import com.delivery.client.data.preferences.UserPreferences
import com.delivery.client.data.remote.ApiService
import com.delivery.client.data.remote.dto.CreateOrderRequest
import com.delivery.client.data.remote.dto.OpenLockerRequest
import com.delivery.client.data.remote.dto.OrderDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences,
    private val orderDao: OrderDao
) {

    fun getCachedOrders(): Flow<List<OrderDto>> {
        return orderDao.getAllOrders().map { entities ->
            entities.map { it.toDto() }
        }
    }

    suspend fun syncOrders(): Result<SyncResult> {
        return try {
            val phone = userPreferences.getPhone() ?: return Result.success(SyncResult(0, emptyList()))
            val orders = apiService.trackOrders(phone)

            val oldOrders = orderDao.getAllOrdersSync()
            val oldStatusMap = oldOrders.associate { it.id to it.status }


            val changes = mutableListOf<StatusChange>()
            for (newOrder in orders) {
                val oldStatus = oldStatusMap[newOrder.id]
                if (oldStatus != null && oldStatus != newOrder.status) {
                    changes.add(
                        StatusChange(
                            orderId = newOrder.id,
                            oldStatus = oldStatus,
                            newStatus = newOrder.status,
                            customerName = newOrder.customerName
                        )
                    )
                }
            }


            orderDao.insertOrders(orders.map { it.toEntity() })

            Result.success(SyncResult(changes.size, changes))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    data class SyncResult(
        val changedCount: Int,
        val changes: List<StatusChange>
    )

    data class StatusChange(
        val orderId: Long,
        val oldStatus: String,
        val newStatus: String,
        val customerName: String
    )

    suspend fun getMyOrders(): Result<List<OrderDto>> {
        return try {
            val phone = userPreferences.getPhone() ?: return Result.success(emptyList())
            val orders = apiService.trackOrders(phone)
            orderDao.insertOrders(orders.map { it.toEntity() })
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrder(id: Long): Result<OrderDto> {
        return try {
            val order = apiService.getOrder(id)
            orderDao.insertOrder(order.toEntity())
            Result.success(order)
        } catch (e: Exception) {
            // Fallback na kes
            val cached = orderDao.getOrderById(id)
            if (cached != null) {
                Result.success(cached.toDto())
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun createOrder(request: CreateOrderRequest): Result<OrderDto> {
        return try {
            val order = apiService.createOrder(request)
            orderDao.insertOrder(order.toEntity())
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun openLocker(orderId: Long, pickupCode: String): Result<OrderDto> {
        return try {
            val order = apiService.openLocker(orderId, OpenLockerRequest(pickupCode))
            orderDao.insertOrder(order.toEntity())
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun detectStatusChanges(newOrders: List<CachedOrderEntity>): Int {
        var changed = 0
        for (newOrder in newOrders) {
            val oldOrder = orderDao.getOrderById(newOrder.id)
            if ((oldOrder != null) && (oldOrder.status != newOrder.status)) {
                changed++
            }
        }
        return changed
    }
}


fun OrderDto.toEntity(): CachedOrderEntity {
    return CachedOrderEntity(
        id = id,
        senderName = senderName,
        senderPhone = senderPhone,
        customerName = customerName,
        customerPhone = customerPhone,
        weight = weight,
        pickupAddress = pickupAddress,
        dropoffAddress = dropoffAddress,
        status = status,
        deliveryMethod = deliveryMethod,
        pickupCode = pickupCode,
        selectedBranchId = selectedBranchId,
        selectedLockerId = selectedLockerId,
        assignedCourierId = assignedCourierId,
        price = price,
        createdAt = createdAt
    )
}

fun CachedOrderEntity.toDto(): OrderDto {
    return OrderDto(
        id = id,
        senderName = senderName,
        senderPhone = senderPhone,
        customerName = customerName,
        customerPhone = customerPhone,
        weight = weight,
        pickupAddress = pickupAddress,
        dropoffAddress = dropoffAddress,
        status = status,
        deliveryMethod = deliveryMethod,
        pickupCode = pickupCode,
        selectedBranchId = selectedBranchId,
        selectedLockerId = selectedLockerId,
        assignedCourierId = assignedCourierId,
        price = price,
        createdAt = createdAt
    )
}