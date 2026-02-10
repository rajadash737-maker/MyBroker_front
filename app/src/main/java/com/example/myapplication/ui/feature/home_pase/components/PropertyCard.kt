package com.example.myapplication.ui.feature.home_pase.components



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun PropertyCard(price: String, location: String) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(120.dp)
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(price, color = Color.Cyan, style = MaterialTheme.typography.titleMedium)
            Text(location, color = Color.White)
        }
    }
}
