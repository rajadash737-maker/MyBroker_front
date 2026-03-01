package com.example.myapplication.ui.feature.home_pase.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun HomeBottomBar(
    selectedItem: String,
    onSelect: (String) -> Unit,
    navController: NavController
) {
    fun navigateSingleTop(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
        }
    }

    NavigationBar {
        NavigationBarItem(
            selected = selectedItem == "Home",
            onClick = {
                onSelect("Home")
                navigateSingleTop("home")
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = selectedItem == "Explore",
            onClick = {
                onSelect("Explore")
                navigateSingleTop("explore")
            },
            icon = { Icon(Icons.Outlined.Explore, contentDescription = "Explore") },
            label = { Text("Explore") }
        )

        NavigationBarItem(
            selected = selectedItem == "Map",
            onClick = {
                onSelect("Map")
                navigateSingleTop("map")
            },
            icon = { Icon(Icons.Default.Map, contentDescription = "Map") },
            label = { Text("Map") }
        )

        NavigationBarItem(
            selected = selectedItem == "Settings",
            onClick = {
                onSelect("Settings")
                navigateSingleTop("settings")
            },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") }
        )
    }
}
