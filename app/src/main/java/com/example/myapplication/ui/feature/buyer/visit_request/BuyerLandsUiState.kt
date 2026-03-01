package com.example.myapplication.ui.feature.buyer.visit_request



import com.example.myapplication.ui.feature.buyer.domain.model.BuyerOffer

enum class BuyerOfferFilter {
    ALL,
    OFFER_SENT,
    COUNTER_RECEIVED,
    ACCEPTED_BOOK_NOW,
    REJECTED,
    EXPIRED
}

data class BuyerLandsUiState(
    val isLoading: Boolean = false,
    val offers: List<BuyerOffer> = emptyList(),
    val selectedFilter: BuyerOfferFilter = BuyerOfferFilter.ALL,
    val errorMessage: String? = null,
    val actionMessage: String? = null
) {
    val filteredOffers: List<BuyerOffer>
        get() = when (selectedFilter) {
            BuyerOfferFilter.ALL -> offers
            BuyerOfferFilter.OFFER_SENT -> offers.filter { it.bookingStatus.equals("offer_sent", true) }
            BuyerOfferFilter.COUNTER_RECEIVED -> offers.filter { it.bookingStatus.equals("counter_received", true) }
            BuyerOfferFilter.ACCEPTED_BOOK_NOW -> offers.filter { it.bookingStatus.equals("accepted_book_now", true) }
            BuyerOfferFilter.REJECTED -> offers.filter { it.bookingStatus.equals("rejected", true) }
            BuyerOfferFilter.EXPIRED -> offers.filter { it.bookingStatus.equals("expired", true) }
        }
}
