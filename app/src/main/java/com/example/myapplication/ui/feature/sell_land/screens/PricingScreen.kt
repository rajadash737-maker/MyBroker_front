package com.example.myapplication.ui.feature.sell_land.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
//import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.sell_land.components.SellLandTopBar
import com.example.myapplication.ui.feature.sell_land.model.PricingDetails
import com.example.myapplication.ui.feature.sell_land.viewmodel.SellLandViewModel
import java.math.BigDecimal

private fun toPlainPrice(value: Double?): String {
    if (value == null || value <= 0.0) return ""
    return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString()
}

private fun normalizePriceType(value: String?): String {
    return when (value?.trim()?.lowercase()) {
        "total_price", "total price" -> "total_price"
        "price_per_unit", "price per unit", "price per sq.ft", "price per sqft" -> "price_per_unit"
        else -> "total_price"
    }
}

private fun priceTypeLabel(value: String): String {
    return when (value) {
        "price_per_unit" -> "Price per Sq.ft"
        else -> "Total Price"
    }
}

private fun normalizeAvailability(value: String?): String {
    return when (value?.trim()?.lowercase()) {
        "within_3_months", "within 3 months" -> "within_3_months"
        else -> "immediate"
    }
}

private fun availabilityLabel(value: String): String {
    return when (value) {
        "within_3_months" -> "Within 3 Months"
        else -> "Immediate"
    }
}

private fun sanitizeDecimalInput(input: String): String {
    val filtered = input.filter { it.isDigit() || it == '.' }
    val firstDot = filtered.indexOf('.')
    return if (firstDot == -1) filtered
    else {
        val before = filtered.substring(0, firstDot + 1)
        val after = filtered.substring(firstDot + 1).replace(".", "")
        before + after
    }
}

@Composable
fun PricingScreen(
    viewModel: SellLandViewModel,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val draft by viewModel.draft.collectAsState()

    var priceType by remember { mutableStateOf("total_price") }
    var price by remember { mutableStateOf("") }
    var negotiable by remember { mutableStateOf(false) }
    var availability by remember { mutableStateOf("immediate") }

    LaunchedEffect(draft.pricing) {
        priceType = normalizePriceType(draft.pricing?.priceType)
        price = toPlainPrice(draft.pricing?.price)
        negotiable = draft.pricing?.negotiable ?: false
        availability = normalizeAvailability(draft.pricing?.availability)
    }

    fun savePricing() {
        viewModel.updatePricing(
            PricingDetails(
                priceType = priceType,
                price = price.toDoubleOrNull() ?: 0.0,
                negotiable = negotiable,
                availability = availability
            )
        )
        viewModel.saveDraft(step = 5)
    }

    Scaffold(
        topBar = {
            SellLandTopBar(
                title = "Pricing",
                onBack = onBack,
                onSaveDraft = { savePricing() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Price Details",
                style = MaterialTheme.typography.titleMedium
            )

            PriceTypeDropdown(
                selectedValue = priceType,
                onSelected = { priceType = it }
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = sanitizeDecimalInput(it) },
                label = { Text("Expected Price (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Text("₹") },
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Price Negotiable")
                Switch(
                    checked = negotiable,
                    onCheckedChange = { negotiable = it }
                )
            }

            HorizontalDivider()

            Text(
                text = "Availability",
                style = MaterialTheme.typography.titleMedium
            )

            AvailabilitySelector(
                selectedValue = availability,
                onSelected = { availability = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    savePricing()
                    onContinue()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = (price.toDoubleOrNull() ?: 0.0) > 0.0
            ) {
                Text("Continue")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceTypeDropdown(
    selectedValue: String,
    onSelected: (String) -> Unit
) {
    val options = listOf(
        "total_price" to "Total Price",
        "price_per_unit" to "Price per Sq.ft"
    )
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = priceTypeLabel(selectedValue),
            onValueChange = {},
            readOnly = true,
            label = { Text("Price Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (value, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onSelected(value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AvailabilitySelector(
    selectedValue: String,
    onSelected: (String) -> Unit
) {
    val options = listOf(
        "immediate" to "Immediate",
        "within_3_months" to "Within 3 Months"
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (value, label) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(label)
                RadioButton(
                    selected = selectedValue == value,
                    onClick = { onSelected(value) }
                )
            }
        }
    }
}
