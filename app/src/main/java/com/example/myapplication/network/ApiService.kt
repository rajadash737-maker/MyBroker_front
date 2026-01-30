package com.example.myapplication.network


import retrofit2.Call



import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET

import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    // SEND OTP
    @FormUrlEncoded
    @POST("send_otp.php")
    suspend fun sendOtp(
        @Field("mobile") mobile: String
    ): Response<SendOtpResponse>

    // VERIFY OTP FOR SIGNUP SCREEN (success = true)
    @FormUrlEncoded
    @POST("verify_otp.php")
    suspend fun verifyOtp(
        @Field("mobile") mobile: String,
        @Field("otp") otp: String
    ): Response<BasicResponse>

    // VERIFY OTP FOR FORGOT SCREEN (status = "success")
    @FormUrlEncoded
    @POST("verify_otp.php")
    suspend fun verifyOtpForForgot(
        @Field("mobile") mobile: String,
        @Field("otp") otp: String
    ): Response<OtpVerifyResponse>

    // SIGNUP
    @FormUrlEncoded
    @POST("signup.php")
    suspend fun signup(
        @Field("fullname") fullname: String,
        @Field("email") email: String,
        @Field("mobile") mobile: String,
        @Field("otp") otp: String,
        @Field("password") password: String,
        @Field("confirm") confirm: String
    ): Response<BasicResponse>

    // LOGIN
    @FormUrlEncoded
    @POST("login.php")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    // RESET PASSWORD
    @FormUrlEncoded
    @POST("reset_password.php")
    suspend fun resetPassword(
        @Field("mobile") mobile: String,
        @Field("password") password: String
    ): Response<BasicResponse>

    // Bydefault profile
    @FormUrlEncoded
    @POST("get_profile.php")
    suspend fun getProfile(
        @Field("user_id") userId: String
    ): ProfileResponse

    // Update profile
    @FormUrlEncoded
    @POST("update_profile.php")
    suspend fun updateProfile(
        @Field("user_id") userId: String,
        @Field("fullname") fullName: String,
        @Field("username") userName: String,
        @Field("email") email: String,
        @Field("mobile") mobile: String,
        @Field("address") address: String,
        @Field("dob") dob: String,
        @Field("gender") gender: String,
        @Field("profile_image") profileImage: String   // ✅ ADD THIS
    ): BasicResponse

    @Multipart
    @POST("upload_profile_image.php")
    suspend fun uploadProfileImage(
        @Part("user_id") userId: RequestBody,
        @Part image: MultipartBody.Part
    ): UploadResponse


//--------------------------------------------------------------=-------------------


}












