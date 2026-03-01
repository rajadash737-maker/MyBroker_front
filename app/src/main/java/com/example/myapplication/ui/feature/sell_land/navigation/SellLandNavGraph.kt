// SellLandNavGraph.kt
package com.example.myapplication.ui.feature.sell_land.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.myapplication.ui.feature.sell_land.screens.CancelReasonScreen
import com.example.myapplication.ui.feature.sell_land.screens.LandDetailsScreen
import com.example.myapplication.ui.feature.sell_land.screens.MapBoundaryScreen
import com.example.myapplication.ui.feature.sell_land.screens.OfferScreen
import com.example.myapplication.ui.feature.sell_land.screens.PaymentScreen
import com.example.myapplication.ui.feature.sell_land.screens.PricingScreen
import com.example.myapplication.ui.feature.sell_land.screens.ReviewScreen
import com.example.myapplication.ui.feature.sell_land.screens.SellerDetailsScreen
import com.example.myapplication.ui.feature.sell_land.screens.SellerListingsScreen
import com.example.myapplication.ui.feature.sell_land.screens.SuccessScreen
import com.example.myapplication.ui.feature.sell_land.screens.UploadMediaScreen
import com.example.myapplication.ui.feature.sell_land.viewmodel.SellLandViewModel

object SellLandRoutes {
    const val SELLER = "seller_details"
    const val SELLER_WITH_ID = "seller_details/{listingId}"
    const val LAND = "land_details"
    const val MEDIA = "upload_media"
    const val MAP = "map_boundary"
    const val PRICING = "pricing"
    const val REVIEW = "review"
    const val PAYMENT = "payment"
    const val SUCCESS = "success"

    fun sellerWithIdRoute(listingId: String) = "seller_details/$listingId"
}

fun NavGraphBuilder.sellLandGraph(
    navController: NavHostController
) {
    navigation(
        route = "sell_land_graph",
        startDestination = SellLandRoutes.SELLER
    ) {
        composable(SellLandRoutes.SELLER) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("sell_land_graph")
            }
            val vm: SellLandViewModel = viewModel(parentEntry)

            SellerDetailsScreen(
                viewModel = vm,
                onContinue = { navController.navigate(SellLandRoutes.LAND) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = SellLandRoutes.SELLER_WITH_ID,
            arguments = listOf(navArgument("listingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("sell_land_graph")
            }
            val vm: SellLandViewModel = viewModel(parentEntry)

            val listingId = backStackEntry.arguments?.getString("listingId")?.toIntOrNull()
            LaunchedEffect(listingId) {
                listingId?.let { vm.loadListingForEdit(it) }
            }

            SellerDetailsScreen(
                viewModel = vm,
                onContinue = { navController.navigate(SellLandRoutes.LAND) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(SellLandRoutes.LAND) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("sell_land_graph")
            }
            val vm: SellLandViewModel = viewModel(parentEntry)

            LandDetailsScreen(
                viewModel = vm,
                onContinue = { navController.navigate(SellLandRoutes.MEDIA) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(SellLandRoutes.MEDIA) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("sell_land_graph")
            }
            val vm: SellLandViewModel = viewModel(parentEntry)

            UploadMediaScreen(
                viewModel = vm,
                onContinue = { navController.navigate(SellLandRoutes.MAP) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(SellLandRoutes.MAP) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("sell_land_graph")
            }
            val vm: SellLandViewModel = viewModel(parentEntry)

            MapBoundaryScreen(
                viewModel = vm,
                onContinue = { navController.navigate(SellLandRoutes.PRICING) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(SellLandRoutes.PRICING) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("sell_land_graph")
            }
            val vm: SellLandViewModel = viewModel(parentEntry)

            PricingScreen(
                viewModel = vm,
                onContinue = { navController.navigate(SellLandRoutes.REVIEW) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(SellLandRoutes.REVIEW) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("sell_land_graph")
            }
            val vm: SellLandViewModel = viewModel(parentEntry)

            ReviewScreen(
                viewModel = vm,
                onEditSeller = { navController.navigate(SellLandRoutes.SELLER) },
                onEditLand = { navController.navigate(SellLandRoutes.LAND) },
                onEditMedia = { navController.navigate(SellLandRoutes.MEDIA) },
                onEditMap = { navController.navigate(SellLandRoutes.MAP) },
                onEditPricing = { navController.navigate(SellLandRoutes.PRICING) },
                onProceedPayment = { navController.navigate(SellLandRoutes.PAYMENT) },
                onSubmitSuccess = { navController.navigate(SellLandRoutes.SUCCESS) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(SellLandRoutes.PAYMENT) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("sell_land_graph")
            }
            val vm: SellLandViewModel = viewModel(parentEntry)

            PaymentScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onSubmitSuccess = { navController.navigate(SellLandRoutes.SUCCESS) }
            )
        }

        composable(route = SellLandRoutes.SUCCESS) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("sell_land_graph")
            }
            val vm: SellLandViewModel = viewModel(parentEntry)
            val submitResult by vm.submitResult.collectAsState()

            SuccessScreen(
                listingCode = submitResult?.listingCode,
                onGoHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                    vm.clearSubmitResult()
                    vm.clearDraft()
                }
            )
        }
    }

    composable(route = "seller_lands") {
        val vm: SellLandViewModel = viewModel()
        SellerListingsScreen(
            navController = navController,
            viewModel = vm
        )
    }

    composable(
        route = "cancel_reason/{id}",
        arguments = listOf(navArgument("id") { type = NavType.StringType })
    ) { backStackEntry ->
        val id = backStackEntry.arguments?.getString("id") ?: ""
        val vm: SellLandViewModel = viewModel()

        CancelReasonScreen(
            listingId = id,
            navController = navController,
            viewModel = vm
        )
    }

    composable(
        route = "offers/{listingId}",
        arguments = listOf(navArgument("listingId") { type = NavType.IntType })
    ) { backStackEntry ->
        val listingId = backStackEntry.arguments?.getInt("listingId") ?: 0
        val vm: SellLandViewModel = viewModel()

        OfferScreen(
            requestId = listingId,
            viewModel = vm
        )
    }
}
