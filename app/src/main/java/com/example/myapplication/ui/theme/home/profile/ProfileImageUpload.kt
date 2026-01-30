package com.example.myapplication.ui.theme.home.profile

//package com.example.myapplication.ui.theme.home.profile

import android.content.Context
import android.net.Uri
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

import com.example.myapplication.network.ApiClient
import com.example.myapplication.utils.FileUtils


object UploadImageToServer {

    suspend fun uploadProfileImage(
        context: Context,
        uri: Uri,
        userId: String,
        onSuccess: (String) -> Unit
    ) = withContext(Dispatchers.IO) {

        try {
            // Convert Uri -> File
            val file = FileUtils.getFile(context, uri)

            // Convert File → RequestBody
            val reqFile = file.asRequestBody("image/*".toMediaType())

            // Create Multipart Part
            val filePart = MultipartBody.Part.createFormData(
                name = "image",
                filename = file.name,
                body = reqFile
            )

            // User ID body
            val userIdBody = userId.toRequestBody("text/plain".toMediaType())

            // API Call
            val response = ApiClient.apiService.uploadProfileImage(
                userIdBody,
                filePart
            )

            if (response.success) {
                withContext(Dispatchers.Main) {
                    onSuccess(response.file ?: "")
                }
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message ?: "Upload failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
