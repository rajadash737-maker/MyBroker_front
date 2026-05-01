package com.example.myapplication.ui.feature.buyer.visit_request.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.feature.buyer.domain.model.BuyerOfferTimelineEvent
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun BuyerOfferTimeline(
    events: List<BuyerOfferTimelineEvent>,
    modifier: Modifier = Modifier
) {
    if (events.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(id = R.string.buyer_timeline),
            style = MaterialTheme.typography.titleSmall
        )

        events.forEachIndexed { index, event ->
            val title = remember(event.label, event.key) {
                val raw = event.label.ifBlank { event.key.replace("_", " ") }
                raw.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            }
            val timestamp = remember(event.at) { formatTimelineTimestamp(event.at) }
            val isLast = index == events.lastIndex
            val isFailed = title.contains("expired", true) || title.contains("rejected", true) || title.contains("cancel", true)
            val dotColor = if (isFailed) Color(0xFFD32F2F) else Color(0xFF2E7D32)
            val lineColor = if (isFailed) Color(0xFFF8BBD0) else Color(0xFFC8E6C9)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(dotColor, CircleShape)
                    )
                    if (!isLast) {
                        Spacer(
                            modifier = Modifier
                                .height(22.dp)
                                .width(2.dp)
                                .background(lineColor)
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    Text(
                        text = timestamp ?: event.at,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun formatTimelineTimestamp(raw: String?): String? {
    val millis = parseIsoToMillis(raw) ?: return null
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(millis)
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
