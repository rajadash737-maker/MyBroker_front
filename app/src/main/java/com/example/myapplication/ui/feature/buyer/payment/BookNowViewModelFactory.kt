package com.example.myapplication.ui.feature.buyer.payment



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.ui.feature.buyer.domain.repository.LandRepository

class BookNowViewModelFactory(
    private val landRepository: LandRepository,
    private val requestId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookNowViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookNowViewModel(landRepository, requestId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
