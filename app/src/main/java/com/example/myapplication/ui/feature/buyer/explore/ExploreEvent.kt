package com.example.myapplication.ui.feature.buyer.explore

sealed class ExploreEvent {

    data class OnSearchChanged(val query: String) : ExploreEvent()

    data class OnSortChanged(val sortType: SortType) : ExploreEvent()

    data class OnFilterApplied(val filter: LandFilter) : ExploreEvent()

    object OnFilterDismiss : ExploreEvent()

    data class OnWishlistClick(val landId: Int) : ExploreEvent()

    object OnRefresh : ExploreEvent()

    object OnRetry : ExploreEvent()

    object OnLoadMore : ExploreEvent() // NEW
}
