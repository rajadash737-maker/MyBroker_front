package com.example.myapplication.ui.feature.sell_land.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.sell_land.status.components.OfferCard
import com.example.myapplication.ui.feature.sell_land.viewmodel.SellLandViewModel

@Composable
fun OfferScreen(
    requestId: Int,
    viewModel: SellLandViewModel
) {
    val offers by viewModel.sellerOffers.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isOfferLocked by viewModel.isOfferLocked.collectAsState()
    val offerLockExpiresAt by viewModel.offerLockExpiresAt.collectAsState()

    LaunchedEffect(requestId) {
        viewModel.loadSellerOffers(requestId)
    }

    when {
        loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        }

        !error.isNullOrBlank() -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = error ?: "Something went wrong",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        offers.isEmpty() -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) { Text("No offers yet") }
        }

        else -> {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                if (isOfferLocked) {
                    item {
                        Text(
                            text = "Listing locked till: ${offerLockExpiresAt ?: "-"}",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }

                items(offers, key = { it.id }) { offer ->
                    OfferCard(
                        offer = offer,
                        onAccept = { viewModel.acceptOffer(requestId, offer.id.toInt()) },
                        onReject = { viewModel.rejectOffer(requestId, offer.id.toInt()) },
                        onCounter = { amount ->
                            viewModel.counterOffer(requestId, offer.id.toInt(), amount)
                        }
                    )
                }
            }
        }
    }
}
