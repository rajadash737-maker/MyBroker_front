package com.example.myapplication.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {

    private val uiFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    private val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val readableDateFormatter = DateTimeFormatter.ofPattern("dd - MMM - yyyy", Locale.ENGLISH)
    private val readableTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
    private val inputDateTimePatterns = listOf(
        DateTimeFormatter.ISO_OFFSET_DATE_TIME,
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ENGLISH)
    )
    private val inputDatePatterns = listOf(
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH)
    )
    private val inputTimePatterns = listOf(
        DateTimeFormatter.ISO_LOCAL_TIME,
        DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("hh.mm a", Locale.ENGLISH)
    )

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

    fun formatReadableDate(rawDate: String?): String {
        val value = rawDate?.trim().orEmpty()
        if (value.isBlank()) return ""

        inputDateTimePatterns.forEach { formatter ->
            try {
                return LocalDateTime.parse(value, formatter).toLocalDate().format(readableDateFormatter)
            } catch (_: Exception) {
            }
            try {
                return OffsetDateTime.parse(value, formatter).toLocalDate().format(readableDateFormatter)
            } catch (_: Exception) {
            }
        }

        inputDatePatterns.forEach { formatter ->
            try {
                return LocalDate.parse(value.take(10), formatter).format(readableDateFormatter)
            } catch (_: Exception) {
            }
        }

        return rawDate.orEmpty()
    }

    fun formatReadableTime(rawTime: String?): String {
        val value = rawTime?.trim().orEmpty()
        if (value.isBlank()) return ""

        inputDateTimePatterns.forEach { formatter ->
            try {
                return LocalDateTime.parse(value, formatter).toLocalTime().format(readableTimeFormatter)
            } catch (_: Exception) {
            }
            try {
                return OffsetDateTime.parse(value, formatter).toLocalTime().format(readableTimeFormatter)
            } catch (_: Exception) {
            }
        }

        inputTimePatterns.forEach { formatter ->
            try {
                return LocalTime.parse(value.uppercase(Locale.ENGLISH), formatter).format(readableTimeFormatter)
            } catch (_: Exception) {
            }
        }

        return rawTime.orEmpty()
    }
}
