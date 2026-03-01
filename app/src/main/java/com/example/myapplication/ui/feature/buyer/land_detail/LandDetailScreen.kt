package com.example.myapplication.ui.feature.buyer.land_detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.buyer.land_detail.components.BottomActionBar
import com.example.myapplication.ui.feature.buyer.land_detail.components.ImageSlider
import com.example.myapplication.ui.feature.buyer.land_detail.components.LandInfoSection
import com.example.myapplication.ui.feature.buyer.land_detail.components.MapPreviewSection

@Composable
fun LandDetailScreen(
    landId: Int,
    viewModel: LandDetailViewModel,
    onBookNowClick: (Int) -> Unit = {},
    onViewMapClick: (Int) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var showOfferDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(landId) {
        viewModel.loadLand(landId)
    }

    LaunchedEffect(state.offerSuccessMessage, state.errorMessage) {
        val msg = state.offerSuccessMessage ?: state.errorMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            if (state.offerSuccessMessage != null) showOfferDialog = false
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            state.land?.let { land ->
                BottomActionBar(
                    isOfferLocked = land.isOfferLocked,
                    offerLockExpiresAt = land.offerLockExpiresAt,
                    onBookClick = { onBookNowClick(land.id) },
                    onOfferClick = {
                        if (!land.isOfferLocked) {
                            showOfferDialog = true
                        }
                    }
                )
            }
        }
    ) { padding ->

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.land?.let { land ->
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item { ImageSlider(land.images) }
                item { LandInfoSection(land = land) }
                item {
                    MapPreviewSection(
                        land = land,
                        onOpenMapClick = { onViewMapClick(land.id) }
                    )
                }
            }
        }
    }

    if (showOfferDialog && state.land?.isOfferLocked != true) {
        AlertDialog(
            onDismissRequest = { showOfferDialog = false },
            title = { Text("Make Offer") },
            text = {
                OutlinedTextField(
                    value = state.offerInput,
                    onValueChange = viewModel::onOfferInputChanged,
                    label = { Text("Enter Amount") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.submitOffer() },
                    enabled = !state.isSubmittingOffer
                ) {
                    Text(if (state.isSubmittingOffer) "Sending..." else "Send")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOfferDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

}
