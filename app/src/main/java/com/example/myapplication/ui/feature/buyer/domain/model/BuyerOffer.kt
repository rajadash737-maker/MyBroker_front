package com.example.myapplication.ui.feature.buyer.domain.model



data class BuyerOffer(
    val offerId: Int,
    val requestId: Int,
    val offeredAmount: Double,
    val counterAmount: Double?,
    val finalAmount: Double,
    val offerStatus: String,
    val bookingStatus: String,
    val statusLabel: String,
    val isBookWindowActive: Boolean,
    val offerLockExpiresAt: String?,
    val createdAt: String,
    val updatedAt: String,
    val sellerResponseAt: String?,
    val canAcceptCounter: Boolean,
    val canRejectCounter: Boolean,
    val timeline: List<BuyerOfferTimelineEvent>,
    val land: BuyerOfferLand
)

data class BuyerOfferTimelineEvent(
    val key: String,
    val label: String,
    val at: String
)

data class BuyerOfferLand(
    val id: Int,
    val title: String?,
    val landType: String,
    val location: String?,
    val city: String?,
    val state: String?,
    val expectedPrice: Double?
)

data class BuyerOfferActionResult(
    val status: String,
    val requestId: Int,
    val offerId: Int,
    val offerStatus: String,
    val bookingStatus: String,
    val offerLockExpiresAt: String?
)
