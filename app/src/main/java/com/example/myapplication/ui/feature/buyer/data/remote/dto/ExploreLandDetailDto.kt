package com.example.myapplication.ui.feature.buyer.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.example.myapplication.ui.feature.sell_land.model.SellLandMediaResponse

data class ExploreLandDetailDto(
    val id: Int,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("seller_id") val sellerId: Int? = null,
    @SerializedName("owner_id") val ownerId: Int? = null,
    val title: String?,
    @SerializedName("owner_category") val ownerCategory: String? = null,
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
    @SerializedName("map_url") val mapUrl: String?,
    @SerializedName("documents") val documents: List<SellLandMediaResponse> = emptyList(),
    @SerializedName("deal_amount") val dealAmount: Double? = null,
    @SerializedName("booking_status") val bookingStatus: String? = null,
    @SerializedName("registry_date") val registryDate: String? = null,
    @SerializedName("registry_time") val registryTime: String? = null,
    @SerializedName("registry_location") val registryLocation: String? = null,
    @SerializedName("registry_proofs") val registryProofs: List<RegistryProofDto> = emptyList()
)

data class RegistryProofDto(
    @SerializedName("id") val id: Int,
    @SerializedName("file_url") val fileUrl: String,
    @SerializedName("file_name") val fileName: String,
    @SerializedName("created_at") val createdAt: String
)
