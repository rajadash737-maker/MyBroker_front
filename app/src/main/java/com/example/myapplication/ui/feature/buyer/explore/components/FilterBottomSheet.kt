package com.example.myapplication.ui.feature.buyer.explore.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.buyer.explore.LandFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    initialFilter: LandFilter = LandFilter(),
    onApply: (LandFilter) -> Unit,
    onDismiss: () -> Unit
) {
    var minPrice by rememberSaveable { mutableStateOf(initialFilter.minPrice?.toString().orEmpty()) }
    var maxPrice by rememberSaveable { mutableStateOf(initialFilter.maxPrice?.toString().orEmpty()) }
    var minArea by rememberSaveable { mutableStateOf(initialFilter.minArea?.toString().orEmpty()) }
    var maxArea by rememberSaveable { mutableStateOf(initialFilter.maxArea?.toString().orEmpty()) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Filter Lands",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = minPrice,
                onValueChange = { minPrice = it.filter(Char::isDigit) },
                label = { Text("Min Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = maxPrice,
                onValueChange = { maxPrice = it.filter(Char::isDigit) },
                label = { Text("Max Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = minArea,
                onValueChange = { minArea = it.filter(Char::isDigit) },
                label = { Text("Min Area") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = maxArea,
                onValueChange = { maxArea = it.filter(Char::isDigit) },
                label = { Text("Max Area") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onApply(
                        LandFilter(
                            minPrice = minPrice.toIntOrNull(),
                            maxPrice = maxPrice.toIntOrNull(),
                            minArea = minArea.toIntOrNull(),
                            maxArea = maxArea.toIntOrNull()
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Apply Filter")
            }

            TextButton(
                onClick = {
                    onApply(LandFilter())
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear Filter")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
