package com.example.myapplication.ui.feature.buyer.data.remote.dto



import com.google.gson.annotations.SerializedName

data class BuyerOfferListResponseDto(
    @SerializedName("total")
    val total: Int,
    @SerializedName("items")
    val items: List<BuyerOfferItemDto>
)

data class BuyerOfferItemDto(
    @SerializedName("offer_id")
    val offerId: Int,
    @SerializedName("request_id")
    val requestId: Int,
    @SerializedName("offered_amount")
    val offeredAmount: Double,
    @SerializedName("counter_amount")
    val counterAmount: Double?,
    @SerializedName("final_amount")
    val finalAmount: Double,
    @SerializedName("offer_status")
    val offerStatus: String,
    @SerializedName("booking_status")
    val bookingStatus: String,
    @SerializedName("status_label")
    val statusLabel: String,
    @SerializedName("is_book_window_active")
    val isBookWindowActive: Boolean,
    @SerializedName("offer_lock_expires_at")
    val offerLockExpiresAt: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("seller_response_at")
    val sellerResponseAt: String?,
    @SerializedName("can_accept_counter")
    val canAcceptCounter: Boolean,
    @SerializedName("can_reject_counter")
    val canRejectCounter: Boolean,
    @SerializedName("timeline")
    val timeline: List<BuyerOfferTimelineEventDto>,
    @SerializedName("land")
    val land: BuyerOfferLandDto
)

data class BuyerOfferTimelineEventDto(
    @SerializedName("key")
    val key: String,
    @SerializedName("label")
    val label: String,
    @SerializedName("at")
    val at: String
)

data class BuyerOfferLandDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String?,
    @SerializedName("land_type")
    val landType: String,
    @SerializedName("location")
    val location: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("state")
    val state: String?,
    @SerializedName("expected_price")
    val expectedPrice: Double?
)

data class BuyerOfferActionResponseDto(
    @SerializedName("status")
    val status: String,
    @SerializedName("request_id")
    val requestId: Int,
    @SerializedName("offer_id")
    val offerId: Int,
    @SerializedName("offer_status")
    val offerStatus: String,
    @SerializedName("booking_status")
    val bookingStatus: String,
    @SerializedName("offer_lock_expires_at")
    val offerLockExpiresAt: String?
)
