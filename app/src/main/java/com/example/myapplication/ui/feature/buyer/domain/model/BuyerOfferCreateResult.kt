package com.example.myapplication.ui.feature.buyer.domain.model



data class BuyerOfferCreateResult(
    val status: String,
    val requestId: Int,
    val offerId: Int,
    val offerStatus: String
)
