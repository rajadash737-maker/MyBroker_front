package com.example.myapplication.ui.feature.sell_land.model

import com.example.myapplication.ui.feature.sell_land.status.OfferStatus

data class OfferModel(
    val id: String,
    val buyerId: Int,
    val buyerName: String,
    val buyerMobile: String? = null,
    val amount: Double,
    val counterAmount: Double? = null,
    val status: OfferStatus = OfferStatus.PENDING
)
