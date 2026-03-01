package com.example.myapplication.ui.feature.buyer.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ExploreLandsResponseDto(
    val total: Int,
    val skip: Int,
    val limit: Int,
    val items: List<ExploreLandItemDto>
)

data class ExploreLandItemDto(
    val id: Int,
    val title: String?,
    @SerializedName("land_type") val landType: String,
    @SerializedName("listing_status") val listingStatus: String,
    @SerializedName("expected_price") val expectedPrice: Double?,
    val area: Double?,
    @SerializedName("area_unit") val areaUnit: String,
    val location: String?,
    val city: String?,
    val state: String?,
    val pincode: String?,
    @SerializedName("boundary_points") val boundaryPoints: List<BoundaryPointDto> = emptyList(),
    val images: List<String>,
    @SerializedName("is_liked") val isLiked: Boolean,
    @SerializedName("is_offer_locked") val isOfferLocked: Boolean = false,
    @SerializedName("offer_lock_expires_at") val offerLockExpiresAt: String? = null,
    @SerializedName("created_at") val createdAt: String
)

data class BoundaryPointDto(
    val lat: Double,
    val lng: Double
)
