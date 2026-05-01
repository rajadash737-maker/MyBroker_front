package com.example.myapplication.ui.feature.buyer.domain.model

data class Booking(
    val bookingId: Int,
    val status: String,
    val paymentStatus: String,
    val paymentMethod: String?,
    val transactionId: String?,
    val dealAmount: Double?,
    val bookingFeeAmount: Double?,
    val paidAt: String?
)
