package com.example.myapplication.ui.feature.buyer.visit_request



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.ui.feature.buyer.domain.repository.LandRepository

class BuyerLandsViewModelFactory(
    private val landRepository: LandRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BuyerLandsViewModel::class.java)) {
            return BuyerLandsViewModel(landRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
