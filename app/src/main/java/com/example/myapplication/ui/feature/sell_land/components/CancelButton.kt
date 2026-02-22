package com.example.myapplication.ui.feature.sell_land.components




import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable

@Composable
fun CancelButton(onClick: () -> Unit) {
    Text(
        text = "Cancel Listing",
        color = Color.Red,
        modifier = Modifier.clickable { onClick() }
    )
}