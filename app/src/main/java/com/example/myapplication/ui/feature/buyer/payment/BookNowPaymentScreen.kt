package com.example.myapplication.ui.feature.buyer.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BookNowPaymentScreen(
    state: BookNowState,
    formatAmount: (Double?) -> String,
    onPayNow: () -> Unit,
    onBack: () -> Unit
) {
    val feeAmount = state.bookingFeeAmount ?: 1000.0

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Payment", style = MaterialTheme.typography.titleLarge)

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F2FB))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Booking Fee", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(formatAmount(feeAmount), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                Text("Deal Amount: ${formatAmount(state.dealAmount)}", style = MaterialTheme.typography.bodySmall)
            }
        }

        Text("Pay securely with Razorpay", style = MaterialTheme.typography.titleSmall)

        Spacer(modifier = Modifier.height(6.dp))

        Button(
            onClick = onPayNow,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isPaymentLoading
        ) {
            if (state.isPaymentLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.height(18.dp)
                )
            } else {
                Text("Pay ${formatAmount(feeAmount)}")
            }
        }

        if (!state.paymentError.isNullOrBlank()) {
            Text(
                text = state.paymentError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Button(
                onClick = onPayNow,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isPaymentLoading
            ) {
                Text("Retry Payment")
            }
        }

        TextButton(onClick = onBack) { Text("Back") }
    }
}
