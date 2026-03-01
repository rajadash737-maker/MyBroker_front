package com.example.myapplication.ui.feature.buyer.land_detail

data class LandDetail(
    val id: Int,
    val title: String,
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
    val mapUrl: String?
)
