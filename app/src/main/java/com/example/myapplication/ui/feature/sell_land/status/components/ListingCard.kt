package com.example.myapplication.ui.feature.sell_land.status.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.myapplication.network.ApiClient
import com.example.myapplication.ui.feature.sell_land.model.SellLandModel
import com.example.myapplication.ui.feature.sell_land.status.ListingStatus
import java.text.NumberFormat
import java.util.Locale

private fun normalizeImageUrl(raw: String?): String? {
    if (raw.isNullOrBlank()) return null
    return ApiClient.absoluteUrl(raw.trim().replace("\\", "/"))
}

private fun formatInrAmount(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
    return "Rs ${formatter.format(amount)}"
}

@Composable
fun ListingCard(
    listing: SellLandModel,
    onEdit: () -> Unit = {},
    onCancel: () -> Unit = {},
    onViewOffers: () -> Unit = {},
    onViewMore: () -> Unit = {}
) {
    val isPaymentSuccess = listing.paymentStatus.equals("success", ignoreCase = true)
    val canCancel = listing.canCancelListing &&
        isPaymentSuccess &&
        listing.status !in setOf(
            ListingStatus.DRAFT,
            ListingStatus.BOOKED,
            ListingStatus.SOLD,
            ListingStatus.PROCESSING_REFUND,
            ListingStatus.REFUND_SUCCESSFUL
        )

    val formattedPrice = NumberFormat.getNumberInstance(Locale("en", "IN")).format(listing.price)
    val coverUrl = remember(listing.images) { normalizeImageUrl(listing.images.firstOrNull()) }
    val normalizedBooking = listing.bookingStatus?.trim()?.lowercase()
    val bookingLabel = when (normalizedBooking) {
        "registry_scheduled" -> "Registry Scheduled"
        "in_process" -> "In Process"
        "registry_done" -> "Registry Done"
        "sold" -> "Sold"
        "booked" -> "Booked"
        else -> when (listing.status) {
            ListingStatus.BOOKED -> "Booked"
            ListingStatus.SOLD -> "Sold"
            else -> null
        }
    }
    val bookingBadgeColors = when (normalizedBooking) {
        "registry_scheduled", "in_process" -> Pair(Color(0xFF1565C0), Color.White)
        "registry_done", "sold" -> Pair(Color(0xFF2E7D32), Color.White)
        "booked" -> Pair(Color(0xFF1565C0), Color.White)
        else -> when (listing.status) {
            ListingStatus.BOOKED -> Pair(Color(0xFF1565C0), Color.White)
            ListingStatus.SOLD -> Pair(Color(0xFF2E7D32), Color.White)
            else -> null
        }
    }
    val showViewMore = listing.status in setOf(ListingStatus.BOOKED, ListingStatus.SOLD) || normalizedBooking in setOf(
        "booked",
        "registry_scheduled",
        "in_process",
        "registry_done",
        "sold"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .background(Color(0xFFF1F1F1), RoundedCornerShape(14.dp))
            ) {
                if (coverUrl == null) {
                    Text(
                        text = "No image",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Gray
                    )
                } else {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(coverUrl)
                            .crossfade(true)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(strokeWidth = 2.dp)
                            }
                        },
                        error = {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Image failed", color = Color.Gray)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = listing.title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Listing ID: ${listing.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "Rs $formattedPrice",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                StatusBadge(
                    status = listing.status,
                    labelOverride = bookingLabel?.uppercase(),
                    backgroundOverride = bookingBadgeColors?.first,
                    contentOverride = bookingBadgeColors?.second
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            SellStatusStepper(
                status = listing.status,
                statusBeforeCancellation = listing.statusBeforeCancellation
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (listing.status == ListingStatus.NEEDS_CORRECTION && !listing.rejectionReason.isNullOrBlank()) {
                Surface(color = Color(0xFFFFF3E0), shape = RoundedCornerShape(10.dp)) {
                    Text(
                        text = "Admin Note: ${listing.rejectionReason}",
                        modifier = Modifier.padding(10.dp),
                        color = Color(0xFFE65100),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (listing.status == ListingStatus.LIVE) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${listing.offerCount} Offers",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Likes", tint = Color(0xFFE53935))
                        Text(" ${listing.likesCount}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            when (listing.status) {
                ListingStatus.LIVE -> Button(onClick = onViewOffers, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text("View Offers") }
                ListingStatus.DRAFT -> Button(onClick = onEdit, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text("Complete Listing") }
                ListingStatus.NEEDS_CORRECTION -> Button(onClick = onEdit, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text("Fix & Resubmit") }
                ListingStatus.UNDER_REVIEW -> OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text("Under Review") }
                ListingStatus.APPROVED -> OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text("Waiting For Field Verification") }
                ListingStatus.FIELD_VERIFIED -> OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text("Waiting For Final Admin Approval") }
                ListingStatus.BOOKED -> {
                    Column {
                        Text(
                            bookingLabel?.let { "$it" } ?: "Booked Successfully",
                            color = Color(0xFF1565C0),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        listing.dealAmount?.let { amount ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Deal Amount: ${formatInrAmount(amount)}",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (showViewMore) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = onViewMore, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                                Text("View More")
                            }
                        }
                    }
                }
                ListingStatus.SOLD -> {
                    Column {
                        Text(
                            bookingLabel?.let { "$it" } ?: "Sold Successfully",
                            color = Color(0xFF2E7D32),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (showViewMore) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = onViewMore, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                                Text("View More")
                            }
                        }
                    }
                }
                ListingStatus.PROCESSING_REFUND -> {
                    Column {
                        Text("Cancellation Confirmed", color = Color.Red, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Refund is processing. It will be completed after admin confirmation.",
                            color = Color(0xFF2E7D32),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                ListingStatus.REFUND_SUCCESSFUL -> Text("Refund Successful", color = Color(0xFF2E7D32), style = MaterialTheme.typography.bodyMedium)
            }

            if (canCancel) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onCancel, modifier = Modifier.align(Alignment.End)) {
                    Text("Cancel Listing", color = Color.Red)
                }
            }
        }
    }
}
