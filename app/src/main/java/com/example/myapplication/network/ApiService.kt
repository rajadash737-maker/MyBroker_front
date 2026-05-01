package com.example.myapplication.network

import com.example.myapplication.ui.feature.sell_land.model.BoundaryRequest
import com.example.myapplication.ui.feature.sell_land.model.CancelListingRequest
import com.example.myapplication.ui.feature.sell_land.model.CancelListingResponse
import com.example.myapplication.ui.feature.sell_land.model.CounterOfferRequest
import com.example.myapplication.ui.feature.sell_land.model.DeleteMediaResponse
import com.example.myapplication.ui.feature.sell_land.model.DocumentRequirementsResponse
import com.example.myapplication.ui.feature.sell_land.model.PaymentInitRequest
import com.example.myapplication.ui.feature.sell_land.model.PaymentMarkPaidRequest
import com.example.myapplication.ui.feature.sell_land.model.PaymentResponse
import com.example.myapplication.ui.feature.sell_land.model.SellLandRazorpayOrderRequest
import com.example.myapplication.ui.feature.sell_land.model.SellLandRazorpayOrderResponse
import com.example.myapplication.ui.feature.sell_land.model.SellLandRazorpayVerifyRequest
import com.example.myapplication.ui.feature.sell_land.model.SellLandRazorpayVerifyResponse
import com.example.myapplication.ui.feature.sell_land.model.SellLandDraftRequest
import com.example.myapplication.ui.feature.sell_land.model.SellLandMediaResponse
import com.example.myapplication.ui.feature.sell_land.model.SellLandResponse
import com.example.myapplication.ui.feature.sell_land.model.SellerOfferActionResponse
import com.example.myapplication.ui.feature.sell_land.model.SellerOfferListResponse
import com.example.myapplication.ui.feature.sell_land.model.SubmitResponse
import com.example.myapplication.ui.feature.ratings.RatingCreateRequest
import com.example.myapplication.ui.feature.ratings.RatingResponse
import com.example.myapplication.ui.feature.ratings.RatingSummaryResponse
import com.example.myapplication.ui.feature.ratings.RatingListItemResponse
import com.example.myapplication.ui.feature.transactions.data.remote.TransactionsResponse
import com.example.myapplication.notification.NotificationListResponseDto
import com.example.myapplication.notification.NotificationReadRequestDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("auth/send-otp")
    suspend fun sendOtp(@Body body: SendOtpRequest): Response<StatusResponse>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body body: VerifyOtpRequest): Response<StatusResponse>

    @POST("auth/firebase/verify")
    suspend fun verifyFirebaseToken(@Body body: FirebaseTokenRequest): Response<StatusResponse>

    @POST("auth/signup")
    suspend fun signup(@Body body: SignupRequest): Response<StatusResponse>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @POST("auth/refresh")
    suspend fun refresh(@Body body: RefreshRequest): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(@Body body: LogoutRequest): Response<StatusResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body body: ResetPasswordRequest): Response<StatusResponse>

    @GET("users/me/profile")
    suspend fun getMyProfile(): Response<UserProfileResponse>

    @PUT("users/me/profile")
    suspend fun updateMyProfile(@Body body: UpdateProfileRequest): Response<UserProfileResponse>

    @GET("settings/social-links")
    suspend fun getSocialLinks(): Response<SocialLinksResponse>

    @GET("settings/banners")
    suspend fun getBanners(): Response<List<BannerResponse>>

    @Multipart
    @POST("users/me/profile-photo")
    suspend fun uploadProfilePhoto(
        @Part file: MultipartBody.Part
    ): Response<ProfilePhotoResponse>

    @POST("users/me/fcm-token")
    suspend fun registerFcmToken(
        @Body body: FcmTokenRequest
    ): Response<StatusResponse>

    @POST("sell-land/draft")
    suspend fun saveDraft(
        @Query("request_id") requestId: Int? = null,
        @Body body: SellLandDraftRequest
    ): Response<SellLandResponse>

    @PUT("sell-land/{id}/boundary")
    suspend fun updateBoundary(
        @Path("id") id: Int,
        @Body body: BoundaryRequest
    ): Response<SellLandResponse>

    @Multipart
    @POST("sell-land/{id}/photos")
    suspend fun uploadPhoto(
        @Path("id") id: Int,
        @Part file: MultipartBody.Part
    ): Response<SellLandMediaResponse>

    @Multipart
    @POST("sell-land/{id}/documents")
    suspend fun uploadDocument(
        @Path("id") id: Int,
        @Part("document_type") documentType: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<SellLandMediaResponse>

    @GET("sell-land/{id}/media")
    suspend fun getListingMedia(
        @Path("id") id: Int
    ): Response<List<SellLandMediaResponse>>

    @DELETE("sell-land/{id}/media/{media_id}")
    suspend fun deleteListingMedia(
        @Path("id") id: Int,
        @Path("media_id") mediaId: Int
    ): Response<DeleteMediaResponse>

    @POST("sell-land/payments/initiate")
    suspend fun initiatePayment(@Body body: PaymentInitRequest): Response<PaymentResponse>

    @POST("sell-land/payments/mark-paid")
    suspend fun markPaid(@Body body: PaymentMarkPaidRequest): Response<PaymentResponse>

    @POST("sell-land/payments/razorpay-order")
    suspend fun createSellLandRazorpayOrder(
        @Body body: SellLandRazorpayOrderRequest
    ): Response<SellLandRazorpayOrderResponse>

    @POST("sell-land/payments/razorpay-verify")
    suspend fun verifySellLandRazorpayPayment(
        @Body body: SellLandRazorpayVerifyRequest
    ): Response<SellLandRazorpayVerifyResponse>

    @POST("sell-land/{id}/submit")
    suspend fun submit(@Path("id") id: Int): Response<SubmitResponse>

    @GET("sell-land/me")
    suspend fun getMyListings(): Response<List<SellLandResponse>>

    @GET("sell-land/me/tracking")
    suspend fun getMyListingsTracking(): Response<List<SellLandResponse>>

    @GET("sell-land/{id}")
    suspend fun getListingDetails(@Path("id") id: Int): Response<SellLandResponse>

    @POST("sell-land/{id}/cancel")
    suspend fun cancelListing(
        @Path("id") id: Int,
        @Body body: CancelListingRequest
    ): Response<CancelListingResponse>

    @GET("sell-land/{id}/offers")
    suspend fun getListingOffers(@Path("id") id: Int): Response<SellerOfferListResponse>

    @POST("sell-land/{id}/offers/{offer_id}/accept")
    suspend fun acceptOffer(
        @Path("id") id: Int,
        @Path("offer_id") offerId: Int
    ): Response<SellerOfferActionResponse>

    @POST("sell-land/{id}/offers/{offer_id}/reject")
    suspend fun rejectOffer(
        @Path("id") id: Int,
        @Path("offer_id") offerId: Int
    ): Response<SellerOfferActionResponse>

    @POST("sell-land/{id}/offers/{offer_id}/counter")
    suspend fun counterOffer(
        @Path("id") id: Int,
        @Path("offer_id") offerId: Int,
        @Body body: CounterOfferRequest
    ): Response<SellerOfferActionResponse>

    @GET("sell-land/document-requirements")
    suspend fun getDocumentRequirements(
        @Query("ownership_type") ownershipType: String? = null
    ): Response<List<DocumentRequirementsResponse>>

    @POST("ratings")
    suspend fun submitRating(
        @Body body: RatingCreateRequest
    ): Response<RatingResponse>

    @GET("ratings/me")
    suspend fun getMyRating(
        @Query("property_id") propertyId: Int,
        @Query("role") role: String
    ): Response<RatingResponse>

    @GET("ratings/me/summary")
    suspend fun getMyRatingSummary(): Response<RatingSummaryResponse>

    @GET("ratings/me/list")
    suspend fun getMyRatingsList(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 50
    ): Response<List<RatingListItemResponse>>

    @GET("ratings/admin/list")
    suspend fun getAdminRatingsList(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 50,
        @Query("property_id") propertyId: Int? = null,
        @Query("role") role: String? = null,
        @Query("min_rating") minRating: Int? = null
    ): Response<List<RatingListItemResponse>>

    @GET("transactions/me")
    suspend fun getMyTransactions(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 50
    ): Response<TransactionsResponse>

    @GET("transactions/{transactionId}")
    suspend fun getTransactionDetail(
        @Path("transactionId") transactionId: String
    ): Response<com.example.myapplication.ui.feature.transactions.data.remote.TransactionDto>

    @GET("notifications")
    suspend fun listNotifications(): Response<NotificationListResponseDto>

    @POST("notifications/read")
    suspend fun markNotificationsRead(
        @Body body: NotificationReadRequestDto
    ): Response<NotificationListResponseDto>
}
