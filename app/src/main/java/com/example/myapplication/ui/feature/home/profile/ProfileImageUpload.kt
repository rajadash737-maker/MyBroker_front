package com.example.myapplication.ui.feature.home.profile

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.example.myapplication.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

object UploadImageToServer {

    suspend fun uploadProfileImage(
        context: Context,
        uri: Uri,
        onSuccess: (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        var tempFile: File? = null

        try {
            val input = context.contentResolver.openInputStream(uri)
                ?: throw IllegalStateException("Cannot read image")
            tempFile = File.createTempFile("profile_", ".jpg", context.cacheDir)

            input.use { ins ->
                tempFile.outputStream().use { out -> ins.copyTo(out) }
            }

            val reqFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", tempFile.name, reqFile)

            val response = ApiClient.apiService.uploadProfilePhoto(filePart)

            if (response.isSuccessful) {
                val body = response.body() ?: throw IllegalStateException("Empty response")
                val absolute = ApiClient.absoluteUrl(body.profileImageUrl) ?: body.profileImageUrl
                withContext(Dispatchers.Main) { onSuccess(absolute) }
            } else {
                throw IllegalStateException("Upload failed: ${response.code()}")
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message ?: "Upload failed!", Toast.LENGTH_SHORT).show()
            }
        } finally {
            tempFile?.delete()
        }
    }

}
