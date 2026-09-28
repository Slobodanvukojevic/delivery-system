package com.delivery.client.notifications

import android.util.Log
import com.delivery.client.data.remote.ApiService
import com.delivery.client.data.preferences.UserPreferences
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DeliveryFirebaseService : FirebaseMessagingService() {

    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var userPreferences: UserPreferences
    @Inject lateinit var apiService: ApiService

    companion object {
        private const val TAG = "FCMService"
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Novi FCM token: $token")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                userPreferences.saveFcmToken(token)
                Log.d(TAG, "Token sacuvan lokalno")
            } catch (e: Exception) {
                Log.e(TAG, "Greska pri cuvanju tokena: ${e.message}")
            }
        }
    }


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: "Delivery"
        val body = message.notification?.body ?: "Nova notifikacija"

        notificationHelper.showNotification(title, body)

        Log.d(TAG, "Primljena notifikacija: $title - $body")
    }
}