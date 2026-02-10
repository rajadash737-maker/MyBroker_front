package com.example.myapplication.network

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val sessionManager: SessionManager,
    private val refreshApi: ApiService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        val path = response.request.url.encodedPath
        if (path.contains("/auth/refresh") || path.contains("/auth/login")) return null

        val refreshToken = sessionManager.getRefreshToken() ?: return null

        val newAccessToken = runBlocking {
            try {
                val refreshResponse = refreshApi.refresh(RefreshRequest(refreshToken))
                if (!refreshResponse.isSuccessful) {
                    sessionManager.clearSession()
                    return@runBlocking null
                }

                val body = refreshResponse.body() ?: run {
                    sessionManager.clearSession()
                    return@runBlocking null
                }

                val tokens = body.tokens ?: run {
                    sessionManager.clearSession()
                    return@runBlocking null
                }

                sessionManager.saveTokens(
                    accessToken = tokens.access_token,
                    refreshToken = tokens.refresh_token
                )
                tokens.access_token
            } catch (_: Exception) {
                sessionManager.clearSession()
                null
            }
        } ?: return null

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
