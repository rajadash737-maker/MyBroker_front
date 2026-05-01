package com.example.myapplication.ui.feature.home_pase.components



import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.res.stringResource
import com.example.myapplication.R



@Composable
fun LandTypeDialog(
    onDismiss: () -> Unit,
    onSeller: () -> Unit,
    onBuyer: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.home_choose_land_type)) },
        text = {
            Column {
                TextButton(onClick = onSeller) {
                    Icon(Icons.Default.Store, null)
                    Text(stringResource(id = R.string.home_seller_lands))
                }
                TextButton(onClick = onBuyer) {
                    Icon(Icons.Default.ShoppingCart, null)
                    Text(stringResource(id = R.string.home_buyer_lands))
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.common_cancel))
            }
        }
    )
}
