package com.example.myapplication.ui.feature.sell_land.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.sell_land.components.SellLandTopBar
import com.example.myapplication.ui.feature.sell_land.viewmodel.SellLandViewModel
import java.text.DecimalFormat

private fun formatPrice(value: Double?): String {
    if (value == null || value <= 0.0) return "-"
    return DecimalFormat("#,##0.##").format(value)
}

private fun displayPriceType(value: String?): String {
    return when (value?.trim()?.lowercase()) {
        "price_per_unit" -> "Price per Sq.ft"
        "total_price" -> "Total Price"
        else -> value ?: "-"
    }
}

private fun displayAvailability(value: String?): String {
    return when (value?.trim()?.lowercase()) {
        "within_3_months" -> "Within 3 Months"
        "immediate" -> "Immediate"
        else -> value ?: "-"
    }
}

private fun countDocuments(
    dynamicDocs: Map<String, String>,
    saleDeed: String?,
    khata: String?,
    encumbrance: String?,
    sellerIdentity: String?
): Int {
    val dynamicCount = dynamicDocs.values.count { it.isNotBlank() }
    if (dynamicCount > 0) return dynamicCount

    return listOf(saleDeed, khata, encumbrance, sellerIdentity)
        .count { !it.isNullOrBlank() }
}

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
        if (submitResult != null) onSubmitSuccess()
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
                Text(draft.sellerDetails?.sellerCategory ?: "-")
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

                val docCount = countDocuments(
                    dynamicDocs = draft.documents.dynamicDocs,
                    saleDeed = draft.documents.saleDeed,
                    khata = draft.documents.khataCertificate,
                    encumbrance = draft.documents.encumbrance,
                    sellerIdentity = draft.documents.sellerIdentity
                )
                Text("Documents: $docCount")
            }

            ReviewSection(title = "Land Boundary", onEdit = onEditMap) {
                Text("Points marked: ${draft.polygon.size}")
            }

            ReviewSection(title = "Pricing", onEdit = onEditPricing) {
                Text("Type: ${displayPriceType(draft.pricing?.priceType)}")
                Text("Price: ₹${formatPrice(draft.pricing?.price)}")
                Text("Negotiable: ${if (draft.pricing?.negotiable == true) "Yes" else "No"}")
                Text("Availability: ${displayAvailability(draft.pricing?.availability)}")
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
