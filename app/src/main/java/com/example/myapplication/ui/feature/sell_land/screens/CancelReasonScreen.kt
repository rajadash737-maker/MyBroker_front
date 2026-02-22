package com.example.myapplication.ui.feature.sell_land.screens




import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    var selectedReason by remember { mutableStateOf("") }

    val reasons = listOf(
        "Price Issue",
        "Found Another Buyer",
        "Personal Reason",
        "Document Problem"
    )

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
                        selected = selectedReason == reason,
                        onClick = { selectedReason = reason }
                    )
                    .padding(vertical = 8.dp)
            ) {
                RadioButton(
                    selected = selectedReason == reason,
                    onClick = { selectedReason = reason }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = reason)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.cancelListing(listingId)
                navController.popBackStack()
            },
            enabled = selectedReason.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}