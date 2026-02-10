package com.example.myapplication.utils



import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val uiFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    private val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun apiToUi(apiDate: String?): String {
        return try {
            if (apiDate.isNullOrBlank()) ""
            else uiFormat.format(apiFormat.parse(apiDate)!!)
        } catch (e: Exception) {
            ""
        }
    }

    fun uiToApi(uiDate: String): String {
        return try {
            apiFormat.format(uiFormat.parse(uiDate)!!)
        } catch (e: Exception) {
            ""
        }
    }
}
