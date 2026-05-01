package com.example.myapplication.network

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.json.JSONObject




class SessionManager(context: Context) {

    private val prefs = EncryptedSharedPreferences.create(
        context,
        PREF_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    fun saveFcmToken(token: String) {
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply()
    }

    fun getFcmToken(): String? = prefs.getString(KEY_FCM_TOKEN, null)

    fun saveFcmTokenSynced(token: String) {
        prefs.edit().putString(KEY_FCM_TOKEN_SYNCED, token).apply()
    }

    fun getFcmTokenSynced(): String? = prefs.getString(KEY_FCM_TOKEN_SYNCED, null)

    fun getUserId(): Int? {
        val token = getAccessToken() ?: return null
        return runCatching {
            val parts = token.split(".")
            if (parts.size < 2) return null
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val sub = JSONObject(payload).optString("sub", "")
            sub.toIntOrNull()
        }.getOrNull()
    }

    fun isLoggedIn(): Boolean {
        val refresh = getRefreshToken()
        return !refresh.isNullOrBlank() && isTokenValid(refresh)
    }

    private fun isTokenValid(token: String): Boolean {
        return runCatching {
            val parts = token.split(".")
            if (parts.size < 2) return false
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val exp = JSONObject(payload).optLong("exp", 0L)
            if (exp <= 0L) return false
            val nowSeconds = System.currentTimeMillis() / 1000
            exp > (nowSeconds + 30) // small clock skew buffer
        }.getOrDefault(false)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREF_NAME = "mybroker_secure_session"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_FCM_TOKEN = "fcm_token"
        private const val KEY_FCM_TOKEN_SYNCED = "fcm_token_synced"
    }
}
