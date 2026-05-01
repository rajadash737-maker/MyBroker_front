package com.example.myapplication.ui.feature.buyer.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BookingCreateResponseDto(
    @SerializedName("status")
    val status: String,
    @SerializedName("booking_id")
    val bookingId: Int,
    @SerializedName("payment_status")
    val paymentStatus: String,
    @SerializedName("deal_amount")
    val dealAmount: Double,
    @SerializedName("booking_fee_amount")
    val bookingFeeAmount: Double
)
