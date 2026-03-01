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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.sell_land.components.SellLandTopBar
import com.example.myapplication.ui.feature.sell_land.model.LandDetails
import com.example.myapplication.ui.feature.sell_land.viewmodel.SellLandViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandDetailsScreen(
    viewModel: SellLandViewModel,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val draft by viewModel.draft.collectAsState()

    var title by remember { mutableStateOf(draft.landDetails?.title ?: "") }
    var landType by remember { mutableStateOf(draft.landDetails?.landType ?: "Residential") }
    var area by remember { mutableStateOf(draft.landDetails?.area ?: "") }
    var areaUnit by remember { mutableStateOf(draft.landDetails?.areaUnit ?: "Sq.ft") }
    var state by remember { mutableStateOf(draft.landDetails?.state?.ifBlank { "Odisha" } ?: "Odisha") }
    var city by remember { mutableStateOf(draft.landDetails?.city ?: "") }
    var pincode by remember { mutableStateOf(draft.landDetails?.pincode ?: "") }

    Scaffold(
        topBar = {
            SellLandTopBar(
                title = "Land Details",
                onBack = onBack,
                onSaveDraft = {
                    viewModel.updateLandDetails(
                        LandDetails(
                            title = title,
                            landType = landType,
                            area = area,
                            areaUnit = areaUnit,
                            state = state,
                            city = city,
                            pincode = pincode
                        )
                    )
                    viewModel.saveDraft(step = 2)
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Land Information",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Land Title / Name") },
                modifier = Modifier.fillMaxWidth()
            )

            LandTypeDropdown(
                selected = landType,
                onSelected = { landType = it }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("Area") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                AreaUnitDropdown(
                    selected = areaUnit,
                    onSelected = { areaUnit = it },
                    modifier = Modifier.weight(1f)
                )
            }

            StateDropdown(
                selected = state,
                onSelected = { selectedState ->
                    state = selectedState
                    if (city !in ODISHA_CITIES) city = ""
                }
            )

            CityDropdown(
                selected = city,
                state = state,
                onSelected = { city = it }
            )

            OutlinedTextField(
                value = pincode,
                onValueChange = { value ->
                    pincode = value.filter { it.isDigit() }.take(6)
                },
                label = { Text("Pincode") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.updateLandDetails(
                        LandDetails(
                            title = title,
                            landType = landType,
                            area = area,
                            areaUnit = areaUnit,
                            state = state,
                            city = city,
                            pincode = pincode
                        )
                    )
                    viewModel.saveDraft(step = 2)
                    if (viewModel.canMoveNext()) onContinue()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() &&
                        area.isNotBlank() &&
                        state.isNotBlank() &&
                        city.isNotBlank() &&
                        pincode.length == 6
            ) {
                Text("Continue")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AreaUnitDropdown(
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("Sq.ft", "Sq.m", "Acre", "Bigha")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Unit") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandTypeDropdown(
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("Residential", "Commercial", "Agricultural", "Industrial")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Land Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StateDropdown(
    selected: String,
    onSelected: (String) -> Unit
) {
    val options = listOf("Odisha")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("State") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDropdown(
    selected: String,
    state: String,
    onSelected: (String) -> Unit
) {
    val options = if (state == "Odisha") ODISHA_CITIES else emptyList()
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (options.isNotEmpty()) expanded = !expanded
        }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            enabled = options.isNotEmpty(),
            label = { Text("City") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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

private val ODISHA_CITIES = listOf(
    "Angul",
    "Balangir",
    "Balasore",
    "Bargarh",
    "Bhadrak",
    "Boudh",
    "Cuttack",
    "Deogarh",
    "Dhenkanal",
    "Gajapati",
    "Ganjam",
    "Jagatsinghpur",
    "Jajpur",
    "Jharsuguda",
    "Kalahandi",
    "Kandhamal",
    "Kendrapara",
    "Kendujhar (Keonjhar)",
    "Khordha",
    "Koraput",
    "Malkangiri",
    "Mayurbhanj",
    "Nabarangpur",
    "Nayagarh",
    "Nuapada",
    "Puri",
    "Rayagada",
    "Sambalpur",
    "Subarnapur (Sonepur)",
    "Sundargarh"
)
