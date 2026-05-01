package com.example.myapplication.ui.feature.buyer.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BookNowSuccessScreen(
    requestId: Int,
    bookingId: Int,
    amount: Double,
    onViewDetails: () -> Unit,
    onContactSeller: () -> Unit,
    onGoHome: () -> Unit
) {
    val bookingCode = if (bookingId > 0) "MB$bookingId" else "MB----"
    val formatter = NumberFormat.getNumberInstance(Locale.Builder().setLanguage("en").setRegion("IN").build())
    val amountText = "₹ ${formatter.format(amount)}"

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 2.dp)
                        .height(36.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Payment Successful",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Text(
                    text = amountText,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1B5E20)
                )

                Text(
                    text = "Booking ID: $bookingCode",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Your booking is confirmed.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = onViewDetails, modifier = Modifier.weight(1f)) {
                Text("View Details")
            }
            OutlinedButton(onClick = onContactSeller, modifier = Modifier.weight(1f)) {
                Text("Contact Seller")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onGoHome, modifier = Modifier.fillMaxWidth()) {
            Text("Go Home")
        }
    }
}
