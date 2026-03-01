package com.example.myapplication.ui.feature.buyer.data.remote

import com.example.myapplication.ui.feature.buyer.data.remote.dto.BuyerOfferActionResponseDto
import com.example.myapplication.ui.feature.buyer.data.remote.dto.BuyerOfferListResponseDto
import com.example.myapplication.ui.feature.buyer.data.remote.dto.ExploreLandDetailDto
import com.example.myapplication.ui.feature.buyer.data.remote.dto.ExploreLikeResponseDto
import com.example.myapplication.ui.feature.buyer.data.remote.dto.ExploreLandsResponseDto
import com.example.myapplication.ui.feature.buyer.data.remote.dto.MakeOfferRequestDto
import com.example.myapplication.ui.feature.buyer.data.remote.dto.MakeOfferResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LandApi {

    @GET("sell-land/explore")
    suspend fun exploreLands(
        @Query("search") search: String? = null,
        @Query("min_price") minPrice: Int? = null,
        @Query("max_price") maxPrice: Int? = null,
        @Query("min_area") minArea: Int? = null,
        @Query("max_area") maxArea: Int? = null,
        @Query("sort") sort: String = "latest",
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 20
    ): ExploreLandsResponseDto

    @GET("sell-land/explore")
    suspend fun nearbyLands(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 10,
        @Query("sort") sort: String = "latest"
    ): ExploreLandsResponseDto

    @GET("sell-land/explore/{request_id}")
    suspend fun getLandDetail(
        @Path("request_id") requestId: Int
    ): ExploreLandDetailDto

    @POST("sell-land/explore/{request_id}/offer")
    suspend fun makeOffer(
        @Path("request_id") requestId: Int,
        @Body body: MakeOfferRequestDto
    ): MakeOfferResponseDto

    @POST("sell-land/explore/{request_id}/like")
    suspend fun likeLand(
        @Path("request_id") requestId: Int
    ): ExploreLikeResponseDto

    @DELETE("sell-land/explore/{request_id}/like")
    suspend fun unlikeLand(
        @Path("request_id") requestId: Int
    ): ExploreLikeResponseDto

    @GET("sell-land/buyer/offers")
    suspend fun getBuyerOffers(): BuyerOfferListResponseDto

    @POST("sell-land/buyer/offers/{offer_id}/accept-counter")
    suspend fun acceptCounterOffer(
        @Path("offer_id") offerId: Int
    ): BuyerOfferActionResponseDto

    @POST("sell-land/buyer/offers/{offer_id}/reject-counter")
    suspend fun rejectCounterOffer(
        @Path("offer_id") offerId: Int
    ): BuyerOfferActionResponseDto
}
