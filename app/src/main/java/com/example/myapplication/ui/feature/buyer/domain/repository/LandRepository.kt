package com.example.myapplication.ui.feature.buyer.domain.repository

import com.example.myapplication.ui.feature.buyer.domain.model.BuyerOffer
import com.example.myapplication.ui.feature.buyer.domain.model.BuyerOfferActionResult
import com.example.myapplication.ui.feature.buyer.explore.Land
import com.example.myapplication.ui.feature.buyer.land_detail.LandDetail

interface LandRepository {

    suspend fun searchLands(
        search: String? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null,
        minArea: Int? = null,
        maxArea: Int? = null,
        sort: String = "latest",
        skip: Int = 0,
        limit: Int = 20
    ): List<Land>

    suspend fun getNearbyLands(
        skip: Int = 0,
        limit: Int = 10,
        sort: String = "latest"
    ): List<Land>

    suspend fun getLandDetail(requestId: Int): LandDetail

    suspend fun makeOffer(requestId: Int, amount: Double): Boolean

    suspend fun getBuyerOffers(): List<BuyerOffer>

    suspend fun acceptCounterOffer(offerId: Int): BuyerOfferActionResult

    suspend fun rejectCounterOffer(offerId: Int): BuyerOfferActionResult
}
