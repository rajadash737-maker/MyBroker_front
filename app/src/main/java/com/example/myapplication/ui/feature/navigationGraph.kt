package com.example.myapplication.ui.feature

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.network.SessionManager
import com.example.myapplication.ui.feature.auth.ForgotPasswordScreen
import com.example.myapplication.ui.feature.auth.Login
import com.example.myapplication.ui.feature.auth.SignupScreenWithOtpUI
import com.example.myapplication.ui.feature.buyer.domain.repository.LandRepository
import com.example.myapplication.ui.feature.buyer.domain.repository.WishlistRepository
import com.example.myapplication.ui.feature.buyer.explore.ExploreScreen
import com.example.myapplication.ui.feature.buyer.explore.ExploreViewModel
import com.example.myapplication.ui.feature.buyer.explore.ExploreViewModelFactory
import com.example.myapplication.ui.feature.buyer.land_detail.LandDetailScreen
import com.example.myapplication.ui.feature.buyer.land_detail.LandDetailViewModel
import com.example.myapplication.ui.feature.buyer.land_detail.LandDetailViewModelFactory
import com.example.myapplication.ui.feature.buyer.map.MapScreen
import com.example.myapplication.ui.feature.buyer.map.MapViewModel
import com.example.myapplication.ui.feature.buyer.map.MapViewModelFactory
import com.example.myapplication.ui.feature.buyer.visit_request.BuyerLandsScreen
import com.example.myapplication.ui.feature.buyer.visit_request.BuyerLandsViewModel
import com.example.myapplication.ui.feature.buyer.visit_request.BuyerLandsViewModelFactory
import com.example.myapplication.ui.feature.home.profile.EditProfileScreen
import com.example.myapplication.ui.feature.home.profile.ProfileScreen
import com.example.myapplication.ui.feature.home_pase.HomeScreen
import com.example.myapplication.ui.feature.sell_land.navigation.sellLandGraph
import com.mybroker.ui.settings.SettingsScreen

fun NavGraphBuilder.NavigationGraph(
    navController: NavHostController,
    landRepository: LandRepository,
    wishlistRepository: WishlistRepository
) {
    composable("login") { Login(navController) }
    composable("signup") { SignupScreenWithOtpUI(navController) }
    composable("forgot") { ForgotPasswordScreen(navController) }

    composable("home") {
        HomeScreen(
            navController = navController,
            landRepository = landRepository
        )
    }

    sellLandGraph(navController)

    composable(route = "explore") { backStackEntry ->
        val exploreViewModel: ExploreViewModel = viewModel(
            viewModelStoreOwner = backStackEntry,
            factory = ExploreViewModelFactory(
                landRepository = landRepository,
                wishlistRepository = wishlistRepository,
                showWishlistOnly = false
            )
        )

        ExploreScreen(
            viewModel = exploreViewModel,
            onLandClick = { id -> navController.navigate("land_detail/$id") },
            onMapClick = { id -> navController.navigate("map/$id") },
            isWishlistScreen = false
        )
    }

    composable(
        route = "land_detail/{landId}",
        arguments = listOf(navArgument("landId") { type = NavType.IntType })
    ) { backStackEntry ->
        val landId = backStackEntry.arguments?.getInt("landId") ?: 0

        val landDetailViewModel: LandDetailViewModel = viewModel(
            viewModelStoreOwner = backStackEntry,
            factory = LandDetailViewModelFactory(landRepository)
        )

        LandDetailScreen(
            landId = landId,
            viewModel = landDetailViewModel,
            onBookNowClick = { /* TODO */ },
            onViewMapClick = { id -> navController.navigate("map/$id") }
        )
    }

    composable(
        route = "map/{focusLandId}",
        arguments = listOf(navArgument("focusLandId") {
            type = NavType.IntType
            defaultValue = -1
        })
    ) { backStackEntry ->
        val focusLandId = backStackEntry.arguments?.getInt("focusLandId")?.takeIf { it > 0 }

        val mapViewModel: MapViewModel = viewModel(
            viewModelStoreOwner = backStackEntry,
            factory = MapViewModelFactory(landRepository)
        )

        MapScreen(
            viewModel = mapViewModel,
            focusLandId = focusLandId,
            onLandClick = { id -> navController.navigate("land_detail/$id") }
        )
    }

    composable("map") { backStackEntry ->
        val mapViewModel: MapViewModel = viewModel(
            viewModelStoreOwner = backStackEntry,
            factory = MapViewModelFactory(landRepository)
        )

        MapScreen(
            viewModel = mapViewModel,
            focusLandId = null,
            onLandClick = { id -> navController.navigate("land_detail/$id") }
        )
    }

    composable(route = "wishlist") { backStackEntry ->
        val exploreViewModel: ExploreViewModel = viewModel(
            viewModelStoreOwner = backStackEntry,
            factory = ExploreViewModelFactory(
                landRepository = landRepository,
                wishlistRepository = wishlistRepository,
                showWishlistOnly = true
            )
        )

        ExploreScreen(
            viewModel = exploreViewModel,
            onLandClick = { id -> navController.navigate("land_detail/$id") },
            onMapClick = { id -> navController.navigate("map/$id") },
            isWishlistScreen = true
        )
    }

    composable(route = "buyer_lands") { backStackEntry ->
        val buyerLandsViewModel: BuyerLandsViewModel = viewModel(
            viewModelStoreOwner = backStackEntry,
            factory = BuyerLandsViewModelFactory(landRepository)
        )

        BuyerLandsScreen(
            viewModel = buyerLandsViewModel,
            onBookNow = { requestId -> navController.navigate("land_detail/$requestId") }
        )

    }

    composable("settings") { SettingsScreen() }

    composable("profile") {
        ProfileScreen(
            onEditClick = { navController.navigate("edit_profile") },
            onLogoutClick = {
                val ctx = navController.context.applicationContext
                val sessionManager = SessionManager(ctx)
                sessionManager.clearSession()
                navController.navigate("login") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }

    composable("edit_profile") { EditProfileScreen() }
}
