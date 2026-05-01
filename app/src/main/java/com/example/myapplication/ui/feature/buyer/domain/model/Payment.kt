package com.example.myapplication.ui.feature.buyer.domain.model

data class PaymentOrder(
    val orderId: String,
    val amount: Long,
    val currency: String
)

data class PaymentVerification(
    val status: String,
    val orderId: String,
    val paymentId: String
)

