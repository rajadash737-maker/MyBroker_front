package com.example.myapplication.ui.feature.buyer.land_detail

data class LandDetail(
    val id: Int,
    val sellerUserId: Int? = null,
    val title: String,
    val ownerCategory: String? = null,
    val landType: String,
    val listingStatus: String,
    val statusLabel: String,
    val expectedPrice: Double,
    val area: Double,
    val areaUnit: String,
    val location: String,
    val city: String?,
    val state: String?,
    val pincode: String?,
    val images: List<String>,
    val boundaryPoints: List<Pair<Double, Double>> = emptyList(),
    val isOfferLocked: Boolean = false,
    val offerLockExpiresAt: String? = null,
    val mapQuery: String?,
    val mapUrl: String?,
    val documents: List<com.example.myapplication.ui.feature.sell_land.model.SellLandMediaResponse> = emptyList(),
    val dealAmount: Double? = null,
    val bookingStatus: String? = null,
    val registryDate: String? = null,
    val registryTime: String? = null,
    val registryLocation: String? = null,
    val registryProofs: List<RegistryProof> = emptyList()
)

data class RegistryProof(
    val id: Int,
    val fileUrl: String,
    val fileName: String,
    val createdAt: String
)
