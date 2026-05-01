package com.example.myapplication.ui.feature.buyer.payment

import android.net.Uri

data class BookNowState(
    val step: BookNowStep = BookNowStep.DETAILS,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val alreadyBooked: Boolean = false,
    val isPaymentLoading: Boolean = false,
    val paymentError: String? = null,
    val paymentCompleted: Boolean = false,

    val bookingId: Int? = null,
    val dealAmount: Double? = null,
    val bookingFeeAmount: Double? = 1000.0,
    val razorpayOrderId: String? = null,
    val razorpayAmount: Long? = null,
    val razorpayCurrency: String? = null,
    val checkoutReady: Boolean = false,

    // land details
    val landTitle: String = "",
    val landType: String = "",
    val areaText: String = "",
    val city: String = "",
    val stateName: String = "",
    val pincode: String = "",
    val expectedPrice: Double? = null,
    val discountPercent: Int? = null,

    // terms
    val termsAccepted: Boolean = false,

    // buyer info
    val buyerName: String = "",
    val buyerMobile: String = "",

    // documents
    val aadhaarUri: Uri? = null,
    val panUri: Uri? = null,
    val bankUri: Uri? = null,

    // payment
    val paymentMethod: String = "UPI",
    val transactionId: String = ""
)
