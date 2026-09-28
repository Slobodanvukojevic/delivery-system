package com.delivery.client.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.delivery.client.data.local.entity.CachedOrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Query("SELECT * FROM cached_orders ORDER BY id DESC")
    fun getAllOrders(): Flow<List<CachedOrderEntity>>

    @Query("SELECT * FROM cached_orders WHERE id = :id")
    suspend fun getOrderById(id: Long): CachedOrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<CachedOrderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: CachedOrderEntity)

    @Query("DELETE FROM cached_orders")
    suspend fun clearAll()
}