package com.example.myapplication.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val path = original.url.encodedPath

        val isAuthEndpoint = path.contains("/auth/login") ||
                path.contains("/auth/refresh") ||
                path.contains("/auth/send-otp") ||
                path.contains("/auth/verify-otp") ||
                path.contains("/auth/firebase/verify") ||
                path.contains("/auth/signup") ||
                path.contains("/auth/reset-password")

        if (isAuthEndpoint) return chain.proceed(original)

        val accessToken = sessionManager.getAccessToken()
        val request = if (!accessToken.isNullOrBlank()) {
            original.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else original

        return chain.proceed(request)
    }
}
