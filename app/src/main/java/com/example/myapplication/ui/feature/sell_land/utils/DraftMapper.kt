package com.example.myapplication.ui.feature.sell_land.utils

import com.example.myapplication.ui.feature.sell_land.model.*
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray

object DraftMapper {

    fun emptyDraft(): SellLandDraft {
        return SellLandDraft(
            sellerDetails = null,
            landDetails = null,
            images = emptyList(),
            documents = LandDocuments(),
            polygon = emptyList(),
            pricing = null,
            lastStep = SellLandStep.SELLER
        )
    }

    fun isDraftEmpty(draft: SellLandDraft): Boolean {
        return draft.sellerDetails == null &&
                draft.landDetails == null &&
                draft.images.isEmpty() &&
                draft.documents.saleDeed == null &&
                draft.documents.khataCertificate == null &&
                draft.documents.encumbrance == null &&
                draft.documents.sellerIdentity == null &&
                draft.polygon.isEmpty() &&
                draft.pricing == null
    }

    fun canProceedToNextStep(draft: SellLandDraft): Boolean {
        return when (draft.lastStep) {
            SellLandStep.SELLER -> draft.sellerDetails != null
            SellLandStep.LAND -> draft.landDetails != null
            SellLandStep.MEDIA -> draft.images.isNotEmpty()
            SellLandStep.MAP -> draft.polygon.size >= 3
            SellLandStep.PRICING -> draft.pricing != null
            SellLandStep.REVIEW -> true
            SellLandStep.PAYMENT -> true
            SellLandStep.SUCCESS -> true
        }
    }

    fun fromExistingListing(
        res: SellLandResponse,
        media: List<SellLandMediaResponse>
    ): SellLandDraft {
        val photos = media
            .filter { it.mediaType == "photo" }
            .map { it.fileUrl }

        fun firstDocUrl(vararg docTypes: String): String? {
            return media.firstOrNull { it.mediaType == "document" && it.documentType in docTypes }?.fileUrl
        }

        val docs = LandDocuments(
            saleDeed = firstDocUrl("sale_deed", "original_sale_deed"),
            khataCertificate = firstDocUrl("khata_patta", "legal_heir_certificate", "joint_affidavit"),
            encumbrance = firstDocUrl("encumbrance_certificate", "latest_tax_receipt"),
            sellerIdentity = firstDocUrl(
                "seller_identity_proof",
                "aadhaar_pan",
                "all_heirs_aadhaar",
                "all_co_owner_aadhaar",
                "consent_affidavit_all_heirs",
                "death_certificate"
            )
        )

        return SellLandDraft(
            sellerDetails = SellerDetails(
                name = res.ownerName.orEmpty(),
                mobile = res.ownerMobile.orEmpty(),
                sellerType = normalizeOwnershipType(res.ownershipType)
            ),
            landDetails = LandDetails(
                title = res.title.orEmpty(),
                landType = res.landType,
                area = (res.area ?: 0.0).toString(),
                areaUnit = res.areaUnit,
                state = res.state.orEmpty(),
                city = res.city.orEmpty(),
                pincode = res.pincode.orEmpty()
            ),
            images = photos,
            documents = docs,
            polygon = parseBoundaryPoints(res.boundaryPoints),
            pricing = PricingDetails(
                priceType = res.priceType,
                price = res.expectedPrice ?: 0.0,
                negotiable = res.negotiable,
                availability = res.availability
            ),
            lastStep = SellLandStep.REVIEW
        )
    }

    private fun normalizeOwnershipType(value: String?): String {
        return when (value?.trim()?.lowercase()) {
            "single_owner" -> "single_owner"
            "multi_owner" -> "multi_owner"
            "inherited_land" -> "inherited_land"

            // legacy fallback
            "owner" -> "single_owner"
            "broker" -> "multi_owner"
            "power_of_attorney", "power of attorney", "power-of-attorney" -> "inherited_land"
            "other" -> "single_owner"

            else -> "single_owner"
        }
    }

    private fun parseBoundaryPoints(boundaryJson: String?): List<LatLng> {
        if (boundaryJson.isNullOrBlank()) return emptyList()

        return try {
            val arr = JSONArray(boundaryJson)
            buildList {
                for (i in 0 until arr.length()) {
                    val p = arr.optJSONObject(i) ?: continue
                    val lat = p.optDouble("lat", Double.NaN)
                    val lng = p.optDouble("lng", Double.NaN)
                    if (!lat.isNaN() && !lng.isNaN()) add(LatLng(lat, lng))
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}
