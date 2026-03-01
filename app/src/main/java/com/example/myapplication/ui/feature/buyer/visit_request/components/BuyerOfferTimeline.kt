package com.example.myapplication.ui.feature.buyer.visit_request.components



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.buyer.domain.model.BuyerOfferTimelineEvent

@Composable
fun BuyerOfferTimeline(
    events: List<BuyerOfferTimelineEvent>,
    modifier: Modifier = Modifier
) {
    if (events.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Timeline",
            style = MaterialTheme.typography.titleSmall
        )

        events.forEach { event ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.size(12.dp)
                )
                Column {
                    Text(
                        text = event.label,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = event.at,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
