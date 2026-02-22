// ReviewScreen.kt
package com.example.myapplication.ui.feature.sell_land.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.sell_land.components.SellLandTopBar
import com.example.myapplication.ui.feature.sell_land.viewmodel.SellLandViewModel

@Composable
fun ReviewScreen(
    viewModel: SellLandViewModel,
    onEditSeller: () -> Unit,
    onEditLand: () -> Unit,
    onEditMedia: () -> Unit,
    onEditMap: () -> Unit,
    onEditPricing: () -> Unit,
    onProceedPayment: () -> Unit,
    onSubmitSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val draft by viewModel.draft.collectAsState()
    val paymentStatus by viewModel.currentPaymentStatus.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val submitResult by viewModel.submitResult.collectAsState()

    val alreadyPaid = paymentStatus.equals("success", ignoreCase = true)

    LaunchedEffect(submitResult?.requestId) {
        if (submitResult != null) {
            onSubmitSuccess()
        }
    }

    Scaffold(
        topBar = {
            SellLandTopBar(
                title = "Review Details",
                onBack = onBack,
                onSaveDraft = { viewModel.saveDraft(step = 6) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ReviewSection(title = "Seller Details", onEdit = onEditSeller) {
                Text(draft.sellerDetails?.name ?: "-")
                Text(draft.sellerDetails?.mobile ?: "-")
                Text(draft.sellerDetails?.sellerType ?: "-")
            }

            ReviewSection(title = "Land Details", onEdit = onEditLand) {
                Text(draft.landDetails?.title ?: "-")
                Text(draft.landDetails?.landType ?: "-")
                Text("${draft.landDetails?.area ?: "-"} ${draft.landDetails?.areaUnit ?: ""}")
                Text("${draft.landDetails?.city ?: ""}, ${draft.landDetails?.state ?: ""}")
                Text("Pincode: ${draft.landDetails?.pincode ?: "-"}")
            }

            ReviewSection(title = "Photos & Documents", onEdit = onEditMedia) {
                Text("Photos: ${draft.images.size}")
                val docCount = listOf(
                    draft.documents.saleDeed,
                    draft.documents.khataCertificate,
                    draft.documents.encumbrance,
                    draft.documents.sellerIdentity
                ).count { it != null }
                Text("Documents: $docCount")
            }

            ReviewSection(title = "Land Boundary", onEdit = onEditMap) {
                Text("Points marked: ${draft.polygon.size}")
            }

            ReviewSection(title = "Pricing", onEdit = onEditPricing) {
                Text("Type: ${draft.pricing?.priceType ?: "-"}")
                Text("Price: ₹${draft.pricing?.price ?: "-"}")
                Text("Negotiable: ${if (draft.pricing?.negotiable == true) "Yes" else "No"}")
                Text("Availability: ${draft.pricing?.availability ?: "-"}")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.saveDraft(step = 6)
                    if (alreadyPaid) {
                        viewModel.preSubmitCheck { viewModel.submitListing() }
                    } else {
                        onProceedPayment()
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    when {
                        loading -> "Please wait..."
                        alreadyPaid -> "Submit Changes"
                        else -> "Proceed to Payment"
                    }
                )
            }
        }
    }
}

@Composable
fun ReviewSection(
    title: String,
    onEdit: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = onEdit) { Text("Edit") }
            }
            content()
        }
    }
}
