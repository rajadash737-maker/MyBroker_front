package com.example.myapplication.ui.feature.sell_land.model



data class PricingDetails(
    val priceType: String,
    val price: Double,
    val negotiable: Boolean,
    val availability: String
)
