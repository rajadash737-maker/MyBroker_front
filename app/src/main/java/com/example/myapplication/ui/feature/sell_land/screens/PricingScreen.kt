package com.example.myapplication.ui.feature.sell_land.screens



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.sell_land.components.SellLandTopBar
import com.example.myapplication.ui.feature.sell_land.model.PricingDetails
import com.example.myapplication.ui.feature.sell_land.viewmodel.SellLandViewModel

@Composable
fun PricingScreen(
    viewModel: SellLandViewModel,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val draft by viewModel.draft.collectAsState()

    var priceType by remember {
        mutableStateOf(draft.pricing?.priceType ?: "Total Price")
    }
    var price by remember {
        mutableStateOf(draft.pricing?.price?.toString() ?: "")
    }
    var negotiable by remember {
        mutableStateOf(draft.pricing?.negotiable ?: false)
    }
    var availability by remember {
        mutableStateOf(draft.pricing?.availability ?: "Immediate")
    }

    Scaffold(
        topBar = {
            SellLandTopBar(
                title = "Pricing",
                onBack = onBack,
                onSaveDraft = {
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

            // Price Type
            PriceTypeDropdown(
                selected = priceType,
                onSelected = { priceType = it }
            )

            // Price Input
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Expected Price (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Text("₹") }
            )

            // Negotiable
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
                selected = availability,
                onSelected = { availability = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.updatePricing(
                        PricingDetails(
                            priceType = priceType,
                            price = price.toDoubleOrNull() ?: 0.0,
                            negotiable = negotiable,
                            availability = availability
                        )
                    )
                    viewModel.saveDraft(step = 5)
                    onContinue()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = price.toDoubleOrNull()?.let { it > 0 } == true
            ) {
                Text("Continue")
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceTypeDropdown(
    selected: String,
    onSelected: (String) -> Unit
) {
    val options = listOf("Total Price", "Price per Sq.ft")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Price Type") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)  // safe for now

        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun AvailabilitySelector(
    selected: String,
    onSelected: (String) -> Unit
) {
    val options = listOf("Immediate", "Within 3 Months")

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(option)
                RadioButton(
                    selected = selected == option,
                    onClick = { onSelected(option) }
                )
            }
        }
    }
}

