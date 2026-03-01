package com.example.myapplication.ui.feature.buyer.domain.repository



interface WishlistRepository {

    suspend fun likeLand(landId: Int): Boolean

    suspend fun unlikeLand(landId: Int): Boolean
}
