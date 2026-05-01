package com.example.myapplication.ui.feature.sell_land.screens



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SuccessScreen(
    listingCode: String?,
    listingId: Int?,
    onGoHome: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "🎉 Listing Submitted!",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your land has been submitted successfully.\nOur team will review it shortly.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Listing ID: ${listingId?.let { "LST-$it" } ?: listingCode ?: "Pending"}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onGoHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go to Home")
            }
        }
    }
}
