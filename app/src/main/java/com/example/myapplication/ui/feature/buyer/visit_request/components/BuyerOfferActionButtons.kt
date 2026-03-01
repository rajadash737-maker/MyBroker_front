package com.example.myapplication.ui.feature.buyer.visit_request.components



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BuyerOfferActionButtons(
    canAcceptCounter: Boolean,
    canRejectCounter: Boolean,
    onAcceptCounter: () -> Unit,
    onRejectCounter: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!canAcceptCounter && !canRejectCounter) return

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (canRejectCounter) {
            OutlinedButton(
                onClick = onRejectCounter,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Reject")
            }
        }

        if (canAcceptCounter) {
            Button(
                onClick = onAcceptCounter,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Accept Counter")
            }
        }
    }
}
