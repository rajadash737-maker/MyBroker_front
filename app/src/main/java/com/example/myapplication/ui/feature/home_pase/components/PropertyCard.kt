package com.example.myapplication.ui.feature.home_pase.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.R

@Composable
fun PropertyCard(
    imageUrl: String?,
    price: String,
    area: String,
    district: String,
    state: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(210.dp)
            .height(220.dp)
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = imageUrl ?: R.drawable.banner1 // changed fallback
                ),
                contentDescription = "Land Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                    .background(Color(0xFF2A2A2A))
            )

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = price,
                    color = Color(0xFF00E5FF),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Area: $area",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "$district, $state",
                    color = Color(0xFFD0D0D0),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
