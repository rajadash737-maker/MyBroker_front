package com.example.myapplication.ui.feature.buyer.data.repository

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.myapplication.network.ApiClient
import com.example.myapplication.ui.feature.buyer.data.mapper.toDomain
import com.example.myapplication.ui.feature.buyer.data.remote.LandApi
import com.example.myapplication.ui.feature.buyer.data.remote.dto.BookingPaymentRequestDto
import com.example.myapplication.ui.feature.buyer.data.remote.dto.CreateOrderRequestDto
import com.example.myapplication.ui.feature.buyer.data.remote.dto.ExploreLandItemDto
import com.example.myapplication.ui.feature.buyer.data.remote.dto.MakeOfferRequestDto
import com.example.myapplication.ui.feature.buyer.data.remote.dto.VerifyPaymentRequestDto
import com.example.myapplication.ui.feature.buyer.domain.model.Booking
import com.example.myapplication.ui.feature.buyer.domain.model.BuyerOffer
import com.example.myapplication.ui.feature.buyer.domain.model.BuyerOfferActionResult
import com.example.myapplication.ui.feature.buyer.domain.model.BuyerOfferCreateResult
import com.example.myapplication.ui.feature.buyer.domain.model.PaymentOrder
import com.example.myapplication.ui.feature.buyer.domain.model.PaymentVerification
import com.example.myapplication.ui.feature.buyer.domain.repository.LandRepository
import com.example.myapplication.ui.feature.buyer.explore.Land
import com.example.myapplication.ui.feature.buyer.land_detail.LandDetail
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class LandRepositoryImpl(
    private val landApi: LandApi,
    private val contentResolver: ContentResolver
) : LandRepository {

    private val tag = "LandRepository"
    override suspend fun searchLands(
        search: String?,
        minPrice: Int?,
        maxPrice: Int?,
        minArea: Int?,
        maxArea: Int?,
        sort: String,
        skip: Int,
        limit: Int
    ): List<Land> {
        val response = landApi.exploreLands(
            search = search,
            minPrice = minPrice,
            maxPrice = maxPrice,
            minArea = minArea,
            maxArea = maxArea,
            sort = sort,
            skip = skip,
            limit = limit
        )
        return response.items.map(::mapToLand)
    }

    override suspend fun getNearbyLands(
        skip: Int,
        limit: Int,
        sort: String
    ): List<Land> {
        val response = landApi.nearbyLands(
            skip = skip,
            limit = limit,
            sort = sort
        )
        return response.items.map(::mapToLand)
    }

    override suspend fun getLandDetail(requestId: Int): LandDetail {
        val dto = landApi.getLandDetail(requestId)
        return mapToLandDetail(dto)
    }

    override suspend fun getBuyerLandDetail(requestId: Int): LandDetail {
        val dto = landApi.getBuyerLandDetail(requestId)
        return mapToLandDetail(dto)
    }

    override suspend fun makeOffer(requestId: Int, amount: Double): BuyerOfferCreateResult {
        val response = landApi.makeOffer(
            requestId = requestId,
            body = MakeOfferRequestDto(amount = amount)
        )
        return BuyerOfferCreateResult(
            status = response.status,
            requestId = response.requestId,
            offerId = response.offerId,
            offerStatus = response.offerStatus
        )
    }

    override suspend fun getBuyerOffers(): List<BuyerOffer> {
        val response = landApi.getBuyerOffers()
        return response.toDomain()
    }

    override suspend fun acceptCounterOffer(offerId: Int): BuyerOfferActionResult {
        val response = landApi.acceptCounterOffer(offerId = offerId)
        return response.toDomain()
    }

    override suspend fun rejectCounterOffer(offerId: Int): BuyerOfferActionResult {
        val response = landApi.rejectCounterOffer(offerId = offerId)
        return response.toDomain()
    }

    override suspend fun createBookingWithDocs(
        requestId: Int,
        buyerName: String,
        buyerMobile: String,
        aadhaarUri: Uri,
        panUri: Uri,
        bankUri: Uri
    ): Booking {
        val requestIdPart = requestId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val buyerNamePart = buyerName.toRequestBody("text/plain".toMediaTypeOrNull())
        val buyerMobilePart = buyerMobile.toRequestBody("text/plain".toMediaTypeOrNull())

        val aadhaarPart = createFilePart("aadhaar_doc", aadhaarUri)
        val panPart = createFilePart("pan_doc", panUri)
        val bankPart = createFilePart("bank_doc", bankUri)

        val response = landApi.createBooking(
            requestId = requestIdPart,
            buyerName = buyerNamePart,
            buyerMobile = buyerMobilePart,
            aadhaarDoc = aadhaarPart,
            panDoc = panPart,
            bankDoc = bankPart
        )

        return Booking(
            bookingId = response.bookingId,
            status = response.status,
            paymentStatus = response.paymentStatus,
            paymentMethod = null,
            transactionId = null,
            dealAmount = response.dealAmount,
            bookingFeeAmount = response.bookingFeeAmount,
            paidAt = null
        )
    }

    override suspend fun payBooking(bookingId: Int, method: String, transactionId: String?): Booking {
        val response = landApi.payBooking(
            bookingId = bookingId,
            body = BookingPaymentRequestDto(method = method, transactionId = transactionId)
        )
        return Booking(
            bookingId = response.bookingId,
            status = response.status,
            paymentStatus = response.paymentStatus,
            paymentMethod = response.paymentMethod,
            transactionId = response.transactionId,
            dealAmount = null,
            bookingFeeAmount = null,
            paidAt = response.paidAt
        )
    }

    override suspend fun createPaymentOrder(amount: Double, userId: Int, propertyId: Int): PaymentOrder {
        val response = landApi.createOrder(
            CreateOrderRequestDto(
                amount = amount,
                user_id = userId,
                property_id = propertyId
            )
        )
        return PaymentOrder(
            orderId = response.order_id,
            amount = response.amount,
            currency = response.currency
        )
    }

    override suspend fun verifyPayment(orderId: String, paymentId: String, signature: String): PaymentVerification {
        val response = landApi.verifyPayment(
            VerifyPaymentRequestDto(
                razorpay_order_id = orderId,
                razorpay_payment_id = paymentId,
                razorpay_signature = signature
            )
        )
        return PaymentVerification(
            status = response.status,
            orderId = response.order_id,
            paymentId = response.payment_id
        )
    }

    private fun createFilePart(fieldName: String, uri: Uri): MultipartBody.Part {
        val fileName = getFileName(uri) ?: "document.pdf"
        val mime = contentResolver.getType(uri) ?: "application/pdf"
        val file = copyToTempFile(uri, fileName)
        val body = file.asRequestBody(mime.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(fieldName, fileName, body)
    }

    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx >= 0) name = it.getString(idx)
            }
        }
        return name
    }

    private fun copyToTempFile(uri: Uri, fileName: String): File {
        val temp = File.createTempFile("upload_", "_$fileName")
        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(temp).use { output ->
                input.copyTo(output)
            }
        }
        return temp
    }

    private fun mapToLand(dto: ExploreLandItemDto): Land {
        return Land(
            id = dto.id,
            title = dto.title.orEmpty(),
            price = dto.expectedPrice ?: 0.0,
            location = dto.location.orEmpty(),
            city = dto.city,
            state = dto.state,
            pincode = dto.pincode,
            area = dto.area ?: 0.0,
            areaUnit = dto.areaUnit,
            images = dto.images.map { raw ->
                val normalized = raw.replace("\\", "/")
                ApiClient.absoluteUrl(normalized) ?: normalized
            },
            boundaryPoints = dto.boundaryPoints.map { it.lat to it.lng },
            isVerified = dto.listingStatus.equals("live", ignoreCase = true),
            isFeatured = false,
            isLiked = dto.isLiked,
            isOfferLocked = dto.isOfferLocked,
            offerLockExpiresAt = dto.offerLockExpiresAt,
            createdAt = dto.createdAt
        )
    }

    private fun mapToLandDetail(dto: com.example.myapplication.ui.feature.buyer.data.remote.dto.ExploreLandDetailDto): LandDetail {
        val sellerUserId = dto.userId ?: dto.sellerId ?: dto.ownerId
        return LandDetail(
            id = dto.id,
            sellerUserId = sellerUserId,
            title = dto.title.orEmpty(),
            ownerCategory = dto.ownerCategory,
            landType = dto.landType,
            listingStatus = dto.listingStatus,
            statusLabel = dto.statusLabel,
            expectedPrice = dto.expectedPrice ?: 0.0,
            area = dto.area ?: 0.0,
            areaUnit = dto.areaUnit,
            location = dto.location.orEmpty(),
            city = dto.city,
            state = dto.state,
            pincode = dto.pincode,
            images = dto.images.map { raw ->
                val normalized = raw.replace("\\", "/")
                ApiClient.absoluteUrl(normalized) ?: normalized
            },
            boundaryPoints = dto.boundaryPoints.map { it.lat to it.lng },
            isOfferLocked = dto.isOfferLocked,
            offerLockExpiresAt = dto.offerLockExpiresAt,
            mapQuery = dto.mapQuery,
            mapUrl = dto.mapUrl,
            documents = dto.documents.map { doc ->
                val normalized = ApiClient.absoluteUrl(doc.fileUrl) ?: doc.fileUrl
                if (normalized != doc.fileUrl) {
                    Log.d(tag, "Normalized document URL: ${doc.fileUrl} -> $normalized")
                }
                doc.copy(fileUrl = normalized)
            },
            dealAmount = dto.dealAmount,
            bookingStatus = dto.bookingStatus?.trim()?.lowercase(),
            registryDate = dto.registryDate,
            registryTime = dto.registryTime,
            registryLocation = dto.registryLocation,
            registryProofs = dto.registryProofs.map { proof ->
                val normalized = ApiClient.absoluteUrl(proof.fileUrl) ?: proof.fileUrl
                com.example.myapplication.ui.feature.buyer.land_detail.RegistryProof(
                    id = proof.id,
                    fileUrl = normalized,
                    fileName = proof.fileName,
                    createdAt = proof.createdAt
                )
            }
        )
    }
}
