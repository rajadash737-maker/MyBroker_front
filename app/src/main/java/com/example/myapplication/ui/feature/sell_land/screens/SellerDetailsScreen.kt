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

private data class SellerTypeOption(
    val label: String,
    val value: String
)

private data class SellerCategoryOption(
    val label: String,
    val value: String
)

private val sellerTypeOptions = listOf(
    SellerTypeOption("Single Owner", "single_owner"),
    SellerTypeOption("Multi Owner", "multi_owner"),
    SellerTypeOption("Inherited Land", "inherited_land")
)

private val sellerCategoryOptions = listOf(
    SellerCategoryOption("General", "general"),
    SellerCategoryOption("OBC", "obc"),
    SellerCategoryOption("SC", "sc"),
    SellerCategoryOption("ST", "st")
)

private fun sellerTypeLabel(value: String): String {
    return sellerTypeOptions.firstOrNull { it.value == value }?.label ?: "Single Owner"
}

private fun sellerCategoryLabel(value: String): String {
    return sellerCategoryOptions.firstOrNull { it.value == value }?.label ?: "General"
}

private fun normalizeSellerType(raw: String?): String {
    return when (raw?.trim()?.lowercase()) {
        "single_owner" -> "single_owner"
        "multi_owner" -> "multi_owner"
        "inherited_land" -> "inherited_land"

        // legacy/app old values fallback
        "owner" -> "single_owner"
        "broker" -> "multi_owner"
        "power_of_attorney" -> "inherited_land"
        "power of attorney" -> "inherited_land"
        "other" -> "single_owner"

        else -> "single_owner"
    }
}

private fun normalizeSellerCategory(raw: String?): String {
    return when (raw?.trim()?.lowercase()) {
        "general" -> "general"
        "obc" -> "obc"
        "sc" -> "sc"
        "st" -> "st"
        else -> "general"
    }
}

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
    var sellerType by remember { mutableStateOf("single_owner") }
    var sellerCategory by remember { mutableStateOf("general") }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(draft.sellerDetails) {
        name = draft.sellerDetails?.name.orEmpty()
        mobile = draft.sellerDetails?.mobile.orEmpty()
        sellerType = normalizeSellerType(draft.sellerDetails?.sellerType)
        sellerCategory = normalizeSellerCategory(draft.sellerDetails?.sellerCategory)
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
                            sellerType = sellerType,
                            sellerCategory = sellerCategory
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
                selectedValue = sellerType,
                onSelected = { sellerType = it }
            )

            SellerCategoryDropdown(
                selectedValue = sellerCategory,
                onSelected = { sellerCategory = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.updateSellerDetails(
                        SellerDetails(
                            name = name.trim(),
                            mobile = mobile.trim(),
                            sellerType = sellerType,
                            sellerCategory = sellerCategory
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
                onResume = { showDialog = false },
                onStartNew = {
                    viewModel.clearDraft()
                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerTypeDropdown(
    selectedValue: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = sellerTypeLabel(selectedValue),
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
            sellerTypeOptions.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.label) },
                    onClick = {
                        onSelected(item.value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerCategoryDropdown(
    selectedValue: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = sellerCategoryLabel(selectedValue),
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
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
            sellerCategoryOptions.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.label) },
                    onClick = {
                        onSelected(item.value)
                        expanded = false
                    }
                )
            }
        }
    }
}
