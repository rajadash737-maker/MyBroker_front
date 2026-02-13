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
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
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
)

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
