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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.feature.buyer.visit_request.components.BuyerOfferCard
import com.example.myapplication.ui.feature.common.NoInternetContent
import com.example.myapplication.ui.feature.common.rememberIsConnected

@Composable
fun BuyerLandsScreen(
    viewModel: BuyerLandsViewModel,
    onBookNow: (Int) -> Unit = {},
    onViewDetails: (com.example.myapplication.ui.feature.buyer.domain.model.BuyerOffer) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val isConnected by rememberIsConnected()

    var showCounterDialog by remember { mutableStateOf(false) }
    var counterRequestId by remember { mutableIntStateOf(-1) }
    var counterAmountInput by remember { mutableStateOf("") }

    if (!isConnected) {
        NoInternetContent(
            onRetry = { viewModel.loadBuyerOffers() },
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    LaunchedEffect(uiState.errorMessage, uiState.actionMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
        uiState.actionMessage?.let { snackbarHostState.showSnackbar(it) }
        if (uiState.errorMessage != null) viewModel.clearError()
        if (uiState.actionMessage != null) viewModel.clearActionMessage()
    }

    if (showCounterDialog) {
        AlertDialog(
            onDismissRequest = {
                showCounterDialog = false
                counterAmountInput = ""
                counterRequestId = -1
            },
            title = { Text(stringResource(id = R.string.buyer_counter_again)) },
            text = {
                OutlinedTextField(
                    value = counterAmountInput,
                    onValueChange = { input ->
                        counterAmountInput = input.filter { ch -> ch.isDigit() || ch == '.' }
                    },
                    label = { Text(stringResource(id = R.string.buyer_enter_new_amount)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.counterAgain(
                            requestId = counterRequestId,
                            amountText = counterAmountInput
                        )
                        showCounterDialog = false
                        counterAmountInput = ""
                        counterRequestId = -1
                    }
                ) {
                    Text(stringResource(id = R.string.common_send))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCounterDialog = false
                        counterAmountInput = ""
                        counterRequestId = -1
                    }
                ) {
                    Text(stringResource(id = R.string.common_cancel))
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Text(
            text = stringResource(id = R.string.buyer_lands_title),
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
                    text = stringResource(id = R.string.buyer_no_offers_found),
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
                        onCounterAgain = { requestId ->
                            counterRequestId = requestId
                            counterAmountInput = ""
                            showCounterDialog = true
                        },
                        onBookNow = onBookNow,
                        onViewDetails = onViewDetails
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
        BuyerOfferFilter.BOOKED,
        BuyerOfferFilter.REJECTED,
        BuyerOfferFilter.EXPIRED
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            FilterChip(
                selected = selected == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filterLabel(filter)) }
            )
        }
    }
}

@Composable
private fun filterLabel(filter: BuyerOfferFilter): String {
    return when (filter) {
        BuyerOfferFilter.ALL -> stringResource(id = R.string.filter_all)
        BuyerOfferFilter.OFFER_SENT -> stringResource(id = R.string.filter_offer_sent)
        BuyerOfferFilter.COUNTER_RECEIVED -> stringResource(id = R.string.filter_counter_received)
        BuyerOfferFilter.ACCEPTED_BOOK_NOW -> stringResource(id = R.string.filter_accepted_book_now)
        BuyerOfferFilter.BOOKED -> stringResource(id = R.string.filter_booked)
        BuyerOfferFilter.REJECTED -> stringResource(id = R.string.filter_rejected)
        BuyerOfferFilter.EXPIRED -> stringResource(id = R.string.filter_expired)
    }
}
