package com.example.myapplication.ui.feature.sell_land.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.ApiClient
import com.example.myapplication.ui.feature.sell_land.model.SellerDetails
import com.example.myapplication.ui.feature.sell_land.utils.DraftMapper
import com.example.myapplication.ui.feature.sell_land.utils.SellLandValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.google.android.gms.maps.model.LatLng
import com.example.myapplication.ui.feature.sell_land.model.SellLandDraft
import com.example.myapplication.ui.feature.sell_land.model.SellLandDraftRequest
import com.example.myapplication.ui.feature.sell_land.model.LandDetails
import com.example.myapplication.ui.feature.sell_land.model.PricingDetails
import com.example.myapplication.ui.feature.sell_land.model.SellLandStep
import com.example.myapplication.ui.feature.sell_land.model.SubmitResponse
import android.content.ContentResolver
import android.net.Uri
import java.io.File
import com.example.myapplication.ui.feature.sell_land.model.SellLandModel
import com.example.myapplication.ui.feature.sell_land.status.ListingStatus
import com.example.myapplication.ui.feature.sell_land.model.SellLandResponse
import com.example.myapplication.ui.feature.sell_land.model.toSellLandModel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


class SellLandViewModel : ViewModel() {

    // 🔹 Single source of truth
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

    // Add near other state flows (after _submitResult is fine)
    private val _currentPaymentStatus = MutableStateFlow<String?>(null)
    val currentPaymentStatus: StateFlow<String?> = _currentPaymentStatus


    // -------------------------------
    // SELLER DETAILS
    // -------------------------------
    fun updateSellerDetails(details: SellerDetails) {
        _draft.update {
            it.copy(
                sellerDetails = details,
                lastStep = SellLandStep.SELLER
            )
        }
    }

    // -------------------------------
    // LAND DETAILS
    // -------------------------------
    fun updateLandDetails(details: LandDetails) {
        _draft.update {
            it.copy(
                landDetails = details,
                lastStep = SellLandStep.LAND
            )
        }
    }

    // -------------------------------
    // MEDIA
    // -------------------------------
    fun updateImages(images: List<String>) {
        _draft.update {
            it.copy(
                images = images,
                lastStep = SellLandStep.MEDIA
            )
        }
    }




    // -------------------------------
    // MAP BOUNDARY
    // -------------------------------
    fun updatePolygon(points: List<LatLng>) {
        if (!SellLandValidator.isPolygonValid(points)) return

        _draft.update {
            it.copy(
                polygon = points,
                lastStep = SellLandStep.MAP
            )
        }
    }

    // -------------------------------
    // PRICING
    // -------------------------------
    fun updatePricing(pricing: PricingDetails) {
        if (!SellLandValidator.isPricingValid(pricing.price)) return

        _draft.update {
            it.copy(
                pricing = pricing,
                lastStep = SellLandStep.PRICING
            )
        }
    }

    // -------------------------------
    // SAVE DRAFT (Manual)
    // -------------------------------


    private fun normalizeOwnershipType(value: String?): String? = when (value?.trim()?.lowercase()) {
        "owner" -> "owner"
        "broker" -> "broker"
        "power_of_attorney", "power of attorney", "power-of-attorney" -> "power_of_attorney"
        "other" -> "other"
        else -> null
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

                val body = SellLandDraftRequest(
                    title = d.landDetails?.title?.trim(),
                    ownerName = d.sellerDetails?.name?.trim(),
                    ownerMobile = d.sellerDetails?.mobile?.trim(),
                    ownershipType = normalizeOwnershipType(d.sellerDetails?.sellerType),
                    landType = normalizeLandType(d.landDetails?.landType),
                    area = d.landDetails?.area?.toDoubleOrNull(),
                    areaUnit = normalizeAreaUnit(d.landDetails?.areaUnit),
                    state = d.landDetails?.state?.trim(),
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

    //
    //
    //API CALL

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
            val result = remote.saveBoundary(requestId, points)
            result.onFailure { _error.value = it.message ?: "Boundary save failed" }
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
            val result = remote.uploadPhoto(requestId, file)
            result.onSuccess { media ->
                _draft.update { it.copy(images = it.images + media.fileUrl, lastStep = SellLandStep.MEDIA) }
            }.onFailure {
                _error.value = it.message ?: "Photo upload failed"
            }
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

            val mediaResult = remote.getListingMedia(requestId)
            mediaResult
                .onSuccess { mediaList: List<com.example.myapplication.ui.feature.sell_land.model.SellLandMediaResponse> ->
                    val target = mediaList.firstOrNull { media ->
                        media.fileUrl == imageUrl && media.mediaType == "photo"
                    }

                    if (target == null) {
                        _error.value = "Photo not found on server"
                    } else {
                        remote.deleteListingMedia(requestId, target.id)
                            .onSuccess {
                                _draft.update { d ->
                                    d.copy(
                                        images = d.images - imageUrl,
                                        lastStep = SellLandStep.MEDIA
                                    )
                                }
                            }
                            .onFailure { ex ->
                                _error.value = ex.message ?: "Delete failed"
                            }
                    }
                }
                .onFailure { ex ->
                    _error.value = ex.message ?: "Media fetch failed"
                }

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
            val result = remote.uploadDocument(requestId, documentType, file)

            result.onSuccess { media ->
                _draft.update { current ->

                    val updatedDocs = when (documentType) {
                        "sale_deed" -> current.documents.copy(saleDeed = media.fileUrl)
                        "khata_patta" -> current.documents.copy(khataCertificate = media.fileUrl)
                        "encumbrance_certificate" -> current.documents.copy(encumbrance = media.fileUrl)
                        "seller_identity_proof" -> current.documents.copy(sellerIdentity = media.fileUrl)
                        else -> current.documents
                    }


                    current.copy(
                        documents = updatedDocs,
                        lastStep = SellLandStep.MEDIA
                    )
                }
            }.onFailure {
                _error.value = it.message ?: "Document upload failed"
            }

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
            val result = remote.initiatePayment(requestId, method)
            result.onSuccess { payment ->
                onPaymentCreated(payment.id)
            }.onFailure {
                _error.value = it.message ?: "Payment initiation failed"
            }
            _loading.value = false
        }
    }

