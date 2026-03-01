package com.example.myapplication.ui.feature.buyer.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.buyer.explore.components.FilterBottomSheet
import com.example.myapplication.ui.feature.buyer.explore.components.LandCard

@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onLandClick: (Int) -> Unit = {},
    onMapClick: (Int) -> Unit = {},
    isWishlistScreen: Boolean = false
) {
    val state by viewModel.uiState.collectAsState()

    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Text(
                    text = if (isWishlistScreen) "My Wishlist" else "Explore Lands",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onEvent(ExploreEvent.OnSearchChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    placeholder = {
                        Text(
                            if (isWishlistScreen) "Search liked lands..." else "Search land, city, state, pincode"
                        )
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = { showFilterSheet = true },
                        label = { Text("Filters") },
                        leadingIcon = { Icon(Icons.Default.Tune, contentDescription = null) }
                    )

                    Box {
                        AssistChip(
                            onClick = { showSortMenu = true },
                            label = { Text("Sort") },
                            leadingIcon = { Icon(Icons.Default.Sort, contentDescription = null) }
                        )
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortType.entries.forEach { sortType ->
                                DropdownMenuItem(
                                    text = { Text(sortType.name.replace("_", " ")) },
                                    onClick = {
                                        showSortMenu = false
                                        viewModel.onEvent(ExploreEvent.OnSortChanged(sortType))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.lands.isEmpty() -> {
                    Text(
                        text = state.errorMessage
                            ?: if (isWishlistScreen) "No liked lands yet" else "No Lands Found",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                        items(state.lands, key = { it.id }) { land ->
                            LandCard(
                                land = land,
                                onClick = { onLandClick(land.id) },
                                onWishlistClick = {
                                    viewModel.onEvent(ExploreEvent.OnWishlistClick(land.id))
                                },
                                onMakeOfferClick = {
                                    if (!land.isOfferLocked) {
                                        onLandClick(land.id)
                                    }
                                },
                                onMapClick = { onMapClick(land.id) }
                            )

                        }

                        item {
                            if (state.hasMore && !state.isLoading && !state.isLoadingMore) {
                                LaunchedEffect(state.lands.size) {
                                    viewModel.onEvent(ExploreEvent.OnLoadMore)
                                }
                            }

                            if (state.isLoadingMore) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }

            if (showFilterSheet) {
                FilterBottomSheet(
                    initialFilter = state.currentFilter ?: LandFilter(),
                    onApply = { filter ->
                        showFilterSheet = false
                        viewModel.onEvent(ExploreEvent.OnFilterApplied(filter))
                    },
                    onDismiss = { showFilterSheet = false }
                )
            }
        }
    }
}
