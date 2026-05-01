package com.example.myapplication.ui.feature.sell_land.status

object StatusTransition {

    fun canMove(current: ListingStatus, target: ListingStatus): Boolean {
        return when (current) {

            ListingStatus.DRAFT ->
                target == ListingStatus.UNDER_REVIEW

            ListingStatus.UNDER_REVIEW ->
                target == ListingStatus.APPROVED || target == ListingStatus.NEEDS_CORRECTION

            ListingStatus.APPROVED ->
                target == ListingStatus.FIELD_VERIFIED || target == ListingStatus.NEEDS_CORRECTION

            ListingStatus.FIELD_VERIFIED ->
                target == ListingStatus.LIVE || target == ListingStatus.NEEDS_CORRECTION

            ListingStatus.LIVE ->
                target == ListingStatus.BOOKED || target == ListingStatus.SOLD || target == ListingStatus.PROCESSING_REFUND

            ListingStatus.NEEDS_CORRECTION ->
                target == ListingStatus.UNDER_REVIEW || target == ListingStatus.DRAFT

            ListingStatus.PROCESSING_REFUND ->
                target == ListingStatus.REFUND_SUCCESSFUL

            ListingStatus.REFUND_SUCCESSFUL ->
                false

            ListingStatus.BOOKED ->
                false

            ListingStatus.SOLD ->
                false
        }
    }
}
