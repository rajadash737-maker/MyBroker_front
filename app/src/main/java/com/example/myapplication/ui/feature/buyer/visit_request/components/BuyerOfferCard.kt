package com.example.myapplication.ui.feature.buyer.visit_request.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.buyer.domain.model.BuyerOffer
import kotlinx.coroutines.delay
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Composable
fun BuyerOfferCard(
    offer: BuyerOffer,
    onAcceptCounter: (Int) -> Unit,
    onRejectCounter: (Int) -> Unit,
    onBookNow: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val timerText = rememberCountdownText(offer.offerLockExpiresAt)
    val statusChipColor = when (offer.bookingStatus.lowercase()) {
        "accepted_book_now" -> Color(0xFF2E7D32)
        "counter_received" -> Color(0xFF6A1B9A)
        "rejected", "expired" -> Color(0xFFB71C1C)
        else -> Color(0xFF455A64)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0EDF6) // white ki jagah attractive soft card tone
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = offer.land.title ?: "Land #${offer.requestId}",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = offer.statusLabel.uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .background(statusChipColor, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Text(
                text = listOfNotNull(offer.land.location, offer.land.city, offer.land.state).joinToString(", "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text("Your Offer: ₹ ${offer.offeredAmount.asAmount()}", style = MaterialTheme.typography.bodyMedium)

            offer.counterAmount?.let {
                Text(
                    text = "Counter Offer: ₹ ${it.asAmount()}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (offer.isBookWindowActive && offer.bookingStatus.equals("accepted_book_now", true)) {
                Text(
                    text = "Booking Timer: ${timerText ?: "Calculating..."}",
                    color = Color(0xFF6A1B9A),
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    onClick = { onBookNow(offer.requestId) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Book Now")
                }
            }

            BuyerOfferTimeline(events = offer.timeline)

            BuyerOfferActionButtons(
                canAcceptCounter = offer.canAcceptCounter,
                canRejectCounter = offer.canRejectCounter,
                onAcceptCounter = { onAcceptCounter(offer.offerId) },
                onRejectCounter = { onRejectCounter(offer.offerId) }
            )

            if (
                !offer.isBookWindowActive &&
                offer.bookingStatus.equals("accepted_book_now", true)
            ) {
                OutlinedButton(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) {
                    Text("Booking Window Expired")
                }
            }
        }
    }
}

@Composable
private fun rememberCountdownText(expiresAt: String?): String? {
    val targetMillis = remember(expiresAt) { parseIsoToMillis(expiresAt) } ?: return null

    val nowMillis by produceState(initialValue = System.currentTimeMillis(), targetMillis) {
        while (true) {
            value = System.currentTimeMillis()
            delay(1000)
        }
    }

    val remaining = targetMillis - nowMillis
    if (remaining <= 0L) return "Expired"

    val h = remaining / (1000 * 60 * 60)
    val m = (remaining / (1000 * 60)) % 60
    val s = (remaining / 1000) % 60
    return String.format("%02d:%02d:%02d", h, m, s)
}

private fun parseIsoToMillis(raw: String?): Long? {
    if (raw.isNullOrBlank()) return null
    return runCatching { OffsetDateTime.parse(raw).toInstant().toEpochMilli() }
        .recoverCatching { LocalDateTime.parse(raw).toInstant(ZoneOffset.UTC).toEpochMilli() }
        .getOrNull()
}

private fun Double.asAmount(): String = DecimalFormat("#,##0.##").format(this)
