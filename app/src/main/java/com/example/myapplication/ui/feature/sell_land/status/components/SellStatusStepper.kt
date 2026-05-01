package com.example.myapplication.ui.feature.sell_land.status.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.sell_land.status.ListingStatus

@Composable
fun SellStatusStepper(
    status: ListingStatus,
    statusBeforeCancellation: ListingStatus? = null
) {
    val steps = listOf(
        ListingStatus.DRAFT,
        ListingStatus.UNDER_REVIEW,
        ListingStatus.APPROVED,
        ListingStatus.FIELD_VERIFIED,
        ListingStatus.LIVE,
        ListingStatus.BOOKED,
        ListingStatus.SOLD
    )

    val effectiveStatus = when (status) {
        ListingStatus.PROCESSING_REFUND, ListingStatus.REFUND_SUCCESSFUL ->
            statusBeforeCancellation ?: ListingStatus.LIVE
        ListingStatus.NEEDS_CORRECTION ->
            ListingStatus.UNDER_REVIEW
        ListingStatus.SOLD ->
            ListingStatus.SOLD
        else -> status
    }

    val currentIndex = steps.indexOf(effectiveStatus).coerceAtLeast(0)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, step ->
            val isCompleted = index <= currentIndex
            val circleColor = if (isCompleted) Color(0xFF4CAF50) else Color.LightGray

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(28.dp)
                        .background(circleColor, CircleShape)
                ) {
                    if (isCompleted) {
                        Text(
                            text = "✓",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = step.name.replace("_", " "),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCompleted) Color.Black else Color.Gray
                )
            }
        }
    }

    if (status == ListingStatus.PROCESSING_REFUND || status == ListingStatus.REFUND_SUCCESSFUL) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (status == ListingStatus.PROCESSING_REFUND) {
                "REFUND: PROCESSING"
            } else {
                "REFUND: SUCCESSFUL"
            },
            style = MaterialTheme.typography.labelMedium,
            color = if (status == ListingStatus.PROCESSING_REFUND) Color(0xFFE65100) else Color(0xFF2E7D32)
        )
    }
}
