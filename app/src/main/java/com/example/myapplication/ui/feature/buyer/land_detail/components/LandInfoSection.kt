package com.example.myapplication.ui.feature.buyer.land_detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.buyer.land_detail.LandDetail
import java.text.DecimalFormat
import java.util.Locale

@Composable
fun LandInfoSection(land: LandDetail) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "₹${formatPrice(land.expectedPrice)}",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text("Title: ${land.title}")
        Text("Land Type: ${land.landType.replace('_', ' ').replaceFirstChar { it.uppercase() }}")
        Text("Status: ${statusText(land.statusLabel)}")
        Text("Area: ${land.area} ${land.areaUnit}")

        val district = land.city ?: "-"
        val state = land.state ?: "-"
        val pincode = land.pincode ?: "-"
        Text("District: $district")
        Text("State: $state")
        Text("Pincode: $pincode")

        Spacer(modifier = Modifier.height(8.dp))
    }
}

private fun statusText(raw: String): String {
    val normalized = raw.trim().lowercase(Locale.ROOT)
    return when {
        normalized == "available" -> "Available"
        normalized == "temp_lock" || normalized == "locked" || normalized == "lock" -> "Lock"
        normalized == "booked" -> "Booked"
        normalized == "sold" -> "Sold"
        else -> raw.replace('_', ' ').replaceFirstChar { it.uppercase() }
    }
}

private fun formatPrice(value: Double): String {
    return DecimalFormat("#,##0.##").format(value)
}
