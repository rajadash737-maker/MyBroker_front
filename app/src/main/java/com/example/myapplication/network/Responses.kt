//package com.myapplication.network
package com.example.myapplication.network

import com.google.gson.annotations.SerializedName

 //For verify_otp.php, signup.php, general responses
data class BasicResponse(
    val success: Boolean,
    val message: String,
    val file: String? = null
)



// For send_otp.php
data class SendOtpResponse(
    val status: String,
    val message: String,
    val success: Boolean,
    val otp_demo: String? = null
)

// For login.php
data class LoginResponse(
    val status: String,
    val success: Boolean,
    val message: String,
    val user: UserData?
)

data class UserData(
    val id: String,
    val fullname: String,
    val email: String,
    val mobile: String
)

// For reset_password.php
data class ApiResponse(
    val success: Boolean,
    val message: String
)

data class OtpVerifyResponse(
    val success: Boolean,
    val message: String
)

// Profile Response

data class ProfileResponse(
    val success: Boolean?,
    val fullname: String?,
    val username: String?,
    val email: String?,
    val mobile: String?,
    val address: String?,
    val dob: String?,
    val gender: String?
)


data class UploadResponse(
    val success: Boolean,
    val message: String,
    val file: String?
)


//--------------------------------------------------------------------------------------------------







