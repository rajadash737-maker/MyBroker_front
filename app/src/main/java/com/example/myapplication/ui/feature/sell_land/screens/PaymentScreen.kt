package com.example.myapplication.ui.feature.sell_land.screens



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.sell_land.components.SellLandTopBar
import com.example.myapplication.ui.feature.sell_land.viewmodel.SellLandViewModel

@Composable
fun PaymentScreen(
    viewModel: SellLandViewModel,
    onBack: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    val listingFee = 999

    var selectedMethod by remember { mutableStateOf("upi") }

    val submitResult by viewModel.submitResult.collectAsState()
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(submitResult?.requestId) {
        if (submitResult != null) {
            onSubmitSuccess()
            viewModel.clearSubmitResult()
        }
    }

    Scaffold(
        topBar = {
            SellLandTopBar(
                title = "Payment",
                onBack = onBack,
                onSaveDraft = { viewModel.saveDraft(step = 7) }


            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Listing Fee",
                    style = MaterialTheme.typography.titleMedium
                )

                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Sell Land Listing")
                        Text(
                            text = "₹$listingFee",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }

                Text(
                    text = "Payment Method",
                    style = MaterialTheme.typography.titleMedium
                )

                PaymentOption(
                    title = "UPI",
                    selected = selectedMethod == "upi",
                    onSelect = { selectedMethod = "upi" }
                )
                PaymentOption(
                    title = "Debit / Credit Card",
                    selected = selectedMethod == "debit_credit_card",
                    onSelect = { selectedMethod = "debit_credit_card" }
                )
                PaymentOption(
                    title = "Net Banking",
                    selected = selectedMethod == "net_banking",
                    onSelect = { selectedMethod = "net_banking" }
                )

            }

            Button(
                onClick = {
                    viewModel.initiatePayment(selectedMethod) { paymentId ->
                        viewModel.markPaidThenSubmit(paymentId)
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (loading) "Processing..." else "Pay ₹$listingFee")
            }

        }
    }
}

@Composable
fun PaymentOption(
    title: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onSelect
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(title)
        }
    }
}
