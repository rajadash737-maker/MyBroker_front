package com.example.myapplication.ui.feature.sell_land.model

import com.google.gson.annotations.SerializedName

data class SellLandDraftRequest(
    @SerializedName("title") val title: String? = null,
    @SerializedName("owner_name") val ownerName: String? = null,
    @SerializedName("owner_mobile") val ownerMobile: String? = null,
    @SerializedName("owner_category") val ownerCategory: String? = null,
    @SerializedName("land_type") val landType: String? = null,
    @SerializedName("ownership_type") val ownershipType: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("city") val city: String? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("pincode") val pincode: String? = null,
    @SerializedName("area") val area: Double? = null,
    @SerializedName("area_unit") val areaUnit: String? = null,
    @SerializedName("price_type") val priceType: String? = null,
    @SerializedName("expected_price") val expectedPrice: Double? = null,
    @SerializedName("negotiable") val negotiable: Boolean? = null,
    @SerializedName("availability") val availability: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("draft_step") val draftStep: Int? = null
)

data class BoundaryPoint(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double
)

data class BoundaryRequest(
    @SerializedName("points") val points: List<BoundaryPoint>
)

data class PaymentInitRequest(
    @SerializedName("request_id") val requestId: Int,
    @SerializedName("method") val method: String,
    @SerializedName("provider_ref") val providerRef: String? = null
)

data class PaymentMarkPaidRequest(
    @SerializedName("payment_id") val paymentId: Int,
    @SerializedName("provider_ref") val providerRef: String? = null
)

data class SellLandRazorpayOrderRequest(
    @SerializedName("request_id") val requestId: Int
)

data class SellLandRazorpayVerifyRequest(
    @SerializedName("razorpay_order_id") val orderId: String,
    @SerializedName("razorpay_payment_id") val paymentId: String,
    @SerializedName("razorpay_signature") val signature: String
)

data class CancelListingRequest(
    @SerializedName("reason") val reason: String,
    @SerializedName("upi_id") val upiId: String,
    @SerializedName("notes") val notes: String? = null
)

data class CounterOfferRequest(
    @SerializedName("counter_amount") val counterAmount: Double
)

object OwnershipTypeApi {
    const val SINGLE_OWNER = "single_owner"
    const val MULTI_OWNER = "multi_owner"
    const val INHERITED_LAND = "inherited_land"
}

