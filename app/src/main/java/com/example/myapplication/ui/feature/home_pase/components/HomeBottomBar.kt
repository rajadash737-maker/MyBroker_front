package com.example.myapplication.ui.feature.home_pase.components



import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Explore

@Composable
fun HomeBottomBar(
    selectedItem: String,
    onSelect: (String) -> Unit,
    navController: NavController
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedItem == "Home",
            onClick = { onSelect("Home") },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = selectedItem == "Explore",
            onClick = {
                onSelect("Explore")
                navController.navigate("explore")
            },
            icon = {
                Icon(Icons.Outlined.Explore, contentDescription = "Explore")
            },
            label = { Text("Explore") }
        )

        NavigationBarItem(
            selected = selectedItem == "Map",
            onClick = {
                onSelect("Map")
                navController.navigate("map")
            },
            icon = { Icon(Icons.Default.Map, null) },
            label = { Text("Map") }
        )
        NavigationBarItem(
            selected = selectedItem == "Settings",
            onClick = {
                onSelect("Settings")
                navController.navigate("settings")
            },
            icon = { Icon(Icons.Default.Settings, null) },
            label = { Text("Settings") }
        )
    }
}
