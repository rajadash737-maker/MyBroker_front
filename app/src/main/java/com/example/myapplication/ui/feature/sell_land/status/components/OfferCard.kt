package com.example.myapplication.ui.feature.sell_land.status.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.sell_land.model.OfferModel
import com.example.myapplication.ui.feature.sell_land.status.OfferStatus
import java.text.NumberFormat
import java.util.Locale

@Composable
fun OfferCard(
    offer: OfferModel,
    onAccept: () -> Unit = {},
    onReject: () -> Unit = {},
    onCounter: (Double) -> Unit = {}
) {
    var counterAmount by remember { mutableStateOf("") }
    var showCounterField by remember { mutableStateOf(false) }

    val formatter = remember {
        NumberFormat.getNumberInstance(Locale.Builder().setLanguage("en").setRegion("IN").build())
    }

    val formattedOfferAmount = remember(offer.amount) { formatter.format(offer.amount) }
    val formattedCounterAmount = remember(offer.counterAmount) {
        offer.counterAmount?.let { formatter.format(it) }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = offer.buyerName,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Offered ₹ $formattedOfferAmount",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (formattedCounterAmount != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Counter ₹ $formattedCounterAmount",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFFFA000)
                        )
                    }
                }

                OfferStatusBadge(status = offer.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (offer.status == OfferStatus.PENDING || offer.status == OfferStatus.COUNTERED) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reject")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Accept")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { showCounterField = !showCounterField }
                ) {
                    Text("Counter Offer")
                }

                if (showCounterField) {
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = counterAmount,
                        onValueChange = { counterAmount = it },
                        label = { Text("Enter Counter Amount") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            counterAmount.toDoubleOrNull()?.let { value ->
                                onCounter(value)
                                counterAmount = ""
                                showCounterField = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send Counter")
                    }
                }
            }
        }
    }
}

@Composable
fun OfferStatusBadge(status: OfferStatus) {
    val color = when (status) {
        OfferStatus.PENDING -> Color.Gray
        OfferStatus.ACCEPTED -> Color(0xFF2E7D32)
        OfferStatus.REJECTED -> Color.Red
        OfferStatus.COUNTERED -> Color(0xFFFFA000)
        OfferStatus.EXPIRED -> Color(0xFF8D6E63)
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = color,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
