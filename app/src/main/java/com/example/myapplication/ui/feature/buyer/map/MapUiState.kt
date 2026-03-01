package com.example.myapplication.ui.feature.buyer.map

enum class LandStatus {
    AVAILABLE,
    TEMP_LOCK,
    BOOKED,
    SOLD
}

data class MapLand(
    val id: Int,
    val price: Int,
    val area: Int,
    val status: LandStatus,
    val polygonPoints: List<Pair<Double, Double>>,
    val offerLockExpiresAt: String? = null
)
