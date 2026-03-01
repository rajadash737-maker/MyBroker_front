package com.example.myapplication.ui.feature.buyer.data.repository

import com.example.myapplication.network.ApiClient
import com.example.myapplication.ui.feature.buyer.data.mapper.toDomain
import com.example.myapplication.ui.feature.buyer.data.remote.LandApi
import com.example.myapplication.ui.feature.buyer.data.remote.dto.ExploreLandItemDto
import com.example.myapplication.ui.feature.buyer.data.remote.dto.MakeOfferRequestDto
import com.example.myapplication.ui.feature.buyer.domain.model.BuyerOffer
import com.example.myapplication.ui.feature.buyer.domain.model.BuyerOfferActionResult
import com.example.myapplication.ui.feature.buyer.domain.repository.LandRepository
import com.example.myapplication.ui.feature.buyer.explore.Land
import com.example.myapplication.ui.feature.buyer.land_detail.LandDetail

class LandRepositoryImpl(
    private val landApi: LandApi
) : LandRepository {

    override suspend fun searchLands(
        search: String?,
        minPrice: Int?,
        maxPrice: Int?,
        minArea: Int?,
        maxArea: Int?,
        sort: String,
        skip: Int,
        limit: Int
    ): List<Land> {
        val response = landApi.exploreLands(
            search = search,
            minPrice = minPrice,
            maxPrice = maxPrice,
            minArea = minArea,
            maxArea = maxArea,
            sort = sort,
            skip = skip,
            limit = limit
        )
        return response.items.map(::mapToLand)
    }

    override suspend fun getNearbyLands(
        skip: Int,
        limit: Int,
        sort: String
    ): List<Land> {
        val response = landApi.nearbyLands(
            skip = skip,
            limit = limit,
            sort = sort
        )
        return response.items.map(::mapToLand)
    }

    override suspend fun getLandDetail(requestId: Int): LandDetail {
        val dto = landApi.getLandDetail(requestId)

        return LandDetail(
            id = dto.id,
            title = dto.title.orEmpty(),
            landType = dto.landType,
            listingStatus = dto.listingStatus,
            statusLabel = dto.statusLabel,
            expectedPrice = dto.expectedPrice ?: 0.0,
            area = dto.area ?: 0.0,
            areaUnit = dto.areaUnit,
            location = dto.location.orEmpty(),
            city = dto.city,
            state = dto.state,
            pincode = dto.pincode,
            images = dto.images.map { raw ->
                val normalized = raw.replace("\\", "/")
                ApiClient.absoluteUrl(normalized) ?: normalized
            },
            boundaryPoints = dto.boundaryPoints.map { it.lat to it.lng },
            isOfferLocked = dto.isOfferLocked,
            offerLockExpiresAt = dto.offerLockExpiresAt,
            mapQuery = dto.mapQuery,
            mapUrl = dto.mapUrl
        )

    }

    override suspend fun makeOffer(requestId: Int, amount: Double): Boolean {
        val response = landApi.makeOffer(
            requestId = requestId,
            body = MakeOfferRequestDto(amount = amount)
        )
        return response.status.equals("Offer sent", ignoreCase = true)
    }

    override suspend fun getBuyerOffers(): List<BuyerOffer> {
        val response = landApi.getBuyerOffers()
        return response.toDomain()
    }

    override suspend fun acceptCounterOffer(offerId: Int): BuyerOfferActionResult {
        val response = landApi.acceptCounterOffer(offerId = offerId)
        return response.toDomain()
    }

    override suspend fun rejectCounterOffer(offerId: Int): BuyerOfferActionResult {
        val response = landApi.rejectCounterOffer(offerId = offerId)
        return response.toDomain()
    }

    private fun mapToLand(dto: ExploreLandItemDto): Land {
        return Land(
            id = dto.id,
            title = dto.title.orEmpty(),
            price = dto.expectedPrice ?: 0.0,
            location = dto.location.orEmpty(),
            city = dto.city,
            state = dto.state,
            pincode = dto.pincode,
            area = dto.area ?: 0.0,
            areaUnit = dto.areaUnit,
            images = dto.images.map { raw ->
                val normalized = raw.replace("\\", "/")
                ApiClient.absoluteUrl(normalized) ?: normalized
            },
            boundaryPoints = dto.boundaryPoints.map { it.lat to it.lng },
            isVerified = dto.listingStatus.equals("live", ignoreCase = true),
            isFeatured = false,
            isLiked = dto.isLiked,
            isOfferLocked = dto.isOfferLocked,
            offerLockExpiresAt = dto.offerLockExpiresAt,
            createdAt = dto.createdAt
        )
    }

}
