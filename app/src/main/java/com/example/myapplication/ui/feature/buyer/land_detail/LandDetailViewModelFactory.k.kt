package com.example.myapplication.ui.feature.buyer.land_detail



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.ui.feature.buyer.domain.repository.LandRepository

class LandDetailViewModelFactory(
    private val landRepository: LandRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LandDetailViewModel::class.java)) {
            return LandDetailViewModel(landRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
