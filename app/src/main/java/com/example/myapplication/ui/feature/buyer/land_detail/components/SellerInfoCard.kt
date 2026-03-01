package com.example.myapplication.ui.feature.buyer.land_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SellerInfoCard(
    sellerName: String,
    isVerified: Boolean
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {

        Column(Modifier.padding(16.dp)) {

            Text(
                text = sellerName,
                style = MaterialTheme.typography.titleMedium
            )

            if (isVerified) {
                Text(
                    text = "Verified Seller",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedButton(onClick = { }) {
                    Text("Chat")
                }

                Button(onClick = { }) {
                    Text("Call")
                }
            }
        }
    }
}