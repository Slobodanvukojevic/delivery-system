package com.delivery.client.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.delivery.client.data.local.dao.OrderDao
import com.delivery.client.data.local.entity.CachedOrderEntity

@Database(
    entities = [CachedOrderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
}