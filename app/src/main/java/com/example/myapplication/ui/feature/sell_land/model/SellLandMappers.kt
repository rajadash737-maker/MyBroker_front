package com.example.myapplication.ui.feature.sell_land.model

import com.example.myapplication.network.ApiClient
import com.example.myapplication.ui.feature.sell_land.status.StatusMapper

fun SellLandResponse.toSellLandModel(): SellLandModel {
    val mappedStatus = StatusMapper.fromBackend(
        stage = tracking?.stage,
        fallbackStatus = status,
        isDraft = isDraft
    )

    val mappedStatusBeforeCancellation = statusBeforeCancellation?.let { previous ->
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
        canCancelListing = canCancelListing,
        paymentStatus = paymentStatus,
        statusBeforeCancellation = mappedStatusBeforeCancellation,
        offerCount = offerCount,
        likesCount = likesCount,
        rejectionReason = tracking?.correctionReason ?: rejectionReason ?: fieldOfficerRejectionReason,
        images = images.orEmpty().filter { it.isNotBlank() },
        profileImageUrl = profileImageUrl,
        dealAmount = dealAmount,
        bookingStatus = bookingStatus?.trim()?.lowercase(),
        registryDate = registryDate,
        registryTime = registryTime,
        registryLocation = registryLocation,
        registryProofs = registryProofs.map { proof ->
            val normalized = ApiClient.absoluteUrl(proof.fileUrl) ?: proof.fileUrl
            RegistryProof(
                id = proof.id,
                fileUrl = normalized,
                fileName = proof.fileName,
                createdAt = proof.createdAt
            )
        }
    )
}
