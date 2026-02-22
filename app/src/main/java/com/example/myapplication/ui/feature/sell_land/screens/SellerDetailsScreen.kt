package com.example.myapplication.ui.feature.sell_land.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.sell_land.components.DraftResumeDialog
import com.example.myapplication.ui.feature.sell_land.components.SellLandTopBar
import com.example.myapplication.ui.feature.sell_land.components.StepIndicator
import com.example.myapplication.ui.feature.sell_land.model.SellLandStep
import com.example.myapplication.ui.feature.sell_land.model.SellerDetails
import com.example.myapplication.ui.feature.sell_land.viewmodel.SellLandViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDetailsScreen(
    viewModel: SellLandViewModel,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val draft by viewModel.draft.collectAsState()

    var name by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var sellerType by remember { mutableStateOf("Owner") }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(draft.sellerDetails) {
        name = draft.sellerDetails?.name.orEmpty()
        mobile = draft.sellerDetails?.mobile.orEmpty()
        sellerType = draft.sellerDetails?.sellerType ?: "Owner"
    }

    LaunchedEffect(Unit) {
        showDialog = !viewModel.isDraftEmpty()
    }

    Scaffold(
        topBar = {
            SellLandTopBar(
                title = "Seller Details",
                onBack = onBack,
                onSaveDraft = {
                    viewModel.updateSellerDetails(
                        SellerDetails(
                            name = name.trim(),
                            mobile = mobile.trim(),
                            sellerType = sellerType
                        )
                    )
                    viewModel.saveDraft(step = 1)
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
            StepIndicator(currentStep = SellLandStep.SELLER)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Seller Information",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = mobile,
                onValueChange = { mobile = it },
                label = { Text("Mobile Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            SellerTypeDropdown(
                selected = sellerType,
                onSelected = { sellerType = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.updateSellerDetails(
                        SellerDetails(
                            name = name.trim(),
                            mobile = mobile.trim(),
                            sellerType = sellerType
                        )
                    )
                    viewModel.saveDraft(step = 1)
                    onContinue()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && mobile.length >= 10
            ) {
                Text("Continue")
            }
        }

        if (showDialog) {
            DraftResumeDialog(
                onResume = {
                    showDialog = false
                },
                onStartNew = {
                    viewModel.clearDraft()
                    showDialog = false
                },
                onDismiss = {
                    showDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerTypeDropdown(
    selected: String,
    onSelected: (String) -> Unit
) {
    val options = listOf("Owner", "Broker", "Power of Attorney", "Other")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Seller Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    type = MenuAnchorType.PrimaryEditable,
                    enabled = true
                )
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
