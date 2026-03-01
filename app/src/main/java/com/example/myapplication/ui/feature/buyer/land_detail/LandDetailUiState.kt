package com.example.myapplication.ui.feature.buyer.land_detail

data class LandDetailUiState(
    val land: LandDetail? = null,
    val isLoading: Boolean = false,
    val isSubmittingOffer: Boolean = false,
    val offerInput: String = "",
    val errorMessage: String? = null,
    val offerSuccessMessage: String? = null
)
