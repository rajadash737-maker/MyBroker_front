package com.example.myapplication.network

import android.content.Context
import android.net.Uri
import com.example.myapplication.core.config.AppEnv
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val BASE_URL = AppEnv.BASE_URL

    @Volatile
    private var initialized = false

    private lateinit var retrofit: Retrofit
    private lateinit var _apiService: ApiService

    private val baseUri: Uri by lazy { Uri.parse(BASE_URL) }

    val apiService: ApiService
        get() {
            check(initialized) { "ApiClient.init(context) must be called before using apiService" }
            return _apiService
        }

    fun <T> createService(serviceClass: Class<T>): T {
        check(initialized) { "ApiClient.init(context) must be called before using createService()" }
        return retrofit.create(serviceClass)
    }

    fun absoluteUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null

        val cleaned = path.trim().replace("\\", "/")
        val lower = cleaned.lowercase()

        if (lower.startsWith("content://") || lower.startsWith("file://")) return cleaned

        if (lower.startsWith("http://") || lower.startsWith("https://")) {
            val uri = Uri.parse(cleaned)
            val host = uri.host?.lowercase().orEmpty()
            val baseHost = baseUri.host?.lowercase().orEmpty()

            val isLocalLike =
                host == "localhost" ||
                        host == "127.0.0.1" ||
                        host == "0.0.0.0" ||
                        host.startsWith("10.") ||
                        host.startsWith("192.168.") ||
                        host.startsWith("172.16.") ||
                        host.startsWith("172.17.") ||
                        host.startsWith("172.18.") ||
                        host.startsWith("172.19.") ||
                        host.startsWith("172.2") ||
                        host.startsWith("172.30.") ||
                        host.startsWith("172.31.")

            if (isLocalLike && host != baseHost) {
                return uri.buildUpon()
                    .scheme(baseUri.scheme)
                    .encodedAuthority(baseUri.encodedAuthority)
                    .build()
                    .toString()
            }
            return cleaned
        }

        val normalizedPath = if (cleaned.startsWith("/")) cleaned else "/$cleaned"
        return BASE_URL.removeSuffix("/") + normalizedPath
    }

    @Synchronized
    fun init(context: Context) {
        if (initialized) return

        val sessionManager = SessionManager(context.applicationContext)

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val refreshRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val refreshApi = refreshRetrofit.create(ApiService::class.java)

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager))
            .authenticator(TokenAuthenticator(sessionManager, refreshApi))
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        _apiService = retrofit.create(ApiService::class.java)
        initialized = true
    }
}
