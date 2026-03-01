package com.example.myapplication.ui.feature.buyer.data.remote.dto



import com.google.gson.annotations.SerializedName

data class MakeOfferResponseDto(
    val status: String,
    @SerializedName("offer_id") val offerId: Int,
    @SerializedName("request_id") val requestId: Int,
    @SerializedName("offered_amount") val offeredAmount: Double
)
