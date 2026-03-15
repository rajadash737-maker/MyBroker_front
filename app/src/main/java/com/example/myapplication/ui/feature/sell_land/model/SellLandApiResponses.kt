package com.example.myapplication.ui.feature.sell_land.model

import com.google.gson.annotations.SerializedName

data class SellLandResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("listing_code") val listingCode: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("owner_name") val ownerName: String? = null,
    @SerializedName("owner_mobile") val ownerMobile: String? = null,
    @SerializedName("land_type") val landType: String,
    @SerializedName("ownership_type") val ownershipType: String,
    @SerializedName("location") val location: String? = null,
    @SerializedName("city") val city: String? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("pincode") val pincode: String? = null,
    @SerializedName("area") val area: Double? = null,
    @SerializedName("area_unit") val areaUnit: String,
    @SerializedName("price_type") val priceType: String,
    @SerializedName("expected_price") val expectedPrice: Double? = null,
    @SerializedName("negotiable") val negotiable: Boolean,
    @SerializedName("availability") val availability: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("boundary_points") val boundaryPoints: String? = null,
    @SerializedName("is_draft") val isDraft: Boolean,
    @SerializedName("draft_step") val draftStep: Int,
    @SerializedName("payment_required_amount") val paymentRequiredAmount: Double,
    @SerializedName("payment_status") val paymentStatus: String,
    @SerializedName("status") val status: String,
    @SerializedName("status_before_cancellation") val statusBeforeCancellation: String? = null,
    @SerializedName("rejection_reason") val rejectionReason: String? = null,
    @SerializedName("field_officer_rejection_reason") val fieldOfficerRejectionReason: String? = null,
    @SerializedName("cancellation_reason") val cancellationReason: String? = null,
    @SerializedName("refund_upi_id") val refundUpiId: String? = null,
    @SerializedName("cancelled_at") val cancelledAt: String? = null,
    @SerializedName("refund_due_at") val refundDueAt: String? = null,
    @SerializedName("refund_completed_at") val refundCompletedAt: String? = null,
    @SerializedName("can_cancel_listing") val canCancelListing: Boolean = false,
    @SerializedName("likes_count") val likesCount: Int = 0,
    @SerializedName("offer_count") val offerCount: Int = 0,
    @SerializedName("images") val images: List<String>? = null,
    @SerializedName("profile_image_url") val profileImageUrl: String? = null,
    @SerializedName("tracking") val tracking: TrackingDto? = null,
    @SerializedName("required_document_types") val requiredDocumentTypes: List<String> = emptyList(),
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class DocumentRequirementsResponse(
    @SerializedName("ownership_type") val ownershipType: String,
    @SerializedName("required_document_types") val requiredDocumentTypes: List<String>,
    @SerializedName("optional_document_types") val optionalDocumentTypes: List<String> = emptyList()
)

data class SellLandMediaResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("request_id") val requestId: Int,
    @SerializedName("media_type") val mediaType: String,
    @SerializedName("document_type") val documentType: String? = null,
    @SerializedName("file_url") val fileUrl: String,
    @SerializedName("file_name") val fileName: String,
    @SerializedName("is_required") val isRequired: Boolean,
    @SerializedName("created_at") val createdAt: String
) {
    val normalizedMediaType: String
        get() = mediaType.trim().lowercase()

    val normalizedDocumentType: String?
        get() = documentType?.trim()?.lowercase()?.takeIf { it.isNotBlank() }
}

data class DeleteMediaResponse(
    @SerializedName("status") val status: String,
    @SerializedName("media_id") val mediaId: Int
)

data class PaymentResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("request_id") val requestId: Int,
    @SerializedName("method") val method: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("status") val status: String,
    @SerializedName("provider_ref") val providerRef: String? = null,
    @SerializedName("paid_at") val paidAt: String? = null,
    @SerializedName("created_at") val createdAt: String
)

data class SubmitResponse(
    @SerializedName("status") val status: String,
    @SerializedName("request_id") val requestId: Int,
    @SerializedName("listing_code") val listingCode: String
)

data class CancelListingResponse(
    @SerializedName("status") val status: String,
    @SerializedName("request_id") val requestId: Int,
    @SerializedName("listing_status") val listingStatus: String,
    @SerializedName("cancellation_reason") val cancellationReason: String,
    @SerializedName("refund_upi_id") val refundUpiId: String,
    @SerializedName("refund_due_at") val refundDueAt: String
)

data class TrackingDto(
    @SerializedName("stage") val stage: String,
    @SerializedName("label") val label: String,
    @SerializedName("needs_correction") val needsCorrection: Boolean,
    @SerializedName("correction_by") val correctionBy: String? = null,
    @SerializedName("correction_reason") val correctionReason: String? = null,
    @SerializedName("refund_due_at") val refundDueAt: String? = null,
    @SerializedName("progress") val progress: TrackingProgressDto
)

data class TrackingProgressDto(
    @SerializedName("draft") val draft: Boolean,
    @SerializedName("under_review") val underReview: Boolean,
    @SerializedName("approved") val approved: Boolean,
    @SerializedName("field_verified") val fieldVerified: Boolean,
    @SerializedName("live") val live: Boolean,
    @SerializedName("sold") val sold: Boolean,
    @SerializedName("processing_refund") val processingRefund: Boolean = false,
    @SerializedName("refund_successful") val refundSuccessful: Boolean = false
)

// -------------------- OFFER RESPONSES --------------------

data class OfferBuyerDto(
    @SerializedName("id") val id: Int,
    @SerializedName("fullname") val fullname: String,
    @SerializedName("mobile") val mobile: String? = null
)

data class SellerOfferItemResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("request_id") val requestId: Int,
    @SerializedName("buyer_id") val buyerId: Int,
    @SerializedName("offered_amount") val offeredAmount: Double,
    @SerializedName("counter_amount") val counterAmount: Double? = null,
    @SerializedName("offer_status") val offerStatus: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("seller_response_at") val sellerResponseAt: String? = null,
    @SerializedName("buyer") val buyer: OfferBuyerDto
)

data class SellerOfferListResponse(
    @SerializedName("request_id") val requestId: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("is_offer_locked") val isOfferLocked: Boolean = false,
    @SerializedName("offer_lock_expires_at") val offerLockExpiresAt: String? = null,
    @SerializedName("items") val items: List<SellerOfferItemResponse>
)

data class SellerOfferActionResponse(
    @SerializedName("status") val status: String,
    @SerializedName("request_id") val requestId: Int,
    @SerializedName("offer_id") val offerId: Int,
    @SerializedName("offer_status") val offerStatus: String,
    @SerializedName("offer_lock_expires_at") val offerLockExpiresAt: String? = null
)
