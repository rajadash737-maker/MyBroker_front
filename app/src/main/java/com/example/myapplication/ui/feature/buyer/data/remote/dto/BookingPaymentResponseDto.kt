package com.example.myapplication.ui.feature.buyer.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BookingPaymentResponseDto(
    @SerializedName("status")
    val status: String,
    @SerializedName("booking_id")
    val bookingId: Int,
    @SerializedName("payment_status")
    val paymentStatus: String,
    @SerializedName("payment_method")
    val paymentMethod: String?,
    @SerializedName("transaction_id")
    val transactionId: String?,
    @SerializedName("paid_at")
    val paidAt: String?
)
