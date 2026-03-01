package com.example.myapplication.ui.feature.buyer.visit_request

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.buyer.visit_request.components.BuyerOfferCard

@Composable
fun BuyerLandsScreen(
    viewModel: BuyerLandsViewModel,
    onBookNow: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage, uiState.actionMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
        uiState.actionMessage?.let { snackbarHostState.showSnackbar(it) }
        if (uiState.errorMessage != null) viewModel.clearError()
        if (uiState.actionMessage != null) viewModel.clearActionMessage()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F1F8))
            .statusBarsPadding()
    ) {
        Text(
            text = "Buyer Lands",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        FilterRow(
            selected = uiState.selectedFilter,
            onFilterSelected = viewModel::onFilterChange
        )

        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.filteredOffers.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No offers found",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = uiState.filteredOffers,
                    key = { it.offerId }
                ) { offer ->
                    BuyerOfferCard(
                        offer = offer,
                        onAcceptCounter = viewModel::acceptCounterOffer,
                        onRejectCounter = viewModel::rejectCounterOffer,
                        onBookNow = onBookNow
                    )
                }
            }
        }

        SnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
private fun FilterRow(
    selected: BuyerOfferFilter,
    onFilterSelected: (BuyerOfferFilter) -> Unit
) {
    val filters = listOf(
        BuyerOfferFilter.ALL,
        BuyerOfferFilter.OFFER_SENT,
        BuyerOfferFilter.COUNTER_RECEIVED,
        BuyerOfferFilter.ACCEPTED_BOOK_NOW,
        BuyerOfferFilter.REJECTED,
        BuyerOfferFilter.EXPIRED
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .background(Color(0xFFE8E1F2), RoundedCornerShape(14.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            FilterChip(
                selected = selected == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter.name.replace("_", " ")) }
            )
        }
    }
}
