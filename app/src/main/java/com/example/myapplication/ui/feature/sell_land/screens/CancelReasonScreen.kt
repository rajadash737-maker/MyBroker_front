package com.example.myapplication.ui.feature.sell_land.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.ui.feature.sell_land.viewmodel.SellLandViewModel

@Composable
fun CancelReasonScreen(
    listingId: String,
    navController: NavController,
    viewModel: SellLandViewModel
) {
    var selectedReasonUi by remember { mutableStateOf("") }
    var upiId by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val loading by viewModel.loading.collectAsState()
    val cancelSuccessMessage by viewModel.cancelSuccessMessage.collectAsState()
    val error by viewModel.error.collectAsState()

    val reasons = listOf(
        "Price Issue",
        "Found Another Buyer",
        "Personal Reason",
        "Document Problem"
    )

    fun mapReasonToApi(reason: String): String = when (reason) {
        "Price Issue" -> "price_issue"
        "Found Another Buyer" -> "found_another_buyer"
        "Personal Reason" -> "personal_reason"
        "Document Problem" -> "document_problem"
        else -> ""
    }

    fun isValidUpi(input: String): Boolean {
        val value = input.trim().lowercase()
        return value.contains("@") && value.length >= 5
    }

    LaunchedEffect(cancelSuccessMessage) {
        if (!cancelSuccessMessage.isNullOrBlank()) {
            viewModel.clearCancelSuccessMessage()
            navController.popBackStack()
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Cancellation") },
            text = { Text("Are you sure you want to cancel this listing? Refund will be processed to provided UPI ID.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        val requestId = listingId.toIntOrNull() ?: return@TextButton
                        val apiReason = mapReasonToApi(selectedReasonUi)
                        if (apiReason.isBlank()) return@TextButton

                        viewModel.cancelListing(
                            requestId = requestId,
                            reason = apiReason,
                            upiId = upiId.trim(),
                            notes = notes.trim().ifBlank { null }
                        )
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Why are you cancelling?",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        reasons.forEach { reason ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedReasonUi == reason,
                        onClick = { selectedReasonUi = reason }
                    )
                    .padding(vertical = 8.dp)
            ) {
                RadioButton(
                    selected = selectedReasonUi == reason,
                    onClick = { selectedReasonUi = reason }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = reason)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = upiId,
            onValueChange = { upiId = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Refund UPI ID") },
            singleLine = true,
            isError = upiId.isNotBlank() && !isValidUpi(upiId)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Optional note") },
            maxLines = 3
        )

        if (!error.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = error ?: "", color = androidx.compose.material3.MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { showConfirmDialog = true },
            enabled = selectedReasonUi.isNotEmpty() && isValidUpi(upiId) && !loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.width(18.dp), strokeWidth = 2.dp)
            } else {
                Text("Submit")
            }
        }
    }
}
