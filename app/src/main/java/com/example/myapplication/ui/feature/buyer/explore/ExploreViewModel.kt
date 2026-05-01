package com.example.myapplication.ui.feature.buyer.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.ConnectivityObserver
import com.example.myapplication.ui.feature.buyer.domain.repository.LandRepository
import com.example.myapplication.ui.feature.buyer.domain.repository.WishlistRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val landRepository: LandRepository,
    private val wishlistRepository: WishlistRepository,
    private val connectivityObserver: ConnectivityObserver,
    private val showWishlistOnly: Boolean = false
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 20
    }

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState

    private var searchJob: Job? = null

    init {
        observeConnectivity()
    }

    fun onEvent(event: ExploreEvent) {
        when (event) {
            is ExploreEvent.OnSearchChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
                debounceSearch()
            }

            is ExploreEvent.OnSortChanged -> {
                _uiState.update { it.copy(selectedSort = event.sortType) }
                fetchExploreLands(reset = true)
            }

            is ExploreEvent.OnFilterApplied -> {
                _uiState.update { it.copy(currentFilter = event.filter, isFilterOpen = false) }
                fetchExploreLands(reset = true)
            }

            is ExploreEvent.OnWishlistClick -> toggleWishlist(event.landId)

            ExploreEvent.OnFilterDismiss -> {
                _uiState.update { it.copy(isFilterOpen = false) }
            }

            ExploreEvent.OnRefresh -> {
                _uiState.update { it.copy(isRefreshing = true) }
                fetchExploreLands(reset = true)
            }

            ExploreEvent.OnRetry -> {
                fetchExploreLands(reset = true)
            }

            ExploreEvent.OnLoadMore -> {
                fetchExploreLands(reset = false)
            }
        }
    }

    private fun debounceSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400)
            fetchExploreLands(reset = true)
        }
    }

    private fun fetchExploreLands(reset: Boolean) {
        if (!_uiState.value.isConnected) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    isLoadingMore = false,
                    errorMessage = null,
                    lands = emptyList(),
                    hasMore = true,
                    nextSkip = 0
                )
            }
            return
        }

        val state = _uiState.value
        if (!reset && (state.isLoading || state.isLoadingMore || !state.hasMore)) return

        viewModelScope.launch {
            val current = _uiState.value
            val filter = current.currentFilter
            val skip = if (reset) 0 else current.nextSkip

            _uiState.update {
                if (reset) {
                    it.copy(
                        isLoading = true,
                        errorMessage = null,
                        hasMore = true,
                        nextSkip = 0
                    )
                } else {
                    it.copy(
                        isLoadingMore = true,
                        errorMessage = null
                    )
                }
            }

            try {
                val page = landRepository.searchLands(
                    search = current.searchQuery.takeIf { it.isNotBlank() },
                    minPrice = filter?.minPrice,
                    maxPrice = filter?.maxPrice,
                    minArea = filter?.minArea,
                    maxArea = filter?.maxArea,
                    sort = current.selectedSort.toApiSort(),
                    skip = skip,
                    limit = PAGE_SIZE
                )

                val visiblePage = if (showWishlistOnly) page.filter { it.isLiked } else page

                _uiState.update { prev ->
                    val merged = if (reset) visiblePage else (prev.lands + visiblePage)
                    prev.copy(
                        lands = merged,
                        isLoading = false,
                        isRefreshing = false,
                        isLoadingMore = false,
                        hasMore = page.size == PAGE_SIZE,
                        nextSkip = skip + page.size
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        isLoadingMore = false,
                        errorMessage = e.message ?: "Failed to load lands"
                    )
                }
            }
        }
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityObserver.isConnected.collectLatest { isConnected ->
                _uiState.update { it.copy(isConnected = isConnected) }

                if (!isConnected) {
                    _uiState.update {
                        it.copy(
                            lands = emptyList(),
                            isLoading = false,
                            isRefreshing = false,
                            isLoadingMore = false,
                            errorMessage = null,
                            hasMore = true,
                            nextSkip = 0
                        )
                    }
                } else if (_uiState.value.lands.isEmpty() && !_uiState.value.isLoading) {
                    fetchExploreLands(reset = true)
                }
            }
        }
    }

    private fun toggleWishlist(landId: Int) {
        viewModelScope.launch {
            val target = _uiState.value.lands.firstOrNull { it.id == landId } ?: return@launch
            val nextLiked = !target.isLiked

            _uiState.update { state ->
                val updated = state.lands.map { land ->
                    if (land.id == landId) land.copy(isLiked = nextLiked) else land
                }
                state.copy(
                    lands = if (showWishlistOnly) updated.filter { it.isLiked } else updated
                )
            }

            try {
                if (nextLiked) wishlistRepository.likeLand(landId)
                else wishlistRepository.unlikeLand(landId)
            } catch (_: Exception) {
                _uiState.update { state ->
                    val reverted = state.lands.map { land ->
                        if (land.id == landId) land.copy(isLiked = !nextLiked) else land
                    }
                    state.copy(
                        lands = if (showWishlistOnly) reverted.filter { it.isLiked } else reverted
                    )
                }
            }
        }
    }

    private fun SortType.toApiSort(): String = when (this) {
        SortType.LATEST -> "latest"
        SortType.PRICE_LOW_TO_HIGH -> "price_low_to_high"
        SortType.PRICE_HIGH_TO_LOW -> "price_high_to_low"
    }
}
