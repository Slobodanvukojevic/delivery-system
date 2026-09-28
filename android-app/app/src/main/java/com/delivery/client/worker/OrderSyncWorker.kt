package com.delivery.client.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.delivery.client.data.repository.OrderRepository
import com.delivery.client.notifications.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class OrderSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val orderRepository: OrderRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val result = orderRepository.syncOrders()
            result.fold(
                onSuccess = { changedCount ->
                    if (changedCount > 0) {
                        notificationHelper.showNotification(
                            "Promena statusa porudzbine",
                            "Imate $changedCount novih promena na vasim porudzbinama"
                        )
                    }
                    Result.success()
                },
                onFailure = {
                    Result.retry()
                }
            )
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "order_sync_worker"
    }
}