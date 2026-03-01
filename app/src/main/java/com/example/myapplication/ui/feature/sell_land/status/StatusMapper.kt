package com.example.myapplication.ui.feature.sell_land.status

object StatusMapper {

    fun fromBackend(stage: String?, fallbackStatus: String?, isDraft: Boolean): ListingStatus {
        val s = stage?.trim()?.lowercase()
        return when (s) {
            "draft" -> ListingStatus.DRAFT
            "under_review" -> ListingStatus.UNDER_REVIEW
            "approved" -> ListingStatus.APPROVED
            "field_verified" -> ListingStatus.FIELD_VERIFIED
            "live" -> ListingStatus.LIVE
            "needs_correction" -> ListingStatus.NEEDS_CORRECTION
            "sold" -> ListingStatus.SOLD
            "processing_refund" -> ListingStatus.PROCESSING_REFUND
            "refund_successful" -> ListingStatus.REFUND_SUCCESSFUL
            else -> fromLegacyStatus(fallbackStatus, isDraft)
        }
    }

    private fun fromLegacyStatus(status: String?, isDraft: Boolean): ListingStatus {
        val s = status?.trim()?.lowercase()

        return when (s) {
            "processing_refund" -> ListingStatus.PROCESSING_REFUND
            "refund_successful" -> ListingStatus.REFUND_SUCCESSFUL
            "sold" -> ListingStatus.SOLD
            "live" -> ListingStatus.LIVE
            "field_verified" -> ListingStatus.FIELD_VERIFIED
            "approved" -> ListingStatus.APPROVED
            "pending" -> ListingStatus.UNDER_REVIEW
            "rejected" -> ListingStatus.NEEDS_CORRECTION
            "draft" -> ListingStatus.DRAFT
            else -> if (isDraft) ListingStatus.DRAFT else ListingStatus.UNDER_REVIEW
        }
    }
}
