package com.delivery.client.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val TOKEN_KEY = stringPreferencesKey("token")
        val USER_ID_KEY = stringPreferencesKey("userId")
        val FULL_NAME_KEY = stringPreferencesKey("fullName")
        val ROLE_KEY = stringPreferencesKey("role")
        val PHONE_KEY = stringPreferencesKey("phone")

        val FCM_TOKEN_KEY = stringPreferencesKey("fcmToken")
    }

    suspend fun saveAuth(token: String, userId: Long, fullName: String, role: String, phone: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USER_ID_KEY] = userId.toString()
            prefs[FULL_NAME_KEY] = fullName
            prefs[ROLE_KEY] = role
            prefs[PHONE_KEY] = phone
        }
    }

    suspend fun getToken(): String? =
        context.dataStore.data.map { it[TOKEN_KEY] }.first()

    suspend fun getUserId(): Long? =
        context.dataStore.data.map { it[USER_ID_KEY]?.toLongOrNull() }.first()

    suspend fun getFullName(): String? =
        context.dataStore.data.map { it[FULL_NAME_KEY] }.first()

    suspend fun getRole(): String? =
        context.dataStore.data.map { it[ROLE_KEY] }.first()

    suspend fun getPhone(): String? =
        context.dataStore.data.map { it[PHONE_KEY] }.first()

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun saveFcmToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[FCM_TOKEN_KEY] = token
        }
    }

    suspend fun getFcmToken(): String? =
        context.dataStore.data.map { it[FCM_TOKEN_KEY] }.first()
}