package com.example.myapplication.ui.feature.buyer.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ExploreLandDetailDto(
    val id: Int,
    val title: String?,
    @SerializedName("land_type") val landType: String,
    @SerializedName("listing_status") val listingStatus: String,
    @SerializedName("status_label") val statusLabel: String,
    @SerializedName("expected_price") val expectedPrice: Double?,
    val area: Double?,
    @SerializedName("area_unit") val areaUnit: String,
    val location: String?,
    val city: String?,
    val state: String?,
    val pincode: String?,
    @SerializedName("boundary_points") val boundaryPoints: List<BoundaryPointDto> = emptyList(),
    val images: List<String>,
    @SerializedName("is_offer_locked") val isOfferLocked: Boolean = false,
    @SerializedName("offer_lock_expires_at") val offerLockExpiresAt: String? = null,
    @SerializedName("map_query") val mapQuery: String?,
    @SerializedName("map_url") val mapUrl: String?
)
