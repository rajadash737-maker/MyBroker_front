package com.example.myapplication.ui.feature.sell_land.status.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.sell_land.model.SellLandModel
import com.example.myapplication.ui.feature.sell_land.status.ListingStatus
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ListingCard(
    listing: SellLandModel,
    onEdit: () -> Unit = {},
    onCancel: () -> Unit = {},
    onViewOffers: () -> Unit = {}
) {
    val canCancel = listing.status !in setOf(
        ListingStatus.SOLD,
        ListingStatus.PROCESSING_REFUND,
        ListingStatus.REFUND_SUCCESSFUL
    )

    val formattedPrice = NumberFormat.getNumberInstance(Locale("en", "IN")).format(listing.price)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = listing.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "₹ $formattedPrice",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                StatusBadge(status = listing.status)
            }

            Spacer(modifier = Modifier.height(14.dp))
            SellStatusStepper(
                status = listing.status,
                statusBeforeCancellation = listing.statusBeforeCancellation
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (listing.status == ListingStatus.NEEDS_CORRECTION &&
                !listing.rejectionReason.isNullOrBlank()
            ) {
                Surface(
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Admin Note: ${listing.rejectionReason}",
                        modifier = Modifier.padding(10.dp),
                        color = Color(0xFFE65100),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Offers + Likes summary in seller status card
            if (listing.status == ListingStatus.LIVE) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${listing.offerCount} Offers",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Likes",
                            tint = Color(0xFFE53935)
                        )
                        Text(
                            text = " ${listing.likesCount}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            when (listing.status) {
                ListingStatus.LIVE -> {
                    Button(
                        onClick = onViewOffers,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("View Offers")
                    }
                }

                ListingStatus.DRAFT -> {
                    Button(
                        onClick = onEdit,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Complete Listing")
                    }
                }

                ListingStatus.NEEDS_CORRECTION -> {
                    Button(
                        onClick = onEdit,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Fix & Resubmit")
                    }
                }

                ListingStatus.UNDER_REVIEW -> {
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Under Review")
                    }
                }

                ListingStatus.APPROVED -> {
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Waiting For Field Verification")
                    }
                }

                ListingStatus.FIELD_VERIFIED -> {
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Waiting For Final Admin Approval")
                    }
                }

                ListingStatus.SOLD -> {
                    Text(
                        text = "Sold Successfully",
                        color = Color(0xFF2E7D32),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                ListingStatus.PROCESSING_REFUND -> {
                    Column {
                        Text(
                            text = "Cancellation Confirmed",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Refund is processing. It will be completed after admin confirmation.",
                            color = Color(0xFF2E7D32),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                ListingStatus.REFUND_SUCCESSFUL -> {
                    Text(
                        text = "Refund Successful",
                        color = Color(0xFF2E7D32),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (canCancel) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cancel Listing", color = Color.Red)
                }
            }
        }
    }
}
