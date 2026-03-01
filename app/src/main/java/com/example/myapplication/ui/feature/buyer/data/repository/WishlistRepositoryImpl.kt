package com.example.myapplication.ui.feature.buyer.data.repository



import com.example.myapplication.ui.feature.buyer.data.remote.LandApi
import com.example.myapplication.ui.feature.buyer.domain.repository.WishlistRepository

class WishlistRepositoryImpl(
    private val landApi: LandApi
) : WishlistRepository {

    override suspend fun likeLand(landId: Int): Boolean {
        return landApi.likeLand(landId).liked
    }

    override suspend fun unlikeLand(landId: Int): Boolean {
        return landApi.unlikeLand(landId).liked
    }
}
