package com.example.myapplication.ui.feature.home_pase

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.myapplication.ui.feature.buyer.domain.repository.LandRepository
import com.example.myapplication.ui.feature.home_pase.components.HomeBottomBar
import com.example.myapplication.ui.feature.home_pase.components.HomeContent
import com.example.myapplication.ui.feature.home_pase.components.HomeTopBar
import com.example.myapplication.ui.feature.home_pase.components.LandTypeDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    landRepository: LandRepository // NEW
) {
    var selectedItem by remember { mutableStateOf("Home") }
    var showLandDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            HomeTopBar(
                onNotificationClick = { navController.navigate("notifications") },
                onLikeClick = { navController.navigate("wishlist") },
                onProfileClick = { navController.navigate("profile") },
                onLandClick = { showLandDialog = true }
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
        HomeContent(
            padding = padding,
            navController = navController,
            landRepository = landRepository // NEW
        )

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
