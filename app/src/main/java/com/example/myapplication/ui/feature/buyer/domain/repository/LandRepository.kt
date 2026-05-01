package com.example.myapplication.ui.feature.buyer.domain.repository

import android.net.Uri
import com.example.myapplication.ui.feature.buyer.domain.model.BuyerOffer
import com.example.myapplication.ui.feature.buyer.domain.model.BuyerOfferActionResult
import com.example.myapplication.ui.feature.buyer.domain.model.BuyerOfferCreateResult
import com.example.myapplication.ui.feature.buyer.domain.model.Booking
import com.example.myapplication.ui.feature.buyer.explore.Land
import com.example.myapplication.ui.feature.buyer.land_detail.LandDetail
import com.example.myapplication.ui.feature.buyer.domain.model.PaymentOrder
import com.example.myapplication.ui.feature.buyer.domain.model.PaymentVerification

interface LandRepository {

    suspend fun searchLands(
        search: String? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null,
        minArea: Int? = null,
        maxArea: Int? = null,
        sort: String = "latest",
        skip: Int = 0,
        limit: Int = 20
    ): List<Land>

    suspend fun getNearbyLands(
        skip: Int = 0,
        limit: Int = 10,
        sort: String = "latest"
    ): List<Land>

    suspend fun getLandDetail(requestId: Int): LandDetail
    suspend fun getBuyerLandDetail(requestId: Int): LandDetail

    suspend fun makeOffer(requestId: Int, amount: Double): BuyerOfferCreateResult

    suspend fun getBuyerOffers(): List<BuyerOffer>

    suspend fun acceptCounterOffer(offerId: Int): BuyerOfferActionResult

    suspend fun rejectCounterOffer(offerId: Int): BuyerOfferActionResult

    // NEW (PDF upload)
    suspend fun createBookingWithDocs(
        requestId: Int,
        buyerName: String,
        buyerMobile: String,
        aadhaarUri: Uri,
        panUri: Uri,
        bankUri: Uri
    ): Booking

    suspend fun payBooking(bookingId: Int, method: String, transactionId: String?): Booking

    suspend fun createPaymentOrder(amount: Double, userId: Int, propertyId: Int): PaymentOrder

    suspend fun verifyPayment(orderId: String, paymentId: String, signature: String): PaymentVerification
}
