package com.example.myapplication.ui.feature.buyer.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.ui.feature.buyer.domain.repository.LandRepository
import com.example.myapplication.ui.feature.buyer.domain.repository.WishlistRepository

class ExploreViewModelFactory(
    private val landRepository: LandRepository,
    private val wishlistRepository: WishlistRepository,
    private val showWishlistOnly: Boolean = false // NEW
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExploreViewModel::class.java)) {
            return ExploreViewModel(
                landRepository = landRepository,
                wishlistRepository = wishlistRepository,
                showWishlistOnly = showWishlistOnly // NEW
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
