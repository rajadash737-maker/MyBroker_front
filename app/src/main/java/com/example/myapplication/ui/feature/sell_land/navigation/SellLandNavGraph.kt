package com.example.myapplication.ui.feature.sell_land.navigation





import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation
import com.example.myapplication.ui.feature.sell_land.screens.*
import com.example.myapplication.ui.feature.sell_land.viewmodel.SellLandViewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue



object SellLandRoutes {
    const val SELLER = "seller_details"
    const val LAND = "land_details"
    const val MEDIA = "upload_media"
    const val MAP = "map_boundary"
    const val PRICING = "pricing"
    const val REVIEW = "review"
    const val PAYMENT = "payment"
    const val SUCCESS = "success"
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
            val viewModel: SellLandViewModel = viewModel(parentEntry)

            SellerDetailsScreen(
                viewModel = viewModel,
                onContinue = {
                    navController.navigate(SellLandRoutes.LAND)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(SellLandRoutes.LAND) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("sell_land_graph")
            }
            val viewModel: SellLandViewModel = viewModel(parentEntry)

            LandDetailsScreen(
                viewModel = viewModel,
                onContinue = {
                    navController.navigate(SellLandRoutes.MEDIA)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(SellLandRoutes.MEDIA) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("sell_land_graph")
            }
            val viewModel: SellLandViewModel = viewModel(parentEntry)

            UploadMediaScreen(
                viewModel = viewModel,
                onContinue = {
                    navController.navigate(SellLandRoutes.MAP)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(SellLandRoutes.MAP) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("sell_land_graph")
            }
            val viewModel: SellLandViewModel = viewModel(parentEntry)

            MapBoundaryScreen(
                viewModel = viewModel,
                onContinue = {
                    navController.navigate(SellLandRoutes.PRICING)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(SellLandRoutes.PRICING) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("sell_land_graph")
            }
            val viewModel: SellLandViewModel = viewModel(parentEntry)

            PricingScreen(
                viewModel = viewModel,
                onContinue = {
                    navController.navigate(SellLandRoutes.REVIEW)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(SellLandRoutes.REVIEW) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("sell_land_graph")
            }
            val viewModel: SellLandViewModel = viewModel(parentEntry)

            ReviewScreen(
                viewModel = viewModel,
                onEditSeller = { navController.navigate(SellLandRoutes.SELLER) },
                onEditLand = { navController.navigate(SellLandRoutes.LAND) },
                onEditMedia = { navController.navigate(SellLandRoutes.MEDIA) },
                onEditMap = { navController.navigate(SellLandRoutes.MAP) },
                onEditPricing = { navController.navigate(SellLandRoutes.PRICING) },
                onProceedPayment = {
                    navController.navigate(SellLandRoutes.PAYMENT)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(SellLandRoutes.PAYMENT) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("sell_land_graph")
            }
            val viewModel: SellLandViewModel = viewModel(parentEntry)

            PaymentScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSubmitSuccess = {
                    navController.navigate(SellLandRoutes.SUCCESS)
                }
            )
        }

        composable(route = SellLandRoutes.SUCCESS) {
            val sellLandViewModel: SellLandViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            val submitResult by sellLandViewModel.submitResult.collectAsState()

            SuccessScreen(
                listingCode = submitResult?.listingCode,
                onGoHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

    }
}
