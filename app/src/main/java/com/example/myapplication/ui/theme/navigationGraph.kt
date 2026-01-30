package com.example.myapplication.ui.theme

//package com.example.myapp.ui.screens

import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.myapplication.ui.theme.auth.ForgotPasswordScreen
//import com.example.myapplication.HomeScreen

import com.example.myapplication.ui.theme.home.profile.ProfileScreen
import com.mybroker.ui.settings.SettingsScreen

import androidx.navigation.NavGraphBuilder
import com.example.myapplication.ui.theme.auth.Login
import com.example.myapplication.ui.theme.auth.SignupScreenWithOtpUI
import com.example.myapplication.ui.theme.home.profile.EditProfileScreen
import com.example.myapplication.ui.theme.home_pase.HomeScreen
import com.example.myapplication.ui.theme.home_pase.components.ExploreScreen


fun NavGraphBuilder.NavigationGraph(navController: NavHostController) {

    composable("login") {
        Login(navController)
    }

    //====================================================

    composable("signup") {
        SignupScreenWithOtpUI(navController)
    }

    //====================================================

    composable("forgot") {
        ForgotPasswordScreen(navController)
    }

    //====================================================

    composable("home") {
        HomeScreen(navController)
    }

    //====================================================

    composable("explore") {
        ExploreScreen(navController)
    }

    //================================================

    composable(route = "map") {

        BuyerMapScreen(
            navController = navController,
            onLocationSelected = { lat, lng ->
                // Save selected location if needed
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("lat", lat)

                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("lng", lng)

                // Go back after selection
                navController.popBackStack()
            }
        )
    }


    //====================================================


    composable("settings") {
        SettingsScreen()
    }

    composable("profile") {
        ProfileScreen(
            onEditClick = {
                navController.navigate("edit_profile")
            },
            onLogoutClick = {
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }

    composable("edit_profile") {
        EditProfileScreen()
    }


//======================================================
//    composable("sell_land") {
//        SellLandScreen(navController)
//    }
//===============================================



}


