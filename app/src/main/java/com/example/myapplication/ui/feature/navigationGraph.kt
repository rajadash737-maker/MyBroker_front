package com.example.myapplication.ui.feature



import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.myapplication.ui.feature.auth.ForgotPasswordScreen


import com.example.myapplication.ui.feature.home.profile.ProfileScreen
import com.mybroker.ui.settings.SettingsScreen

import androidx.navigation.NavGraphBuilder
import com.example.myapplication.ui.feature.auth.Login
import com.example.myapplication.ui.feature.auth.SignupScreenWithOtpUI
import com.example.myapplication.ui.feature.home.profile.EditProfileScreen
import com.example.myapplication.ui.feature.home_pase.HomeScreen
import com.example.myapplication.ui.feature.home_pase.components.ExploreScreen
import com.example.myapplication.network.SessionManager


fun NavGraphBuilder.NavigationGraph(navController: NavHostController) {

    composable("login") {
        Login(navController)
    }

    composable("signup") {
        SignupScreenWithOtpUI(navController)
    }

    composable("forgot") {
        ForgotPasswordScreen(navController)
    }

    composable("home") {
        HomeScreen(navController)
    }

    composable("explore") {
        ExploreScreen(navController)
    }

    composable("map") {
        BuyerMapScreen(
            navController = navController,
            onLocationSelected = { lat, lng ->
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("lat", lat)

                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("lng", lng)

                navController.popBackStack()
            }
        )
    }

    composable("settings") {
        SettingsScreen()
    }

    composable("profile") {
        ProfileScreen(
            onEditClick = {
                navController.navigate("edit_profile")
            },
            onLogoutClick = {
                val ctx = navController.context.applicationContext
                val sessionManager = SessionManager(ctx)   // <-- yahan
                sessionManager.clearSession()

                navController.navigate("login") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }


    composable("edit_profile") {
        EditProfileScreen()
    }
}
