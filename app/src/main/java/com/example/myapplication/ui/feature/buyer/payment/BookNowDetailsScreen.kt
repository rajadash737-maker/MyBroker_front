package com.example.myapplication.ui.feature.buyer.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun BookNowDetailsScreen(
    state: BookNowState,
    formatAmount: (Double?) -> String,
    onAcceptTerms: (Boolean) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val expectedPrice = state.expectedPrice
    val dealAmount = state.dealAmount
    val formatter = NumberFormat.getNumberInstance(Locale.Builder().setLanguage("en").setRegion("IN").build())
    val formatInr: (Double?) -> String = { value ->
        if (value == null) "-" else "₹ ${formatter.format(value)}"
    }
    val discountAmount = if (expectedPrice != null && dealAmount != null && expectedPrice > 0 && dealAmount < expectedPrice) {
        expectedPrice - dealAmount
    } else {
        null
    }
    val discountPercent = if (discountAmount != null && expectedPrice != null && expectedPrice > 0) {
        ((discountAmount / expectedPrice) * 100).roundToInt()
    } else {
        null
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Book Now - Details")

        Text("Title: ${state.landTitle}")
        Text("Type: ${state.landType}")
        Text("Area: ${state.areaText}")
        Text("City: ${state.city}")
        Text("State: ${state.stateName}")
        Text("Pincode: ${state.pincode}")
        Text("Total Price: ${formatInr(state.expectedPrice)}")
        Text("Deal Amount: ${formatInr(state.dealAmount)}")

        discountAmount?.let {
            val percentText = discountPercent?.let { percent -> " ($percent%)" }.orEmpty()
            Text("Discount: ${formatInr(it)}$percentText")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Book Now Charges: ₹1000 (Non-Refundable)")
        Text("Ownership charge time: 48 hours")

        Spacer(modifier = Modifier.height(8.dp))
        RowWithCheckbox(
            checked = state.termsAccepted,
            onCheckedChange = onAcceptTerms,
            label = "I accept Terms & Conditions"
        )

        Button(
            onClick = onContinue,
            enabled = state.termsAccepted && !state.alreadyBooked,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Continue") }

        TextButton(onClick = onBack) { Text("Back") }
    }
}

@Composable
private fun RowWithCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(label)
    }
}
