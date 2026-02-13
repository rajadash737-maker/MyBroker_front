package com.example.myapplication.ui.feature.sell_land.utils



import com.example.myapplication.ui.feature.sell_land.model.*

/**
 * DraftMapper
 * ------------------------------------
 * UI Draft <-> Persistence / Backend mapping
 * Industry standard utility
 */
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

}
