package com.example.myapplication.network
import com.google.gson.annotations.SerializedName

data class StatusResponse(val status: String)

data class SendOtpRequest(
    val mobile: String,
    val purpose: String = "signup"
)

data class VerifyOtpRequest(
    val mobile: String,
    val otp: String,
    val purpose: String = "signup"
)

data class SignupRequest(
    val fullname: String,
    val email: String,
    val mobile: String,
    val password: String,
    val confirm: String
)

data class LoginRequest(
    val identifier: String,
    val password: String
)

data class ResetPasswordRequest(
    val mobile: String,
    val password: String,
    val confirm: String
)

data class TokenPair(
    val access_token: String,
    val refresh_token: String,
    val token_type: String,
    val access_expires_in_seconds: Int,
    val refresh_expires_in_seconds: Int
)

data class AuthResponse(
    val status: String,
    val user_id: Int? = null,
    val tokens: TokenPair? = null
)

data class UserProfileResponse(
    val id: Int? = null,
    val fullname: String? = null,
    val username: String? = null,
    val email: String? = null,
    val mobile: String? = null,
    @SerializedName("profile_image_url") val profileImageUrl: String? = null,
    val address: String? = null,
    @SerializedName("date_of_birth") val dateOfBirth: String? = null,
    val gender: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)


data class UpdateProfileRequest(
    val fullname: String? = null,
    val username: String? = null,
    val email: String? = null,
    val mobile: String? = null,
    val address: String? = null,
    @SerializedName("date_of_birth") val dateOfBirth: String? = null,
    val gender: String? = null
)

data class ProfilePhotoResponse(
    val status: String,
    @SerializedName("profile_image_url") val profileImageUrl: String
)






data class RefreshRequest(val refresh_token: String)
data class LogoutRequest(val refresh_token: String)




