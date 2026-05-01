package com.example.myapplication.ui.feature.home_pase.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

data class HomeLandItem(
    val id: Int,
    val imageUrl: String?,
    val price: String,
    val area: String,
    val district: String,
    val state: String
)

@Composable
fun PropertyList(
    lands: List<HomeLandItem>,
    onViewMoreClick: () -> Unit,
    onCardClick: (Int) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.home_nearby_lands),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(id = R.string.common_view_more),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onViewMoreClick() }
            )
        }

        LazyRow(
            modifier = Modifier.padding(start = 16.dp),
            contentPadding = PaddingValues(end = 16.dp)
        ) {
            items(lands, key = { it.id }) { land ->
                PropertyCard(
                    imageUrl = land.imageUrl,
                    price = land.price,
                    area = land.area,
                    district = land.district,
                    state = land.state,
                    onClick = { onCardClick(land.id) }
                )
            }
        }
    }
}
