package com.example.myapplication.ui.feature.buyer.data.remote.dto



import com.google.gson.annotations.SerializedName

data class ExploreLikeResponseDto(
    val status: String,
    @SerializedName("request_id") val requestId: Int,
    val liked: Boolean
)
