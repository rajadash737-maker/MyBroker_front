package com.example.myapplication.network


import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ApiClient {


    private const val BASE_URL = "http://10.181.27.22:8001/"



    @Volatile
    private var initialized = false

    private lateinit var _apiService: ApiService
    val apiService: ApiService
        get() {
            check(initialized) { "ApiClient.init(context) must be called before using apiService" }
            return _apiService
        }


    fun absoluteUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        return if (path.startsWith("http")) path else BASE_URL.removeSuffix("/") + path
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

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        _apiService = retrofit.create(ApiService::class.java)
        initialized = true
    }
}
