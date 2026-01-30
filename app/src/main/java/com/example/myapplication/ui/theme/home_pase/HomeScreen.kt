package com.example.myapplication.ui.theme.home_pase


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.myapplication.ui.theme.home_pase.components.HomeBottomBar
import com.example.myapplication.ui.theme.home_pase.components.HomeTopBar
import com.example.myapplication.ui.theme.home_pase.components.LandTypeDialog
import com.example.myapplication.ui.theme.home_pase.components.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    var selectedItem by remember { mutableStateOf("Home") }
    var showLandDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            HomeTopBar(
                onNotificationClick = { navController.navigate("notifications") },
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
            navController = navController
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


