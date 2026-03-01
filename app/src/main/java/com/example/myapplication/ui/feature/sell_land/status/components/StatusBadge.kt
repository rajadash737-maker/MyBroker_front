package com.example.myapplication.ui.feature.sell_land.status.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.sell_land.status.ListingStatus

@Composable
fun StatusBadge(status: ListingStatus) {
    val backgroundColor = when (status) {
        ListingStatus.DRAFT -> Color.Gray
        ListingStatus.UNDER_REVIEW -> Color(0xFFFF9800)
        ListingStatus.APPROVED -> Color(0xFF1976D2)
        ListingStatus.FIELD_VERIFIED -> Color(0xFF009688)
        ListingStatus.NEEDS_CORRECTION -> Color(0xFFD32F2F)
        ListingStatus.LIVE -> Color(0xFF4CAF50)
        ListingStatus.SOLD -> Color(0xFF2E7D32)
        ListingStatus.PROCESSING_REFUND -> Color(0xFFE65100)
        ListingStatus.REFUND_SUCCESSFUL -> Color(0xFF2E7D32)
    }

    val label = when (status) {
        ListingStatus.PROCESSING_REFUND -> "PROCESSING REFUND"
        ListingStatus.REFUND_SUCCESSFUL -> "REFUND SUCCESSFUL"
        else -> status.name.replace("_", " ")
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}
