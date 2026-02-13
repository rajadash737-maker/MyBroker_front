package com.example.myapplication.ui.feature.sell_land.model

import com.google.android.gms.maps.model.LatLng

data class SellLandDraft(
    val sellerDetails: SellerDetails? = null,
    val landDetails: LandDetails? = null,
    val images: List<String> = emptyList(),
    val documents: LandDocuments = LandDocuments(),
    val polygon: List<LatLng> = emptyList(),
    val pricing: PricingDetails? = null,
    val lastStep: SellLandStep = SellLandStep.SELLER
)


data class LandDocuments(
    val saleDeed: String? = null,
    val khataCertificate: String? = null,
    val encumbrance: String? = null,
    val sellerIdentity: String? = null
)
