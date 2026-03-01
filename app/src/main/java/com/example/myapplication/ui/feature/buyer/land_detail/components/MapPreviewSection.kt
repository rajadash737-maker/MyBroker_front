package com.example.myapplication.ui.feature.buyer.land_detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.buyer.land_detail.LandDetail

@Composable
fun MapPreviewSection(
    land: LandDetail,
    onOpenMapClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Location",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            val place = listOfNotNull(land.location, land.city, land.state, land.pincode)
                .joinToString(", ")
            Text("📍 $place")

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onOpenMapClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View on Map")
            }
        }
    }
}
