package com.example.myapplication.ui.feature.buyer.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BookingCreateRequestDto(
    @SerializedName("request_id")
    val requestId: Int,
    @SerializedName("buyer_name")
    val buyerName: String,
    @SerializedName("buyer_mobile")
    val buyerMobile: String,
    @SerializedName("aadhaar_number")
    val aadhaarNumber: String,
    @SerializedName("pan_number")
    val panNumber: String,
    @SerializedName("bank_account")
    val bankAccount: String
)
