package com.example.myapplication.ui.feature.buyer.land_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.ui.feature.buyer.domain.repository.LandRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LandDetailViewModel(
    private val landRepository: LandRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LandDetailUiState())
    val uiState: StateFlow<LandDetailUiState> = _uiState

    fun loadLand(id: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    offerSuccessMessage = null
                )
            }

            runCatching { landRepository.getLandDetail(id) }
                .onSuccess { detail ->
                    _uiState.update {
                        it.copy(
                            land = detail,
                            isLoading = false
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Failed to load land details"
                        )
                    }
                }
        }
    }

    fun onOfferInputChanged(value: String) {
        val numericOnly = value.filter { ch -> ch.isDigit() }
        _uiState.update { it.copy(offerInput = numericOnly) }
    }

    fun submitOffer() {
        val current = _uiState.value
        val land = current.land ?: return
        val requestId = land.id

        if (land.isOfferLocked) {
            _uiState.update {
                it.copy(errorMessage = "This land is temporarily locked for another buyer")
            }
            return
        }

        val amount = current.offerInput.toDoubleOrNull()

        if (amount == null || amount <= 0.0) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid numeric amount") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSubmittingOffer = true,
                    errorMessage = null,
                    offerSuccessMessage = null
                )
            }

            runCatching { landRepository.makeOffer(requestId, amount) }
                .onSuccess { ok ->
                    _uiState.update {
                        it.copy(
                            isSubmittingOffer = false,
                            offerSuccessMessage = if (ok) "Offer sent successfully" else null,
                            errorMessage = if (ok) null else "Offer not sent"
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isSubmittingOffer = false,
                            errorMessage = e.message ?: "Failed to send offer"
                        )
                    }
                }
        }
    }


    fun clearMessage() {
        _uiState.update { it.copy(errorMessage = null, offerSuccessMessage = null) }
    }
}
