package com.example.myapplication.ui.feature.buyer.explore

data class ExploreUiState(
    val lands: List<Land> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false, // NEW
    val hasMore: Boolean = true,        // NEW
    val nextSkip: Int = 0,              // NEW
    val searchQuery: String = "",
    val selectedSort: SortType = SortType.LATEST,
    val isFilterOpen: Boolean = false,
    val currentFilter: LandFilter? = null,
    val errorMessage: String? = null,
    val isConnected: Boolean = true
)
