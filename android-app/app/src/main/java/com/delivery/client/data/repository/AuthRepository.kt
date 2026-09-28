package com.delivery.client.data.repository

import android.util.Log
import com.delivery.client.data.preferences.UserPreferences
import com.delivery.client.data.remote.ApiService
import com.delivery.client.data.remote.dto.LoginRequest
import com.delivery.client.data.remote.dto.RegisterRequest
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            userPreferences.saveAuth(
                token = response.token,
                userId = response.userId,
                fullName = response.fullName,
                role = response.role,
                phone = "+381641234567"
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        phone: String
    ): Result<Unit> {
        return try {
            val response = apiService.register(
                RegisterRequest(email, password, fullName, phone, "CUSTOMER")
            )
            userPreferences.saveAuth(
                token = response.token,
                userId = response.userId,
                fullName = response.fullName,
                role = response.role,
                phone = phone
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        userPreferences.clear()
    }

    suspend fun isLoggedIn(): Boolean {
        return !userPreferences.getToken().isNullOrEmpty()
    }

    suspend fun registerFcmToken() {
        try {
            val token = FirebaseMessaging.getInstance().token.await()
            userPreferences.saveFcmToken(token)
            Log.d("AuthRepository", "FCM token: $token")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Greska pri dohvatanju FCM tokena: ${e.message}")
        }
    }
}