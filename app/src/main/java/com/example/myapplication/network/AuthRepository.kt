package com.example.myapplication.network

class AuthRepository(
    private val api: ApiService,
    private val session: SessionManager
) {
    suspend fun login(identifier: String, password: String): Result<Unit> = runCatching {
        val res = api.login(LoginRequest(identifier = identifier, password = password))
        if (!res.isSuccessful) error("Login failed: ${res.code()}")

        val body = res.body() ?: error("Empty response")
        val tokens = body.tokens ?: error(body.status)

        session.saveTokens(tokens.access_token, tokens.refresh_token)
    }

    suspend fun logout() {
        val refresh = session.getRefreshToken()
        if (!refresh.isNullOrBlank()) {
            runCatching { api.logout(LogoutRequest(refresh_token = refresh)) }
        }
        session.clearSession()
    }

    fun isLoggedIn(): Boolean = session.isLoggedIn()
}
