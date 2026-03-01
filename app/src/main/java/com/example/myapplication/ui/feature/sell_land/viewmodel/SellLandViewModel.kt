package com.example.myapplication.ui.feature.sell_land.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.ApiClient
import com.example.myapplication.ui.feature.sell_land.model.LandDetails
import com.example.myapplication.ui.feature.sell_land.model.OfferModel
import com.example.myapplication.ui.feature.sell_land.model.PricingDetails
import com.example.myapplication.ui.feature.sell_land.model.SellLandDraft
import com.example.myapplication.ui.feature.sell_land.model.SellLandDraftRequest
import com.example.myapplication.ui.feature.sell_land.model.SellLandModel
import com.example.myapplication.ui.feature.sell_land.model.SellLandResponse
import com.example.myapplication.ui.feature.sell_land.model.SellLandStep
import com.example.myapplication.ui.feature.sell_land.model.SellerDetails
import com.example.myapplication.ui.feature.sell_land.model.SubmitResponse
import com.example.myapplication.ui.feature.sell_land.model.toSellLandModel
import com.example.myapplication.ui.feature.sell_land.status.ListingStatus
import com.example.myapplication.ui.feature.sell_land.status.OfferStatus
import com.example.myapplication.ui.feature.sell_land.utils.DraftMapper
import com.example.myapplication.ui.feature.sell_land.utils.SellLandValidator
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class SellLandViewModel : ViewModel() {

    private val remote = SellLandRemote()

    private val _draft = MutableStateFlow(DraftMapper.emptyDraft())
    val draft: StateFlow<SellLandDraft> = _draft

    private val _currentRequestId = MutableStateFlow<Int?>(null)
    val currentRequestId: StateFlow<Int?> = _currentRequestId

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _submitResult = MutableStateFlow<SubmitResponse?>(null)
    val submitResult: StateFlow<SubmitResponse?> = _submitResult

    private val _currentPaymentStatus = MutableStateFlow<String?>(null)
    val currentPaymentStatus: StateFlow<String?> = _currentPaymentStatus

    private val _statusListings = MutableStateFlow<List<SellLandModel>>(emptyList())
    val statusListings: StateFlow<List<SellLandModel>> = _statusListings

    // ---------------- OFFERS ----------------
    private val _sellerOffers = MutableStateFlow<List<OfferModel>>(emptyList())
    val sellerOffers: StateFlow<List<OfferModel>> = _sellerOffers

    private val _isOfferLocked = MutableStateFlow(false)
    val isOfferLocked: StateFlow<Boolean> = _isOfferLocked

    private val _offerLockExpiresAt = MutableStateFlow<String?>(null)
    val offerLockExpiresAt: StateFlow<String?> = _offerLockExpiresAt

    private val odishaCities = setOf(
        "Angul", "Balangir", "Balasore", "Bargarh", "Bhadrak", "Boudh", "Cuttack",
        "Deogarh", "Dhenkanal", "Gajapati", "Ganjam", "Jagatsinghpur", "Jajpur",
        "Jharsuguda", "Kalahandi", "Kandhamal", "Kendrapara", "Kendujhar (Keonjhar)",
        "Khordha", "Koraput", "Malkangiri", "Mayurbhanj", "Nabarangpur", "Nayagarh",
        "Nuapada", "Puri", "Rayagada", "Sambalpur", "Subarnapur (Sonepur)", "Sundargarh"
    )

    private fun normalizeOdishaState(value: String?): String {
        return if (value?.trim().equals("odisha", ignoreCase = true)) "Odisha" else value?.trim().orEmpty()
    }

    private fun isValidOdishaLocation(state: String?, city: String?): Boolean {
        val s = state?.trim().orEmpty()
        val c = city?.trim().orEmpty()
        return s.equals("Odisha", ignoreCase = true) && odishaCities.contains(c)
    }


    fun updateSellerDetails(details: SellerDetails) {
        _draft.update { it.copy(sellerDetails = details, lastStep = SellLandStep.SELLER) }
    }

  
    fun updateLandDetails(details: LandDetails) {
        _draft.update {
            it.copy(
                landDetails = details.copy(state = normalizeOdishaState(details.state)),
                lastStep = SellLandStep.LAND
            )
        }
    }


    fun updateImages(images: List<String>) {
        _draft.update { it.copy(images = images, lastStep = SellLandStep.MEDIA) }
    }

    fun updatePolygon(points: List<LatLng>) {
        if (!SellLandValidator.isPolygonValid(points)) return
        _draft.update { it.copy(polygon = points, lastStep = SellLandStep.MAP) }
    }

    fun updatePricing(pricing: PricingDetails) {
        if (!SellLandValidator.isPricingValid(pricing.price)) return
        _draft.update { it.copy(pricing = pricing, lastStep = SellLandStep.PRICING) }
    }

    private fun normalizeOwnershipType(value: String?): String = when (value?.trim()?.lowercase()) {
        // new values
        "single_owner" -> "single_owner"
        "multi_owner" -> "multi_owner"
        "inherited_land" -> "inherited_land"

        // legacy fallback
        "owner" -> "single_owner"
        "broker" -> "multi_owner"
        "power_of_attorney", "power of attorney", "power-of-attorney" -> "inherited_land"
        "other" -> "single_owner"

        else -> "single_owner"
    }


    private fun requiredDocsForSellerType(rawSellerType: String?): Set<String> {
        return when (normalizeOwnershipType(rawSellerType)) {
            "multi_owner" -> setOf(
                "sale_deed",
                "all_co_owner_aadhaar",
                "joint_affidavit",
                "encumbrance_certificate",
                "latest_tax_receipt"
            )

            "inherited_land" -> setOf(
                "original_sale_deed",
                "death_certificate",
                "legal_heir_certificate",
                "all_heirs_aadhaar",
                "consent_affidavit_all_heirs",
                "encumbrance_certificate",
                "latest_tax_receipt"
            )

            else -> setOf(
                "sale_deed",
                "khata_patta",
                "encumbrance_certificate",
                "latest_tax_receipt",
                "aadhaar_pan"
            )
        }
    }



    private fun normalizeLandType(value: String?): String? = when (value?.trim()?.lowercase()) {
        "residential" -> "residential"
        "commercial" -> "commercial"
        "agricultural" -> "agricultural"
        "industrial" -> "industrial"
        "other" -> "other"
        else -> null
    }

    private fun normalizeAreaUnit(value: String?): String? = when (value?.trim()?.lowercase()) {
        "sqft", "sq.ft", "sq ft" -> "sqft"
        "sqm", "sq.m", "sq m" -> "sqm"
        "acre" -> "acre"
        "bigha" -> "bigha"
        else -> null
    }

    private fun normalizePriceType(value: String?): String = when (value?.trim()?.lowercase()) {
        "total price", "total_price" -> "total_price"
        "price per unit", "price_per_unit", "price per sq.ft", "price per sqft" -> "price_per_unit"
        else -> "total_price"
    }

    private fun normalizeAvailability(value: String?): String? = when (value?.trim()?.lowercase()) {
        "immediate" -> "immediate"
        "within 3 months", "within_3_months" -> "within_3_months"
        else -> null
    }

    fun saveDraft(step: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val d = _draft.value
                val normalizedState = normalizeOdishaState(d.landDetails?.state)
                val body = SellLandDraftRequest(
                    title = d.landDetails?.title?.trim(),
                    ownerName = d.sellerDetails?.name?.trim(),
                    ownerMobile = d.sellerDetails?.mobile?.trim(),
                    ownershipType = normalizeOwnershipType(d.sellerDetails?.sellerType),
                    landType = normalizeLandType(d.landDetails?.landType),
                    area = d.landDetails?.area?.toDoubleOrNull(),
                    areaUnit = normalizeAreaUnit(d.landDetails?.areaUnit),
                    state = normalizedState,
                    city = d.landDetails?.city?.trim(),
                    pincode = d.landDetails?.pincode?.trim(),
                    expectedPrice = d.pricing?.price,
                    negotiable = d.pricing?.negotiable,
                    availability = normalizeAvailability(d.pricing?.availability),
                    priceType = normalizePriceType(d.pricing?.priceType),
                    draftStep = step
                )

                val res = ApiClient.apiService.saveDraft(_currentRequestId.value, body)
                if (res.isSuccessful && res.body() != null) {
                    _currentRequestId.value = res.body()!!.id
                } else {
                    val raw = res.errorBody()?.string()
                    _error.value = "Draft save failed (${res.code()}): ${raw ?: "Unknown error"}"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Network error"
            } finally {
                _loading.value = false
            }
        }
    }

    fun canMoveNext(): Boolean {
        val d = _draft.value
        val details = d.landDetails ?: return false
        val basic = DraftMapper.canProceedToNextStep(d)
        val pincodeOk = details.pincode?.trim()?.length == 6
        return basic && pincodeOk && isValidOdishaLocation(details.state, details.city)
    }


    fun saveBoundary() {
        val requestId = _currentRequestId.value ?: run {
            _error.value = "Draft ID missing. Pehle draft save karo."
            return
        }

        val points = _draft.value.polygon
        if (!SellLandValidator.isPolygonValid(points)) {
            _error.value = "Boundary points invalid"
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            remote.saveBoundary(requestId, points)
                .onFailure { _error.value = it.message ?: "Boundary save failed" }
            _loading.value = false
        }
    }

    fun uploadPhoto(file: File) {
        val requestId = _currentRequestId.value ?: run {
            _error.value = "Draft ID missing. Pehle draft save karo."
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            remote.uploadPhoto(requestId, file)
                .onSuccess { media ->
                    _draft.update { it.copy(images = it.images + media.fileUrl, lastStep = SellLandStep.MEDIA) }
                }
                .onFailure { _error.value = it.message ?: "Photo upload failed" }
            _loading.value = false
        }
    }

    fun uploadPhotoFromUri(uri: Uri, contentResolver: ContentResolver, cacheDir: File) {
        viewModelScope.launch {
            try {
                val file = remote.uriToTempFile(uri, contentResolver, cacheDir)
                uploadPhoto(file)
            } catch (e: Exception) {
                _error.value = e.message ?: "Photo read failed"
            }
        }
    }

    fun removePhotoByUrl(imageUrl: String) {
        val requestId = _currentRequestId.value ?: run {
            _error.value = "Draft ID missing. Pehle draft save karo."
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            remote.getListingMedia(requestId)
                .onSuccess { mediaList ->
                    val target = mediaList.firstOrNull { media ->
                        media.fileUrl == imageUrl && media.mediaType == "photo"
                    }

                    if (target == null) {
                        _error.value = "Photo not found on server"
                    } else {
                        remote.deleteListingMedia(requestId, target.id)
                            .onSuccess {
                                _draft.update { d ->
                                    d.copy(images = d.images - imageUrl, lastStep = SellLandStep.MEDIA)
                                }
                            }
                            .onFailure { ex -> _error.value = ex.message ?: "Delete failed" }
                    }
                }
                .onFailure { ex -> _error.value = ex.message ?: "Media fetch failed" }

            _loading.value = false
        }
    }

    fun uploadDocument(documentType: String, file: File) {
        val requestId = _currentRequestId.value ?: run {
            _error.value = "Draft ID missing. Pehle draft save karo."
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            remote.uploadDocument(requestId, documentType, file)
                .onSuccess { media ->
                    _draft.update { current ->
                        val updatedDocs = when (documentType) {
                            "sale_deed" -> current.documents.copy(saleDeed = media.fileUrl)
                            "khata_patta" -> current.documents.copy(khataCertificate = media.fileUrl)
                            "encumbrance_certificate" -> current.documents.copy(encumbrance = media.fileUrl)
                            "seller_identity_proof" -> current.documents.copy(sellerIdentity = media.fileUrl)
                            else -> current.documents
                        }
                        current.copy(documents = updatedDocs, lastStep = SellLandStep.MEDIA)
                    }
                }
                .onFailure { _error.value = it.message ?: "Document upload failed" }

            _loading.value = false
        }
    }

    fun uploadDocumentFromUri(
        documentType: String,
        uri: Uri,
        contentResolver: ContentResolver,
        cacheDir: File
    ) {
        viewModelScope.launch {
            try {
                val file = remote.uriToTempFile(uri, contentResolver, cacheDir)
                uploadDocument(documentType, file)
            } catch (e: Exception) {
                _error.value = e.message ?: "Document read failed"
            }
        }
    }

    fun initiatePayment(method: String, onPaymentCreated: (paymentId: Int) -> Unit = {}) {
        val requestId = _currentRequestId.value ?: run {
            _error.value = "Draft ID missing. Pehle draft save karo."
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            remote.initiatePayment(requestId, method)
                .onSuccess { onPaymentCreated(it.id) }
                .onFailure { _error.value = it.message ?: "Payment initiation failed" }
            _loading.value = false
        }
    }

    fun markPaymentPaid(paymentId: Int, providerRef: String? = null, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            remote.markPaid(paymentId, providerRef)
                .onSuccess { onSuccess() }
                .onFailure { _error.value = it.message ?: "Mark paid failed" }
            _loading.value = false
        }
    }

    fun markPaidThenSubmit(paymentId: Int, providerRef: String? = null) {
        markPaymentPaid(paymentId, providerRef) {
            preSubmitCheck { submitListing() }
        }
    }

    fun submitListing() {
        val requestId = _currentRequestId.value ?: run {
            _error.value = "Draft ID missing. Pehle draft save karo."
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            remote.submit(requestId)
                .onSuccess { _submitResult.value = it }
                .onFailure { _error.value = it.message ?: "Submit failed" }
            _loading.value = false
        }
    }

    fun clearDraft() {
        _draft.value = SellLandDraft()
        _currentRequestId.value = null
        _currentPaymentStatus.value = null
    }

    fun clearSubmitResult() {
        _submitResult.value = null
    }

    fun isDraftEmpty(): Boolean = DraftMapper.isDraftEmpty(_draft.value)



    private fun buildPreSubmitErrors(
        mediaList: List<com.example.myapplication.ui.feature.sell_land.model.SellLandMediaResponse>
    ): List<String> {
        val errors = mutableListOf<String>()

        if (mediaList.none { it.mediaType == "photo" }) errors.add("At least one land photo is required")

        val uploadedDocs = mediaList
            .filter { it.mediaType == "document" }
            .mapNotNull { it.documentType }
            .toSet()

        val requiredDocs = requiredDocsForSellerType(_draft.value.sellerDetails?.sellerType)
        val missingDocs = requiredDocs - uploadedDocs
        if (missingDocs.isNotEmpty()) {
            errors.add("Missing documents: ${missingDocs.joinToString()}")
        }

        if (_draft.value.polygon.size < 3) errors.add("Boundary points are required")

        val d = _draft.value
        if (d.landDetails?.title.isNullOrBlank()) errors.add("Title is required")
        if (d.sellerDetails?.name.isNullOrBlank()) errors.add("Owner name is required")
        if (d.sellerDetails?.mobile.isNullOrBlank()) errors.add("Owner mobile is required")
        if (d.landDetails?.state.isNullOrBlank()) errors.add("State is required")
        if (d.landDetails?.city.isNullOrBlank()) errors.add("City is required")
        if (!isValidOdishaLocation(d.landDetails?.state, d.landDetails?.city)) {
            errors.add("Please select valid Odisha state and city from list")
        }
        if (d.landDetails?.pincode.isNullOrBlank()) errors.add("Pincode is required")
        if ("${d.landDetails?.area}".toDoubleOrNull()?.let { it <= 0.0 } != false) errors.add("Area is required")
        if ("${d.pricing?.price}".toDoubleOrNull()?.let { it <= 0.0 } != false) errors.add("Expected price is required")

        return errors
    }

    fun preSubmitCheck(onValid: () -> Unit = {}) {
        val requestId = _currentRequestId.value ?: run {
            _error.value = "Draft ID missing. Pehle draft save karo."
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            remote.getListingMedia(requestId)
                .onSuccess { mediaList ->
                    val errors = buildPreSubmitErrors(mediaList)
                    if (errors.isEmpty()) onValid() else _error.value = errors.joinToString("\n")
                }
                .onFailure { ex -> _error.value = ex.message ?: "Pre-check failed" }

            _loading.value = false
        }
    }

    fun updateStatus(newStatus: ListingStatus) {
        _draft.update { it.copy(status = newStatus) }
    }

    fun loadStatusListings() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            remote.getMyListingsTracking()
                .onSuccess { apiList: List<SellLandResponse> ->
                    _statusListings.value = apiList.map { it.toSellLandModel() }
                }
                .onFailure { ex -> _error.value = ex.message ?: "Failed to load listings" }

            _loading.value = false
        }
    }

    fun setCurrentRequestId(requestId: Int) {
        _currentRequestId.value = requestId
    }

    fun loadListingForEdit(requestId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _currentRequestId.value = requestId

            val detailResult = remote.getListingDetails(requestId)
            val mediaResult = remote.getListingMedia(requestId)

            detailResult
                .onSuccess { detail ->
                    _currentPaymentStatus.value = detail.paymentStatus
                    val media = mediaResult.getOrNull().orEmpty()
                    _draft.value = DraftMapper.fromExistingListing(detail, media)
                }
                .onFailure { ex -> _error.value = ex.message ?: "Failed to load listing" }

            _loading.value = false
        }
    }

    fun isAlreadyPaid(): Boolean = _currentPaymentStatus.value.equals("success", ignoreCase = true)

    private val _cancelSuccessMessage = MutableStateFlow<String?>(null)
    val cancelSuccessMessage: StateFlow<String?> = _cancelSuccessMessage

    fun clearCancelSuccessMessage() {
        _cancelSuccessMessage.value = null
    }

    fun cancelListing(
        requestId: Int,
        reason: String,
        upiId: String,
        notes: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            remote.cancelListing(requestId, reason, upiId, notes)
                .onSuccess { res ->
                    _cancelSuccessMessage.value = res.status
                    loadStatusListings()
                    onSuccess()
                }
                .onFailure { ex -> _error.value = ex.message ?: "Cancel listing failed" }

            _loading.value = false
        }
    }

    // ---------------- OFFER ACTIONS ----------------

    private fun mapOfferStatus(raw: String): OfferStatus = when (raw.trim().uppercase()) {
        "PENDING" -> OfferStatus.PENDING
        "ACCEPTED" -> OfferStatus.ACCEPTED
        "REJECTED" -> OfferStatus.REJECTED
        "COUNTERED" -> OfferStatus.COUNTERED
        "EXPIRED" -> OfferStatus.EXPIRED
        else -> OfferStatus.PENDING
    }

    fun loadSellerOffers(requestId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            remote.getListingOffers(requestId)
                .onSuccess { response ->
                    _isOfferLocked.value = response.isOfferLocked
                    _offerLockExpiresAt.value = response.offerLockExpiresAt
                    _sellerOffers.value = response.items.map { item ->
                        OfferModel(
                            id = item.id.toString(),
                            buyerId = item.buyerId,
                            buyerName = item.buyer.fullname,
                            buyerMobile = item.buyer.mobile,
                            amount = item.offeredAmount,
                            counterAmount = item.counterAmount,
                            status = mapOfferStatus(item.offerStatus)
                        )
                    }
                }
                .onFailure { ex -> _error.value = ex.message ?: "Failed to load offers" }

            _loading.value = false
        }
    }

    fun acceptOffer(requestId: Int, offerId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            remote.acceptOffer(requestId, offerId)
                .onSuccess {
                    loadSellerOffers(requestId)
                    loadStatusListings()
                }
                .onFailure { ex -> _error.value = ex.message ?: "Accept offer failed" }

            _loading.value = false
        }
    }

    fun rejectOffer(requestId: Int, offerId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            remote.rejectOffer(requestId, offerId)
                .onSuccess { loadSellerOffers(requestId) }
                .onFailure { ex -> _error.value = ex.message ?: "Reject offer failed" }

            _loading.value = false
        }
    }

    fun counterOffer(requestId: Int, offerId: Int, counterAmount: Double) {
        if (counterAmount <= 0.0) {
            _error.value = "Counter amount should be greater than 0"
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            remote.counterOffer(requestId, offerId, counterAmount)
                .onSuccess { loadSellerOffers(requestId) }
                .onFailure { ex -> _error.value = ex.message ?: "Counter offer failed" }

            _loading.value = false
        }
    }
}
