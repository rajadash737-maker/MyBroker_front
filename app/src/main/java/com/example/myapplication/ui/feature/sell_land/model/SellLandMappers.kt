package com.example.myapplication.ui.feature.sell_land.model

import com.example.myapplication.ui.feature.sell_land.status.StatusMapper

fun SellLandResponse.toSellLandModel(): SellLandModel {
    val mappedStatus = StatusMapper.fromBackend(
        stage = tracking?.stage,
        fallbackStatus = status,
        isDraft = isDraft
    )

    val mappedStatusBeforeCancellation = statusBeforeCancellation
        ?.let { previous ->
            StatusMapper.fromBackend(
                stage = previous,
                fallbackStatus = previous,
                isDraft = false
            )
        }

    return SellLandModel(
        id = id.toString(),
        title = title ?: "Untitled Land",
        price = expectedPrice ?: 0.0,
        status = mappedStatus,
        statusBeforeCancellation = mappedStatusBeforeCancellation,
        offerCount = offerCount,
        likesCount = likesCount,
        rejectionReason = tracking?.correctionReason ?: rejectionReason ?: fieldOfficerRejectionReason
    )
}
