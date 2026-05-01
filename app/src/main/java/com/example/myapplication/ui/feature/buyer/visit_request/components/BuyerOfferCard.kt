package com.example.myapplication.ui.feature.buyer.visit_request.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.feature.buyer.domain.model.BuyerOffer
import com.example.myapplication.ui.feature.buyer.domain.model.BuyerOfferLand
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.text.NumberFormat
import java.util.Locale
import java.util.TimeZone
import java.util.regex.Pattern

@Composable
fun BuyerOfferCard(
    offer: BuyerOffer,
    onAcceptCounter: (Int) -> Unit,
    onRejectCounter: (Int) -> Unit,
    onCounterAgain: (Int) -> Unit,
    onBookNow: (Int) -> Unit,
    onViewDetails: (BuyerOffer) -> Unit = {},
    onContactAdmin: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val timerText = rememberCountdownText(offer.offerLockExpiresAt)
    val isBooked = offer.bookingStatus.equals("booked", true)
        || offer.bookingStatus.equals("sold", true)
        || offer.bookingStatus.equals("registry_scheduled", true)
        || offer.bookingStatus.equals("in_process", true)
        || offer.bookingStatus.equals("registry_done", true)
    val bookedAt = remember(offer.timeline) {
        offer.timeline.firstOrNull {
            it.key.equals("booked", true) || it.label.contains("booked", true)
        }?.at
    }
    val bookedAtText = remember(bookedAt) { formatDisplayDate(bookedAt) }
    val dealAmount = if (offer.finalAmount > 0.0) offer.finalAmount else offer.offeredAmount
    val locationLine = remember(offer.land) { formatLocationLine(offer.land) }

    val statusChipColor = when (offer.bookingStatus.lowercase()) {
        "accepted_book_now" -> Color(0xFF2E7D32)
        "counter_received" -> Color(0xFF6A1B9A)
        "booked" -> Color(0xFF1565C0)
        "registry_scheduled" -> Color(0xFF1565C0)
        "in_process" -> Color(0xFF1565C0)
        "registry_done" -> Color(0xFF2E7D32)
        "sold" -> Color(0xFF2E7D32)
        "rejected", "expired" -> Color(0xFFB71C1C)
        else -> Color(0xFF455A64)
    }
    val headerColors = remember(offer.bookingStatus) { statusHeaderColors(offer.bookingStatus) }
    val displayStatus = when (offer.bookingStatus.lowercase()) {
        "sold" -> "SOLD"
        "booked" -> "BOOKED"
        "registry_scheduled" -> "REGISTRY SCHEDULED"
        "in_process" -> "IN PROCESS"
        "registry_done" -> "REGISTRY DONE"
        else -> offer.statusLabel.uppercase()
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = if (isBooked) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isBooked) 2.dp else 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isBooked) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isBooked) {
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
    StatusHeader(
        label = displayStatus,
        background = headerColors.background,
        content = headerColors.content,
        chipBackground = statusChipColor
    )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = offer.land.title ?: "Land #${offer.requestId}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
                Text(
                    text = stringResource(id = R.string.buyer_listing_id, offer.requestId),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = locationLine,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (isBooked) {
                    AmountRow(
                        label = stringResource(id = R.string.buyer_final_price),
                        amount = "₹ ${dealAmount.asAmount()}",
                        highlight = true
                    )

                    bookedAtText?.let {
                        Text(
                            text = stringResource(id = R.string.buyer_booked_on, it),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    AmountRow(
                        label = stringResource(id = R.string.buyer_your_offer),
                        amount = "₹ ${offer.offeredAmount.asAmount()}"
                    )
                    offer.counterAmount?.let {
                        AmountRow(
                            label = stringResource(id = R.string.buyer_counter_offer),
                            amount = "₹ ${it.asAmount()}"
                        )
                    }
                }

                if (offer.isBookWindowActive && offer.bookingStatus.equals("accepted_book_now", true) && !isBooked) {
                    Text(
                        text = stringResource(id = R.string.buyer_booking_timer, timerText ?: stringResource(id = R.string.common_calculating)),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Button(
                        onClick = { onBookNow(offer.requestId) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(id = R.string.buyer_book_now))
                    }
                }

                if (isBooked) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onViewDetails(offer) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(id = R.string.buyer_view_details))
                        }
                        Button(
                            onClick = { onContactAdmin(offer.requestId) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(id = R.string.buyer_contact_admin))
                        }
                    }
                }

                BuyerOfferTimeline(events = offer.timeline)

                if (!isBooked) {
                    BuyerOfferActionButtons(
                        canAcceptCounter = offer.canAcceptCounter,
                        canRejectCounter = offer.canRejectCounter,
                        canCounterAgain = offer.bookingStatus.equals("counter_received", true),
                        onAcceptCounter = { onAcceptCounter(offer.offerId) },
                        onRejectCounter = { onRejectCounter(offer.offerId) },
                        onCounterAgain = { onCounterAgain(offer.requestId) }
                    )
                }

                if (!offer.isBookWindowActive && offer.bookingStatus.equals("accepted_book_now", true)) {
                    OutlinedButton(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(id = R.string.buyer_booking_window_expired))
                    }
                }
            }
        }
    }
}

