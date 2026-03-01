package com.example.myapplication.network

import android.content.Context
import com.google.maps.android.ktx.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val BASE_URL = "http://10.208.82.22:8000/"

    @Volatile
    private var initialized = false

    private lateinit var retrofit: Retrofit
    private lateinit var _apiService: ApiService

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
        if (path.startsWith("http://") || path.startsWith("https://")) return path
        val normalized = if (path.startsWith("/")) path else "/$path"
        return BASE_URL.removeSuffix("/") + normalized
    }

    @Synchronized
    fun init(context: Context) {
        if (initialized) return

        val sessionManager = SessionManager(context.applicationContext)

        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
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
