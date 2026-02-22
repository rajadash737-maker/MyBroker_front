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
            else -> fromLegacyStatus(fallbackStatus, isDraft)
        }
    }

    private fun fromLegacyStatus(status: String?, isDraft: Boolean): ListingStatus {
        val s = status?.trim()?.lowercase()
        if (isDraft || s == "draft") return ListingStatus.DRAFT
        return when (s) {
            "pending" -> ListingStatus.UNDER_REVIEW
            "approved" -> ListingStatus.APPROVED
            "field_verified" -> ListingStatus.FIELD_VERIFIED
            "live" -> ListingStatus.LIVE
            "rejected" -> ListingStatus.NEEDS_CORRECTION
            "sold" -> ListingStatus.SOLD
            else -> ListingStatus.DRAFT
        }
    }
}
