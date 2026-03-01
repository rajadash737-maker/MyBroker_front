package com.example.myapplication.ui.feature.buyer.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.ui.feature.buyer.domain.repository.LandRepository
import com.example.myapplication.ui.feature.buyer.explore.Land
import kotlinx.coroutines.launch

class MapViewModel(
    private val landRepository: LandRepository
) : ViewModel() {

    private val _lands = mutableStateListOf<MapLand>()
    val lands: List<MapLand> = _lands

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadMapData()
    }

    fun loadMapData() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            runCatching {
                fetchAllLiveLands()
            }.onSuccess { allLands ->
                _lands.clear()
                _lands.addAll(
                    allLands
                        .distinctBy { it.id }
                        .filter { it.boundaryPoints.size >= 3 }
                        .map { land ->
                            MapLand(
                                id = land.id,
                                price = land.price.toInt(),
                                area = land.area.toInt(),
                                status = if (land.isOfferLocked) LandStatus.TEMP_LOCK else LandStatus.AVAILABLE,
                                polygonPoints = closePolygon(land.boundaryPoints),
                                offerLockExpiresAt = land.offerLockExpiresAt
                            )
                        }
                )
                isLoading = false
            }.onFailure { e ->
                isLoading = false
                errorMessage = e.message ?: "Failed to load map lands"
            }
        }
    }

    private suspend fun fetchAllLiveLands(): List<Land> {
        val pageSize = 100
        var skip = 0
        val all = mutableListOf<Land>()

        while (true) {
            val page = landRepository.searchLands(
                search = null,
                minPrice = null,
                maxPrice = null,
                minArea = null,
                maxArea = null,
                sort = "latest",
                skip = skip,
                limit = pageSize
            )

            if (page.isEmpty()) break
            all.addAll(page)

            if (page.size < pageSize) break
            skip += pageSize
        }

        return all
    }

    private fun closePolygon(points: List<Pair<Double, Double>>): List<Pair<Double, Double>> {
        if (points.isEmpty()) return points
        return if (points.first() == points.last()) points else points + points.first()
    }
}
