package com.example.myapplication.ui.feature.buyer.payment



import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun BookNowKycScreen(
    state: BookNowState,
    onBuyerNameChange: (String) -> Unit,
    onBuyerMobileChange: (String) -> Unit,
    onAadhaarPicked: (Uri?) -> Unit,
    onPanPicked: (Uri?) -> Unit,
    onBankPicked: (Uri?) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val pickPdf = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        onAadhaarPicked(uri)
    }
    val pickPan = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        onPanPicked(uri)
    }
    val pickBank = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        onBankPicked(uri)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("KYC Documents (PDF required)")

        OutlinedTextField(
            value = state.buyerName,
            onValueChange = onBuyerNameChange,
            label = { Text("Buyer Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.buyerMobile,
            onValueChange = onBuyerMobileChange,
            label = { Text("Buyer Mobile") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Button(onClick = { pickPdf.launch("application/pdf") }, modifier = Modifier.fillMaxWidth()) {
            Text(if (state.aadhaarUri == null) "Upload Aadhaar PDF" else "Aadhaar Selected")
        }
        Button(onClick = { pickPan.launch("application/pdf") }, modifier = Modifier.fillMaxWidth()) {
            Text(if (state.panUri == null) "Upload PAN PDF" else "PAN Selected")
        }
        Button(onClick = { pickBank.launch("application/pdf") }, modifier = Modifier.fillMaxWidth()) {
            Text(if (state.bankUri == null) "Upload Bank PDF" else "Bank Doc Selected")
        }

        Button(
            onClick = onContinue,
            enabled = !state.alreadyBooked,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Continue") }
        TextButton(onClick = onBack) { Text("Back") }
    }
}
