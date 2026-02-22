package com.example.myapplication.ui.feature.sell_land.status.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box

import com.example.myapplication.ui.feature.sell_land.status.ListingStatus

import androidx.compose.foundation.layout.*

import androidx.compose.material3.*

import androidx.compose.ui.Alignment

@Composable
fun SellStatusStepper(status: ListingStatus) {
    val steps = listOf(
        ListingStatus.DRAFT,
        ListingStatus.UNDER_REVIEW,
        ListingStatus.APPROVED,
        ListingStatus.FIELD_VERIFIED,
        ListingStatus.LIVE,
        ListingStatus.SOLD
    )

    val currentIndex = when (status) {
        ListingStatus.NEEDS_CORRECTION -> 1 // correction usually after review
        else -> steps.indexOf(status).coerceAtLeast(0)
    }

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
                        Text("✓", color = Color.White, style = MaterialTheme.typography.labelMedium)
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
}
