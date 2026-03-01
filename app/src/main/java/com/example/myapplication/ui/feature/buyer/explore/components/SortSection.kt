package com.example.myapplication.ui.feature.buyer.explore.components




import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.buyer.explore.SortType

@Composable
fun SortSection(
    selectedSort: SortType,
    onSortSelected: (SortType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SortType.values().forEach { sort ->
            TextButton(
                onClick = { onSortSelected(sort) }
            ) {
                Text(
                    text = sort.name,
                    color = if (sort == selectedSort)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}