package com.example.myapplication.ui.feature.buyer.payment

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.ui.feature.buyer.domain.repository.LandRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import android.util.Log
import kotlinx.coroutines.launch
import com.example.myapplication.ui.feature.buyer.utils.formatAreaWithUnit
import java.text.NumberFormat
import java.util.Locale

enum class BookNowStep { DETAILS, KYC, PAYMENT }

class BookNowViewModel(
    private val landRepository: LandRepository,
    private val requestId: Int
) : ViewModel() {

    private val _state = MutableStateFlow(BookNowState())
    val state: StateFlow<BookNowState> = _state.asStateFlow()

    init {
        loadDetails()
        loadDealAmountFromOffers()
    }

    private fun loadDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { landRepository.getLandDetail(requestId) }
                .onSuccess { land ->
                    _state.update {
                        val resolvedExpected = land.expectedPrice
                        val fallbackDeal = it.dealAmount ?: resolvedExpected
                        it.copy(
                            isLoading = false,
                            landTitle = land.title,
                            landType = land.landType,
                            areaText = formatAreaWithUnit(land.area, land.areaUnit),
                            city = land.city ?: "",
                            stateName = land.state ?: "",
                            pincode = land.pincode ?: "",
                            expectedPrice = resolvedExpected,
                            dealAmount = fallbackDeal,
                            discountPercent = computeDiscountPercent(resolvedExpected, fallbackDeal)
                        )
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load land details")
                    }
                }
        }
    }

    private fun loadDealAmountFromOffers() {
        viewModelScope.launch {
            runCatching { landRepository.getBuyerOffers() }
                .onSuccess { offers ->
                    val offer = offers.firstOrNull { it.requestId == requestId }
                    val isSellerAccepted =
                        offer?.offerStatus?.equals("accepted", true) == true ||
                            offer?.bookingStatus?.equals("accepted_book_now", true) == true ||
                            offer?.bookingStatus?.equals("booked", true) == true ||
                            offer?.bookingStatus?.equals("sold", true) == true ||
                            offer?.bookingStatus?.equals("registry_scheduled", true) == true ||
                            offer?.bookingStatus?.equals("in_process", true) == true ||
                            offer?.bookingStatus?.equals("registry_done", true) == true
                    val deal = if (isSellerAccepted) offer?.finalAmount else null
                    _state.update { current ->
                        val resolvedDeal = deal ?: current.expectedPrice ?: current.dealAmount
                        current.copy(
                            dealAmount = resolvedDeal,
                            discountPercent = computeDiscountPercent(current.expectedPrice, resolvedDeal)
                        )
                    }
                }
        }
    }

    private fun computeDiscountPercent(expected: Double?, deal: Double?): Int? {
        if (expected == null || expected <= 0.0 || deal == null || deal <= 0.0) return null
        val pct = ((expected - deal) / expected) * 100.0
        return pct.toInt().coerceAtLeast(0)
    }

    fun acceptTerms(checked: Boolean) {
        _state.update { it.copy(termsAccepted = checked) }
    }

    fun goToKyc() {
        if (!_state.value.termsAccepted) {
            _state.update { it.copy(errorMessage = "Please accept terms & conditions") }
            return
        }
        _state.update { it.copy(step = BookNowStep.KYC) }
    }

    fun goToPayment() {
        _state.update { it.copy(step = BookNowStep.PAYMENT) }
    }

    fun onBuyerNameChange(value: String) = _state.update { it.copy(buyerName = value) }
    fun onBuyerMobileChange(value: String) = _state.update { it.copy(buyerMobile = value) }

    fun onAadhaarPicked(uri: Uri?) = _state.update { it.copy(aadhaarUri = uri) }
    fun onPanPicked(uri: Uri?) = _state.update { it.copy(panUri = uri) }
    fun onBankPicked(uri: Uri?) = _state.update { it.copy(bankUri = uri) }
    fun onPaymentMethodChange(value: String) = _state.update { it.copy(paymentMethod = value) }
    fun onTransactionIdChange(value: String) = _state.update { it.copy(transactionId = value) }

    fun createBooking() {
        val s = _state.value
        if (s.buyerName.isBlank() || s.buyerMobile.isBlank()) {
            _state.update { it.copy(errorMessage = "Buyer name and mobile required") }
            return
        }
        if (s.aadhaarUri == null || s.panUri == null || s.bankUri == null) {
            _state.update { it.copy(errorMessage = "All 3 documents are required") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                landRepository.createBookingWithDocs(
                    requestId = requestId,
                    buyerName = s.buyerName,
                    buyerMobile = s.buyerMobile,
                    aadhaarUri = s.aadhaarUri,
                    panUri = s.panUri,
                    bankUri = s.bankUri
                )
            }.onSuccess { booking ->
                _state.update { current ->
                    val resolvedDeal = current.dealAmount ?: booking.dealAmount
                    current.copy(
                        isLoading = false,
                        bookingId = booking.bookingId,
                        dealAmount = resolvedDeal,
                        bookingFeeAmount = booking.bookingFeeAmount,
                        step = BookNowStep.PAYMENT
                    )
                }
            }.onFailure { e ->
                val http = e as? retrofit2.HttpException
                val body = http?.response()?.errorBody()?.string()
                Log.e("BookNow", "createBooking failed: code=${http?.code()} body=$body", e)
                val alreadyBooked = http?.code() == 409 &&
                    body?.contains("Booking already completed", ignoreCase = true) == true
                val message = when {
                    alreadyBooked -> "You already booked this land."
                    else -> body
                }
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = message ?: e.message ?: "Booking failed",
                        alreadyBooked = alreadyBooked
                    )
                }
            }
        }
    }

    fun createRazorpayOrder(userId: Int) {
        val amount = _state.value.bookingFeeAmount ?: 1000.0
        viewModelScope.launch {
            _state.update { it.copy(isPaymentLoading = true, paymentError = null) }
            runCatching {
                landRepository.createPaymentOrder(amount = amount, userId = userId, propertyId = requestId)
            }.onSuccess { order ->
                _state.update {
                    it.copy(
                        isPaymentLoading = false,
                        razorpayOrderId = order.orderId,
                        razorpayAmount = order.amount,
                        razorpayCurrency = order.currency,
                        checkoutReady = true
                    )
                }
            }.onFailure { e ->
                _state.update {
                    it.copy(isPaymentLoading = false, paymentError = e.message ?: "Failed to create payment order")
                }
            }
        }
    }

    fun onCheckoutLaunched() {
        _state.update { it.copy(checkoutReady = false) }
    }

    fun onPaymentSuccess(paymentId: String?, orderId: String?, signature: String?) {
        val resolvedOrderId = orderId ?: _state.value.razorpayOrderId
        if (paymentId.isNullOrBlank() || resolvedOrderId.isNullOrBlank() || signature.isNullOrBlank()) {
            _state.update { it.copy(paymentError = "Payment details missing. Please retry.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isPaymentLoading = true, paymentError = null) }
            runCatching {
                landRepository.verifyPayment(
                    orderId = resolvedOrderId,
                    paymentId = paymentId,
                    signature = signature
                )
            }.onSuccess {
                _state.update {
                    it.copy(
                        isPaymentLoading = false,
                        paymentCompleted = true,
                        successMessage = "Payment successful"
                    )
                }
            }.onFailure { e ->
                _state.update { it.copy(isPaymentLoading = false, paymentError = e.message ?: "Payment verification failed") }
            }
        }
    }

    fun onPaymentFailure(message: String?) {
        _state.update { it.copy(paymentError = message ?: "Payment failed. Please retry.") }
    }

    fun clearMessage() {
        _state.update { it.copy(errorMessage = null, successMessage = null, paymentError = null) }
    }

    fun formatAmount(value: Double?): String {
        if (value == null) return "-"
        val formatter = NumberFormat.getNumberInstance(Locale.Builder().setLanguage("en").setRegion("IN").build())
        return "₹ ${formatter.format(value)}"
    }

}
