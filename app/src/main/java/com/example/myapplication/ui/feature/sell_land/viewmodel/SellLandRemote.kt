// SellLandRemote.kt
package com.example.myapplication.ui.feature.sell_land.viewmodel

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import com.example.myapplication.network.ApiClient
import com.example.myapplication.ui.feature.sell_land.model.BoundaryPoint
import com.example.myapplication.ui.feature.sell_land.model.BoundaryRequest
import com.example.myapplication.ui.feature.sell_land.model.PaymentInitRequest
import com.example.myapplication.ui.feature.sell_land.model.PaymentMarkPaidRequest
import com.example.myapplication.ui.feature.sell_land.model.PaymentResponse
import com.example.myapplication.ui.feature.sell_land.model.SellLandMediaResponse
import com.example.myapplication.ui.feature.sell_land.model.SellLandResponse
import com.example.myapplication.ui.feature.sell_land.model.SubmitResponse
import com.google.android.gms.maps.model.LatLng
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class SellLandRemote {

    suspend fun saveBoundary(requestId: Int, points: List<LatLng>): Result<Unit> = runCatching {
        val body = BoundaryRequest(points = points.map { BoundaryPoint(lat = it.latitude, lng = it.longitude) })
        val res = ApiClient.apiService.updateBoundary(requestId, body)
        if (!res.isSuccessful) throw Exception(res.errorBody()?.string() ?: "Boundary update failed")
    }

    suspend fun uploadPhoto(requestId: Int, file: File): Result<SellLandMediaResponse> = runCatching {
        val req = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", file.name, req)
        val res = ApiClient.apiService.uploadPhoto(requestId, part)
        if (!res.isSuccessful || res.body() == null) {
            throw Exception(res.errorBody()?.string() ?: "Photo upload failed")
        }
        res.body()!!
    }

    suspend fun uploadDocument(requestId: Int, documentType: String, file: File): Result<SellLandMediaResponse> = runCatching {
        val fileReq = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, fileReq)
        val docTypeBody: RequestBody = documentType.toRequestBody("text/plain".toMediaTypeOrNull())

        val res = ApiClient.apiService.uploadDocument(requestId, docTypeBody, filePart)
        if (!res.isSuccessful || res.body() == null) {
            throw Exception(res.errorBody()?.string() ?: "Document upload failed")
        }
        res.body()!!
    }

    suspend fun initiatePayment(requestId: Int, method: String): Result<PaymentResponse> = runCatching {
        val res = ApiClient.apiService.initiatePayment(PaymentInitRequest(requestId = requestId, method = method))
        if (!res.isSuccessful || res.body() == null) {
            throw Exception(res.errorBody()?.string() ?: "Payment initiation failed")
        }
        res.body()!!
    }

    suspend fun markPaid(paymentId: Int, providerRef: String? = null): Result<PaymentResponse> = runCatching {
        val res = ApiClient.apiService.markPaid(PaymentMarkPaidRequest(paymentId = paymentId, providerRef = providerRef))
        if (!res.isSuccessful || res.body() == null) {
            throw Exception(res.errorBody()?.string() ?: "Mark paid failed")
        }
        res.body()!!
    }

    suspend fun submit(requestId: Int): Result<SubmitResponse> = runCatching {
        val res = ApiClient.apiService.submit(requestId)
        if (!res.isSuccessful || res.body() == null) {
            throw Exception(res.errorBody()?.string() ?: "Submit failed")
        }
        res.body()!!
    }

    fun uriToTempFile(uri: Uri, contentResolver: ContentResolver, cacheDir: File): File {
        val originalName = queryName(contentResolver, uri)
        val mime = contentResolver.getType(uri)
        val ext = when {
            originalName?.contains(".") == true -> originalName.substringAfterLast(".", "")
            mime == "application/pdf" -> "pdf"
            mime == "image/jpeg" -> "jpg"
            mime == "image/png" -> "png"
            else -> "pdf"
        }

        val safeBase = originalName?.substringBeforeLast(".")?.ifBlank { "upload_${System.currentTimeMillis()}" }
            ?: "upload_${System.currentTimeMillis()}"

        val temp = File(cacheDir, "$safeBase.$ext")
        contentResolver.openInputStream(uri).use { input ->
            temp.outputStream().use { output ->
                input?.copyTo(output) ?: throw IllegalStateException("Cannot read file")
            }
        }
        return temp
    }

    private fun queryName(contentResolver: ContentResolver, uri: Uri): String? {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0 && cursor.moveToFirst()) return cursor.getString(index)
        }
        return null
    }

    suspend fun getListingMedia(requestId: Int): Result<List<SellLandMediaResponse>> = runCatching {
        val res = ApiClient.apiService.getListingMedia(requestId)
        if (!res.isSuccessful || res.body() == null) {
            throw Exception(res.errorBody()?.string() ?: "Get media failed")
        }
        res.body()!!
    }

    suspend fun deleteListingMedia(requestId: Int, mediaId: Int): Result<Unit> = runCatching {
        val res = ApiClient.apiService.deleteListingMedia(requestId, mediaId)
        if (!res.isSuccessful) {
            throw Exception(res.errorBody()?.string() ?: "Delete media failed")
        }
    }



    suspend fun getMyListingsTracking(): Result<List<SellLandResponse>> = runCatching {
        val res = ApiClient.apiService.getMyListingsTracking()
        if (!res.isSuccessful || res.body() == null) {
            throw Exception(res.errorBody()?.string() ?: "Get tracking listings failed")
        }
        res.body()!!
    }

    suspend fun getListingDetails(requestId: Int): Result<SellLandResponse> = runCatching {
        val res = ApiClient.apiService.getListingDetails(requestId)
        if (!res.isSuccessful || res.body() == null) {
            throw Exception(res.errorBody()?.string() ?: "Get listing details failed")
        }
        res.body()!!
    }

}
