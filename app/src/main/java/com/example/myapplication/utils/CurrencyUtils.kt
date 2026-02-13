package com.example.myapplication.utils

object CurrencyUtils {

    fun formatINR(amount: Double): String {
        return "₹%,.0f".format(amount)
    }

    fun parseAmount(value: String): Double {
        return value.replace("₹", "").replace(",", "").toDoubleOrNull() ?: 0.0
    }
}
