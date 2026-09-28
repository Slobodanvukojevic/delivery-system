package com.delivery.client.data.remote

import com.delivery.client.data.preferences.UserPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val userPreferences: UserPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { userPreferences.getToken() }

        val path = chain.request().url.encodedPath
        val isPublic = path.contains("/api/auth/") || path.contains("/api/orders/track")

        val request = if (token.isNullOrEmpty() || isPublic) {
            chain.request()
        } else {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        }

        return chain.proceed(request)
    }
}