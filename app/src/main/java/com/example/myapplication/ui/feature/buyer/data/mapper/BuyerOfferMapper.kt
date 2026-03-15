package com.example.myapplication.ui.feature.buyer.data.mapper

import com.example.myapplication.ui.feature.buyer.data.remote.dto.BuyerOfferActionResponseDto
import com.example.myapplication.ui.feature.buyer.data.remote.dto.BuyerOfferItemDto
import com.example.myapplication.ui.feature.buyer.data.remote.dto.BuyerOfferListResponseDto
import com.example.myapplication.ui.feature.buyer.domain.model.BuyerOffer
import com.example.myapplication.ui.feature.buyer.domain.model.BuyerOfferActionResult
import com.example.myapplication.ui.feature.buyer.domain.model.BuyerOfferLand
import com.example.myapplication.ui.feature.buyer.domain.model.BuyerOfferTimelineEvent

fun BuyerOfferListResponseDto.toDomain(): List<BuyerOffer> {
    return items.map { it.toDomain() }
}

fun BuyerOfferItemDto.toDomain(): BuyerOffer {
    val normalizedBookingStatus = bookingStatus.trim().lowercase()
    val normalizedOfferStatus = offerStatus.trim().lowercase()

    return BuyerOffer(
        offerId = offerId,
        requestId = requestId,
        offeredAmount = offeredAmount,
        counterAmount = counterAmount,
        finalAmount = finalAmount,
        offerStatus = normalizedOfferStatus,
        bookingStatus = normalizedBookingStatus,
        statusLabel = statusLabel,
        isBookWindowActive = isBookWindowActive,
        offerLockExpiresAt = offerLockExpiresAt,
        createdAt = createdAt,
        updatedAt = updatedAt,
        sellerResponseAt = sellerResponseAt,
        canAcceptCounter = canAcceptCounter,
        canRejectCounter = canRejectCounter,
        timeline = timeline.map {
            BuyerOfferTimelineEvent(
                key = it.key,
                label = it.label,
                at = it.at
            )
        },
        land = BuyerOfferLand(
            id = land.id,
            title = land.title,
            landType = land.landType,
            location = land.location,
            city = land.city,
            state = land.state,
            expectedPrice = land.expectedPrice
        )
    )
}

fun BuyerOfferActionResponseDto.toDomain(): BuyerOfferActionResult {
    return BuyerOfferActionResult(
        status = status,
        requestId = requestId,
        offerId = offerId,
        offerStatus = offerStatus.trim().lowercase(),
        bookingStatus = bookingStatus.trim().lowercase(),
        offerLockExpiresAt = offerLockExpiresAt
    )
}
