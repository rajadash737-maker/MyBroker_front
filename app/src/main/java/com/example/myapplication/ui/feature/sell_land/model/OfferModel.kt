package com.example.myapplication.ui.feature.sell_land.model

import com.example.myapplication.ui.feature.sell_land.status.OfferStatus

data class OfferModel(
    val id: String,
    val buyerName: String,
    val amount: Double,
    val status: OfferStatus = OfferStatus.PENDING
)
