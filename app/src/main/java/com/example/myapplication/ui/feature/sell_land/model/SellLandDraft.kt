package com.example.myapplication.ui.feature.sell_land.model

import com.example.myapplication.ui.feature.sell_land.status.ListingStatus
import com.google.android.gms.maps.model.LatLng

data class SellLandDraft(
    val sellerDetails: SellerDetails? = null,
    val landDetails: LandDetails? = null,
    val images: List<String> = emptyList(),
    val documents: LandDocuments = LandDocuments(),
    val polygon: List<LatLng> = emptyList(),
    val pricing: PricingDetails? = null,
    val lastStep: SellLandStep = SellLandStep.SELLER,
    val status: ListingStatus = ListingStatus.DRAFT
)

data class LandDocuments(
    // Legacy fields
    val saleDeed: String? = null,
    val khataCertificate: String? = null,
    val encumbrance: String? = null,
    val sellerIdentity: String? = null,

    // New mapping: key=document_type, value=file_url
    val dynamicDocs: Map<String, String> = emptyMap()
) {
    private fun normalizedDynamic(): Map<String, String> {
        return dynamicDocs
            .mapKeys { it.key.trim().lowercase() }
            .filterValues { it.isNotBlank() }
    }

    fun asMap(): Map<String, String> {
        val base = normalizedDynamic().toMutableMap()

        if (!saleDeed.isNullOrBlank()) {
            base.putIfAbsent("sale_deed", saleDeed)
            base.putIfAbsent("original_sale_deed", saleDeed)
        }
        if (!khataCertificate.isNullOrBlank()) {
            base.putIfAbsent("khata_patta", khataCertificate)
        }
        if (!encumbrance.isNullOrBlank()) {
            base.putIfAbsent("encumbrance_certificate", encumbrance)
        }
        if (!sellerIdentity.isNullOrBlank()) {
            base.putIfAbsent("aadhaar_pan", sellerIdentity)
            base.putIfAbsent("seller_identity_proof", sellerIdentity)
        }

        return base
    }

    fun get(docType: String): String? = asMap()[docType.trim().lowercase()]

    fun isUploaded(docType: String): Boolean = !get(docType).isNullOrBlank()

    fun withDoc(docType: String, fileUrl: String): LandDocuments {
        val key = docType.trim().lowercase()
        if (key.isBlank() || fileUrl.isBlank()) return this
        return copy(dynamicDocs = normalizedDynamic() + (key to fileUrl))
    }

    fun withoutDoc(docType: String): LandDocuments {
        val key = docType.trim().lowercase()
        return copy(dynamicDocs = normalizedDynamic() - key)
    }
}

data class SellLandModel(
    val id: String,
    val title: String,
    val price: Double,
    val status: ListingStatus = ListingStatus.DRAFT,
    val statusBeforeCancellation: ListingStatus? = null,
    val rejectionReason: String? = null,
    val offerCount: Int = 0,
    val likesCount: Int = 0,
    val images: List<String> = emptyList(),
    val profileImageUrl: String? = null
) {
    val coverImageUrl: String?
        get() = images.firstOrNull()
}
