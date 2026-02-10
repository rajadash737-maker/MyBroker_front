package com.example.myapplication.network

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface ApiService {

    @POST("auth/send-otp")
    suspend fun sendOtp(@Body body: SendOtpRequest): Response<StatusResponse>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body body: VerifyOtpRequest): Response<StatusResponse>

    @POST("auth/signup")
    suspend fun signup(@Body body: SignupRequest): Response<StatusResponse>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @POST("auth/refresh")
    suspend fun refresh(@Body body: RefreshRequest): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(@Body body: LogoutRequest): Response<StatusResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body body: ResetPasswordRequest): Response<StatusResponse>

    @GET("users/me/profile")
    suspend fun getMyProfile(): Response<UserProfileResponse>

    @PUT("users/me/profile")
    suspend fun updateMyProfile(@Body body: UpdateProfileRequest): Response<UserProfileResponse>

    @Multipart
    @POST("users/me/profile-photo")
    suspend fun uploadProfilePhoto(@Part file: MultipartBody.Part): Response<ProfilePhotoResponse>
}
