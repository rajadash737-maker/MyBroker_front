package com.example.myapplication.ui.feature.sell_land.screens



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.sell_land.components.SellLandTopBar
import com.example.myapplication.ui.feature.sell_land.model.LandDetails
import com.example.myapplication.ui.feature.sell_land.viewmodel.SellLandViewModel








//=========================== LAND DETAILS=======================
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
    var state by remember { mutableStateOf(draft.landDetails?.state ?: "") }
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

            // Land Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Land Title / Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Land Type
            LandTypeDropdown(
                selected = landType,
                onSelected = { landType = it }
            )

            // Area + Unit (Row)
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

            // Location
            OutlinedTextField(
                value = state,
                onValueChange = { state = it },
                label = { Text("State") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = pincode,
                onValueChange = { pincode = it },
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
                    if (viewModel.canMoveNext()) {
                        onContinue()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && area.isNotBlank()
            ) {
                Text("Continue")
            }

        }
    }
}

//==============================AREY OF LAND=======================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AreaUnitDropdown(
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("Sq.ft", "Sq.m", "Acre", "Bigha")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Unit") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandTypeDropdown(
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("Residential", "Commercial", "Agricultural", "Industrial")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Land Type") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)

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