private data class StatusHeaderColors(
    val background: Color,
    val content: Color
)

private fun statusHeaderColors(status: String): StatusHeaderColors {
    return when (status.trim().lowercase()) {
        "booked" -> StatusHeaderColors(Color(0xFFE3F2FD), Color(0xFF0D47A1))
        "registry_scheduled" -> StatusHeaderColors(Color(0xFFE3F2FD), Color(0xFF0D47A1))
        "in_process" -> StatusHeaderColors(Color(0xFFE3F2FD), Color(0xFF0D47A1))
        "registry_done" -> StatusHeaderColors(Color(0xFFE8F5E9), Color(0xFF1B5E20))
        "sold" -> StatusHeaderColors(Color(0xFFE8F5E9), Color(0xFF1B5E20))
        "accepted_book_now" -> StatusHeaderColors(Color(0xFFE8F5E9), Color(0xFF1B5E20))
        "counter_received" -> StatusHeaderColors(Color(0xFFF3E5F5), Color(0xFF4A148C))
        "rejected", "expired" -> StatusHeaderColors(Color(0xFFFFEBEE), Color(0xFFB71C1C))
        else -> StatusHeaderColors(Color(0xFFECEFF1), Color(0xFF37474F))
    }
}

@Composable
private fun StatusHeader(
    label: String,
    background: Color,
    content: Color,
    chipBackground: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(16.dp)
                    .background(content, RoundedCornerShape(4.dp))
            )
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = content
            )
        }
        Text(
            text = label.uppercase(),
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .background(chipBackground, RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun AmountRow(
    label: String,
    amount: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (highlight) FontWeight.SemiBold else FontWeight.Medium
            )
        )
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
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
}

private fun parseIsoToMillis(raw: String?): Long? {
    if (raw.isNullOrBlank()) return null

    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ss"
    )

    patterns.forEach { pattern ->
        runCatching {
            val sdf = SimpleDateFormat(pattern, Locale.US)
            if (pattern == "yyyy-MM-dd'T'HH:mm:ss") {
                sdf.timeZone = TimeZone.getTimeZone("UTC")
            }
            sdf.parse(raw)?.time
        }.getOrNull()?.let { return it }
    }

    return null
}

private fun Double.asAmount(): String {
    val formatter = NumberFormat.getNumberInstance(Locale.Builder().setLanguage("en").setRegion("IN").build())
    return formatter.format(this)
}

private fun formatDisplayDate(raw: String?): String? {
    val millis = parseIsoToMillis(raw) ?: return null
    val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
    return sdf.format(millis)
}

private fun formatLocationLine(land: BuyerOfferLand): String {
    val locationRaw = land.location?.trim().orEmpty()
    val city = land.city?.trim().orEmpty()
    val state = land.state?.trim().orEmpty()
    val extractedPin = extractPincode(locationRaw)
    val pincode = land.pincode?.trim().takeIf { !it.isNullOrBlank() } ?: extractedPin

    val rawParts = if (locationRaw.isNotBlank()) {
        locationRaw.split(",").map { it.trim() }
    } else {
        listOf(city, state)
    }

    val parts = rawParts
        .filter { it.isNotBlank() }
        .filterNot { part -> pincode != null && part.equals(pincode, true) }

    val unique = mutableListOf<String>()
    for (part in parts) {
        if (unique.none { it.equals(part, true) }) {
            unique.add(part)
        }
    }

    val locationText = if (unique.isNotEmpty()) unique.joinToString(", ") else "-"
    return if (!pincode.isNullOrBlank()) "$locationText pin code - $pincode" else locationText
}

private fun extractPincode(raw: String): String? {
    if (raw.isBlank()) return null
    val matcher = Pattern.compile("\\b\\d{6}\\b").matcher(raw)
    return if (matcher.find()) matcher.group() else null
}
