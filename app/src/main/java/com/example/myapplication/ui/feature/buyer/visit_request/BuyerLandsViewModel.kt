package com.example.myapplication.ui.feature.buyer.visit_request

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.ui.feature.buyer.domain.repository.LandRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BuyerLandsViewModel(
    private val landRepository: LandRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BuyerLandsUiState())
    val uiState: StateFlow<BuyerLandsUiState> = _uiState.asStateFlow()

    init {
        loadBuyerOffers()
    }

    fun loadBuyerOffers() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    actionMessage = null
                )
            }

            runCatching {
                landRepository.getBuyerOffers()
            }.onSuccess { offers ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        offers = offers,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Failed to load buyer offers"
                    )
                }
            }
        }
    }

    fun onFilterChange(filter: BuyerOfferFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun acceptCounterOffer(offerId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, actionMessage = null) }

            runCatching {
                landRepository.acceptCounterOffer(offerId)
            }.onSuccess { result ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        actionMessage = result.status
                    )
                }
                loadBuyerOffers()
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Failed to accept counter offer"
                    )
                }
            }
        }
    }

    fun rejectCounterOffer(offerId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, actionMessage = null) }

            runCatching {
                landRepository.rejectCounterOffer(offerId)
            }.onSuccess { result ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        actionMessage = result.status
                    )
                }
                loadBuyerOffers()
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Failed to reject counter offer"
                    )
                }
            }
        }
    }

    fun counterAgain(requestId: Int, amountText: String) {
        val amount = amountText.trim().toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            _uiState.update { it.copy(errorMessage = "Please enter valid amount") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, actionMessage = null) }

            runCatching {
                landRepository.makeOffer(requestId = requestId, amount = amount)
            }.onSuccess { result ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        actionMessage = result.status
                    )
                }
                loadBuyerOffers()
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Failed to send counter offer again"
                    )
                }
            }
        }
    }

    fun clearActionMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
