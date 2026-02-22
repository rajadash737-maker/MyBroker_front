package com.example.myapplication.ui.feature.sell_land.status.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import com.example.myapplication.ui.feature.sell_land.model.OfferModel
import com.example.myapplication.ui.feature.sell_land.status.OfferStatus

@Composable
fun OfferCard(
    offer: OfferModel,
    onAccept: () -> Unit = {},
    onReject: () -> Unit = {},
    onCounter: (Double) -> Unit = {}
) {

    var counterAmount by remember { mutableStateOf("") }
    var showCounterField by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            // Buyer + Status
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
                        text = "Offered ₹ ${offer.amount}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                OfferStatusBadge(status = offer.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (offer.status == OfferStatus.PENDING) {

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
                            counterAmount.toDoubleOrNull()?.let {
                                onCounter(it)
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
