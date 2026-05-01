package com.example.myapplication.ui.feature.home_pase

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.notification.NotificationStore
import com.example.myapplication.ui.feature.home_pase.components.HomeBottomBar
import com.example.myapplication.ui.feature.home_pase.components.HomeContent
import com.example.myapplication.ui.feature.home_pase.components.HomeTopBar
import com.example.myapplication.ui.feature.home_pase.components.LandTypeDialog
import com.example.myapplication.ui.feature.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    var selectedItem by remember { mutableStateOf("Home") }
    var showLandDialog by remember { mutableStateOf(false) }
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val notificationVersion by NotificationStore.version.collectAsState()
    val unreadCount = remember(notificationVersion) {
        NotificationStore.unreadCount(context)
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                onNotificationClick = { navController.navigate(Screen.Notifications.route) },
                onLikeClick = { navController.navigate("wishlist") },
                onProfileClick = { navController.navigate("profile") },
                onLandClick = { showLandDialog = true },
                unreadCount = unreadCount
            )
        },
        bottomBar = {
            HomeBottomBar(
                selectedItem = selectedItem,
                onSelect = { selectedItem = it },
                navController = navController
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                !state.isConnected -> {
                    NoInternetContent(
                        onRetry = { viewModel.onEvent(HomeEvent.OnRetry) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    HomeContent(
                        navController = navController,
                        userName = state.userName,
                        banners = state.banners,
                        nearbyLands = state.nearbyLands,
                        isRefreshing = state.isRefreshing,
                        onRefresh = { viewModel.onEvent(HomeEvent.OnRefresh) }
                    )
                }
            }
        }

        if (showLandDialog) {
            LandTypeDialog(
                onDismiss = { showLandDialog = false },
                onSeller = {
                    showLandDialog = false
                    navController.navigate("seller_lands")
                },
                onBuyer = {
                    showLandDialog = false
                    navController.navigate("buyer_lands")
                }
            )
        }
    }
}

@Composable
private fun NoInternetContent(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "No Internet Connection")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Please check your connection and try again.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