    fun markPaymentPaid(
        paymentId: Int,
        providerRef: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            val result = remote.markPaid(paymentId, providerRef)
            result.onSuccess {
                onSuccess()
            }.onFailure {
                _error.value = it.message ?: "Mark paid failed"
            }
            _loading.value = false
        }
    }

    fun markPaidThenSubmit(paymentId: Int, providerRef: String? = null) {
        markPaymentPaid(paymentId, providerRef) {
            preSubmitCheck {
                submitListing()
            }
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
            val result = remote.submit(requestId)
            result.onSuccess { submit ->
                _submitResult.value = submit
            }.onFailure {
                _error.value = it.message ?: "Submit failed"
            }
            _loading.value = false
        }
    }



    // -------------------------------
    // CLEAR DRAFT (After Success)
    // -------------------------------
    // Replace clearDraft()
    fun clearDraft() {
        _draft.value = SellLandDraft()
        _currentRequestId.value = null
        _currentPaymentStatus.value = null
    }


    fun clearSubmitResult() {
        _submitResult.value = null
    }

    fun isDraftEmpty(): Boolean {
        return DraftMapper.isDraftEmpty(_draft.value)
    }

    fun canMoveNext(): Boolean {
        return DraftMapper.canProceedToNextStep(_draft.value)
    }

    private fun buildPreSubmitErrors(
        mediaList: List<com.example.myapplication.ui.feature.sell_land.model.SellLandMediaResponse>
    ): List<String> {
        val errors = mutableListOf<String>()

        val hasPhoto = mediaList.any { it.mediaType == "photo" }
        if (!hasPhoto) errors.add("At least one land photo is required")

        val uploadedDocs = mediaList
            .filter { it.mediaType == "document" }
            .mapNotNull { it.documentType }
            .toSet()

        val requiredDocs = setOf(
            "sale_deed",
            "khata_patta",
            "encumbrance_certificate",
            "seller_identity_proof"
        )

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
        if (d.landDetails?.pincode.isNullOrBlank()) errors.add("Pincode is required")
        val areaValue = "${d.landDetails?.area}".toDoubleOrNull() ?: 0.0
        if (areaValue <= 0.0) errors.add("Area is required")

        val priceValue = "${d.pricing?.price}".toDoubleOrNull() ?: 0.0
        if (priceValue <= 0.0) errors.add("Expected price is required")


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
                    if (errors.isEmpty()) onValid()
                    else _error.value = errors.joinToString("\n")
                }
                .onFailure { ex ->
                    _error.value = ex.message ?: "Pre-check failed"
                }

            _loading.value = false
        }
    }

    fun updateStatus(newStatus: ListingStatus) {
        _draft.update {
            it.copy(status = newStatus)
        }
    }

    private val _statusListings = MutableStateFlow<List<SellLandModel>>(emptyList())
    val statusListings: StateFlow<List<SellLandModel>> = _statusListings

    fun loadStatusListings() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            remote.getMyListingsTracking()
                .onSuccess { apiList: List<SellLandResponse> ->
                    _statusListings.value = apiList.map { response -> response.toSellLandModel() }
                }
                .onFailure { ex ->
                    _error.value = ex.message ?: "Failed to load listings"
                }

            _loading.value = false
        }
    }
    // EDIT LIST ===============
    fun setCurrentRequestId(requestId: Int) {
        _currentRequestId.value = requestId
    }

    // Replace loadListingForEdit()
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
                .onFailure { ex ->
                    _error.value = ex.message ?: "Failed to load listing"
                }

            _loading.value = false
        }
    }

    // Optional helper (useful in ReviewScreen)
    fun isAlreadyPaid(): Boolean {
        return _currentPaymentStatus.value.equals("success", ignoreCase = true)
    }



    // CANCEL BOTTON

    // CANCEL BUTTON DEMO STATE


    fun cancelListing(id: String) {
        _statusListings.value = _statusListings.value.map { listing ->
            if (listing.id == id)
                listing.copy(status = ListingStatus.CANCELLED)
            else listing
        }
    }


}
