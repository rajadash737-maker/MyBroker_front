package com.example.myapplication.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object FileUtils {

    fun getFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)

        val out = FileOutputStream(tempFile)
        inputStream.copyTo(out)

        out.close()
        inputStream.close()

        return tempFile
    }
}
