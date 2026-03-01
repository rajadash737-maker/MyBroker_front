package com.example.myapplication.ui.feature.buyer.land_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

@Composable
fun BottomActionBar(
    isOfferLocked: Boolean = false,
    offerLockExpiresAt: String? = null,
    onBookClick: () -> Unit,
    onOfferClick: () -> Unit
) {
    Surface(
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            if (isOfferLocked) {
                Text(
                    text = formatLockRemaining(offerLockExpiresAt) ?: "Temporarily locked for another buyer",
                    color = Color(0xFFD32F2F),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onBookClick
                ) {
                    Text("Book Now")
                }

                Button(
                    modifier = Modifier.weight(1f),
                    enabled = !isOfferLocked,
                    onClick = onOfferClick
                ) {
                    Text(if (isOfferLocked) "Locked" else "Make Offer")
                }
            }
        }
    }
}

private fun formatLockRemaining(expiresAt: String?): String? {
    if (expiresAt.isNullOrBlank()) return null
    return try {
        val expiry = OffsetDateTime.parse(expiresAt).toInstant()
        val now = Instant.now()
        val diff = Duration.between(now, expiry)
        if (diff.isNegative || diff.isZero) {
            "Lock expired"
        } else {
            val h = diff.toHours()
            val m = diff.toMinutes() % 60
            "Offer locked: ${h}h ${m}m left"
        }
    } catch (_: DateTimeParseException) {
        "Temporarily locked for another buyer"
    }
}
