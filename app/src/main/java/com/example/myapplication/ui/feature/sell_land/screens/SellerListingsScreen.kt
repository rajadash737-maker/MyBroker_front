package com.example.myapplication.ui.feature.sell_land.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.navigation.NavController
import com.example.myapplication.ui.feature.common.NoInternetContent
import com.example.myapplication.ui.feature.common.rememberIsConnected
import com.example.myapplication.ui.feature.sell_land.navigation.SellLandRoutes
import com.example.myapplication.ui.feature.sell_land.status.ListingStatus
import com.example.myapplication.ui.feature.sell_land.status.components.ListingCard
import com.example.myapplication.ui.feature.sell_land.viewmodel.SellLandViewModel

@Composable
fun SellerListingsScreen(
    navController: NavController,
    viewModel: SellLandViewModel
) {
    val listings by viewModel.statusListings.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isConnected by rememberIsConnected()

    if (!isConnected) {
        NoInternetContent(
            onRetry = { viewModel.loadStatusListings() },
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    LaunchedEffect(Unit) {
        viewModel.loadStatusListings()
    }

    when {
        loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
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

        listings.isEmpty() -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No listings found")
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(listings, key = { it.id }) { listing ->
                    ListingCard(
                        listing = listing,
                        onViewOffers = { navController.navigate("offers/${listing.id}") },
                        onEdit = {
                            if (listing.id.isNotBlank()) {
                                navController.navigate(SellLandRoutes.sellerWithIdRoute(listing.id))
                            }
                        },
                        onCancel = {
                            val isPaymentSuccess = listing.paymentStatus.equals("success", ignoreCase = true)
                            val canNavigateToCancel = listing.canCancelListing &&
                                isPaymentSuccess &&
                                listing.status !in setOf(
                                    ListingStatus.DRAFT,
                                    ListingStatus.BOOKED,
                                    ListingStatus.SOLD,
                                    ListingStatus.PROCESSING_REFUND,
                                    ListingStatus.REFUND_SUCCESSFUL
                                )
                            if (canNavigateToCancel) {
                                navController.navigate("cancel_reason/${listing.id}")
                            }
                        },
                        onViewMore = {
                            navController.navigate(SellLandRoutes.sellerListingDetailRoute(listing.id))
                        }
                    )
                }
            }
        }
    }
}
