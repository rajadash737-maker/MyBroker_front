package com.example.myapplication.ui.feature.sell_land.status.components




import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
        ListingStatus.CANCELLED -> Color(0xFF9E9E9E)   // 👈 add this
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = status.name.replace("_", " "),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}