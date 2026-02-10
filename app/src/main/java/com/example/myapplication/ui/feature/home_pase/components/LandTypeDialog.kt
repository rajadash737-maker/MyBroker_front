package com.example.myapplication.ui.feature.home_pase.components



import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*



@Composable
fun LandTypeDialog(
    onDismiss: () -> Unit,
    onSeller: () -> Unit,
    onBuyer: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Land Type") },
        text = {
            Column {
                TextButton(onClick = onSeller) {
                    Icon(Icons.Default.Store, null)
                    Text(" Seller Lands")
                }
                TextButton(onClick = onBuyer) {
                    Icon(Icons.Default.ShoppingCart, null)
                    Text(" Buyer Lands")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
