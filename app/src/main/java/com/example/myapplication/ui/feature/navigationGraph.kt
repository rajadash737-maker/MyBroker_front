package com.example.myapplication.ui.feature

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import android.net.Uri
import com.example.myapplication.network.ApiClient
import com.example.myapplication.network.ConnectivityObserver
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
import com.example.myapplication.ui.feature.buyer.visit_request.BuyerLandDetailsScreen
import com.example.myapplication.ui.feature.buyer.visit_request.BuyerLandDetailsViewModel
import com.example.myapplication.ui.feature.buyer.visit_request.BuyerLandDetailsViewModelFactory
import com.example.myapplication.ui.feature.home.profile.EditProfileScreen
import com.example.myapplication.ui.feature.home.profile.ProfileScreen
import com.example.myapplication.ui.feature.home_pase.HomeScreen
import com.example.myapplication.ui.feature.home_pase.HomeViewModel
import com.example.myapplication.ui.feature.home_pase.HomeViewModelFactory
import com.example.myapplication.ui.feature.sell_land.navigation.sellLandGraph
import com.example.myapplication.ui.feature.home.profile.SettingsScreen
import com.example.myapplication.ui.feature.buyer.payment.BookNowScreen
import com.example.myapplication.ui.feature.buyer.payment.BookNowSuccessScreen
import com.example.myapplication.ui.feature.buyer.payment.BookNowViewModel
import com.example.myapplication.ui.feature.buyer.payment.BookNowViewModelFactory
import com.example.myapplication.ui.feature.transactions.data.repository.TransactionRepositoryImpl
import com.example.myapplication.ui.feature.transactions.ui.TransactionDetailScreen
import com.example.myapplication.ui.feature.transactions.ui.TransactionDetailViewModel
import com.example.myapplication.ui.feature.transactions.ui.TransactionDetailViewModelFactory
import com.example.myapplication.ui.feature.transactions.ui.TransactionsScreen
import com.example.myapplication.ui.feature.transactions.ui.TransactionsViewModel
import com.example.myapplication.ui.feature.transactions.ui.TransactionsViewModelFactory
import com.example.myapplication.ui.feature.home.profile.NotificationScreen
import com.example.myapplication.notification.NotificationRoutes
import com.example.myapplication.ui.feature.navigation.Screen
import androidx.navigation.navDeepLink
import com.example.myapplication.ui.feature.entry.OnboardingRoute
import com.example.myapplication.ui.feature.entry.SplashRoute


fun NavGraphBuilder.NavigationGraph(
    navController: NavHostController,
    landRepository: LandRepository,
    wishlistRepository: WishlistRepository,
    connectivityObserver: ConnectivityObserver
) {
    composable("splash") {
        SplashRoute(
            onNavigate = { route ->
                navController.navigate(route) {
                    popUpTo("splash") { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }
    composable("onboarding") {
        OnboardingRoute(
            onComplete = {
                navController.navigate("login") {
                    popUpTo("onboarding") { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }
    composable("login") { Login(navController) }
    composable("signup") { SignupScreenWithOtpUI(navController) }
    composable("forgot") { ForgotPasswordScreen(navController) }

    composable(Screen.Home.route) { backStackEntry ->
        val apiService = ApiClient.apiService
        val homeViewModel: HomeViewModel = viewModel(
            viewModelStoreOwner = backStackEntry,
            factory = HomeViewModelFactory(
                api = apiService,
                landRepository = landRepository,
                connectivityObserver = connectivityObserver
            )
        )

        HomeScreen(
            navController = navController,
            viewModel = homeViewModel
        )
    }

    composable(Screen.Notifications.route) {
        NotificationScreen(
            onNotificationClick = { item ->
                val route = NotificationRoutes.routeForNotification(
                    type = item.type,
                    referenceId = item.referenceId,
                    roleTarget = item.roleTarget
                )
                if (route != null) {
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            }
        )
    }

    sellLandGraph(navController)

    composable(route = "explore") { backStackEntry ->
        val exploreViewModel: ExploreViewModel = viewModel(
            viewModelStoreOwner = backStackEntry,
            factory = ExploreViewModelFactory(
                landRepository = landRepository,
                wishlistRepository = wishlistRepository,
                connectivityObserver = connectivityObserver,
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
            onBookNowClick = { id -> navController.navigate("book_now/$id") },
            onViewMapClick = { id -> navController.navigate("map/$id") }
        )
    }

    composable(
        route = Screen.PropertyDetail.route,
        arguments = listOf(navArgument("id") { type = NavType.StringType }),
        deepLinks = listOf(navDeepLink { uriPattern = "mybroker://property/{id}" })
    ) { backStackEntry ->
        val landId = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0

        val landDetailViewModel: LandDetailViewModel = viewModel(
            viewModelStoreOwner = backStackEntry,
            factory = LandDetailViewModelFactory(landRepository)
        )

        LandDetailScreen(
            landId = landId,
            viewModel = landDetailViewModel,
            onBookNowClick = { id -> navController.navigate("book_now/$id") },
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
                connectivityObserver = connectivityObserver,
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

    composable("transactions") {
        val vm: TransactionsViewModel = viewModel(
            factory = TransactionsViewModelFactory(TransactionRepositoryImpl())
        )
        TransactionsScreen(
            viewModel = vm,
            onTransactionClick = { tx ->
                val id = Uri.encode(tx.transactionId ?: tx.id)
                navController.navigate("transactions/$id")
            }
        )
    }

    composable(
        route = "transactions/{transactionId}",
        arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
    ) { backStackEntry ->
        val transactionId = backStackEntry.arguments?.getString("transactionId").orEmpty()
        val vm: TransactionDetailViewModel = viewModel(
            factory = TransactionDetailViewModelFactory(
                TransactionRepositoryImpl(),
                transactionId
            )
        )
        TransactionDetailScreen(viewModel = vm, onBack = { navController.popBackStack() })
    }

    composable(route = "buyer_lands") { backStackEntry ->
        val buyerLandsViewModel: BuyerLandsViewModel = viewModel(
            viewModelStoreOwner = backStackEntry,
            factory = BuyerLandsViewModelFactory(landRepository)
        )

        BuyerLandsScreen(
            viewModel = buyerLandsViewModel,
            onBookNow = { requestId -> navController.navigate("book_now/$requestId") },
            onViewDetails = { offer ->
                val dealAmount = if (offer.finalAmount > 0.0) offer.finalAmount else offer.offeredAmount
                val bookingStatus = Uri.encode(offer.bookingStatus)
                val registryDate = Uri.encode(offer.registryDate.orEmpty())
                val registryTime = Uri.encode(offer.registryTime.orEmpty())
                val registryLocation = Uri.encode(offer.registryLocation.orEmpty())
                val dealAmountParam = Uri.encode(dealAmount.toString())
                navController.navigate(
                    "buyer_land_details/${offer.requestId}?dealAmount=$dealAmountParam&bookingStatus=$bookingStatus&registryDate=$registryDate&registryTime=$registryTime&registryLocation=$registryLocation"
                )
            }

        )

    }

    composable(
        route = "buyer_land_details/{requestId}?dealAmount={dealAmount}&bookingStatus={bookingStatus}&registryDate={registryDate}&registryTime={registryTime}&registryLocation={registryLocation}",
        arguments = listOf(
            navArgument("requestId") { type = NavType.IntType },
            navArgument("dealAmount") { type = NavType.StringType; defaultValue = "" },
            navArgument("bookingStatus") { type = NavType.StringType; defaultValue = "" },
            navArgument("registryDate") { type = NavType.StringType; defaultValue = "" },
            navArgument("registryTime") { type = NavType.StringType; defaultValue = "" },
            navArgument("registryLocation") { type = NavType.StringType; defaultValue = "" }
        )
    ) { backStackEntry ->
        val requestId = backStackEntry.arguments?.getInt("requestId") ?: 0
        val dealAmount = backStackEntry.arguments?.getString("dealAmount")?.toDoubleOrNull()?.takeIf { it > 0.0 }
        val bookingStatus = backStackEntry.arguments?.getString("bookingStatus").orEmpty()
        val registryDate = backStackEntry.arguments?.getString("registryDate").orEmpty()
        val registryTime = backStackEntry.arguments?.getString("registryTime").orEmpty()
        val registryLocation = backStackEntry.arguments?.getString("registryLocation").orEmpty()
        val landDetailViewModel: BuyerLandDetailsViewModel = viewModel(
            viewModelStoreOwner = backStackEntry,
            factory = BuyerLandDetailsViewModelFactory(landRepository)
        )

        BuyerLandDetailsScreen(
            landId = requestId,
            viewModel = landDetailViewModel,
            onBack = { navController.popBackStack() },
            dealAmount = dealAmount,
            bookingStatus = bookingStatus,
            registryDate = registryDate,
            registryTime = registryTime,
            registryLocation = registryLocation
        )
    }

    composable(
        route = "book_now/{requestId}",
        arguments = listOf(navArgument("requestId") { type = NavType.IntType })
    ) { backStackEntry ->
        val requestId = backStackEntry.arguments?.getInt("requestId") ?: return@composable
        val vm: BookNowViewModel = viewModel(
            viewModelStoreOwner = backStackEntry,
            factory = BookNowViewModelFactory(landRepository, requestId)
        )
        BookNowScreen(
            viewModel = vm,
            requestId = requestId,
            onSuccess = { reqId, bookingId, amount ->
                val safeBookingId = bookingId ?: 0
                val safeAmount = (amount ?: 0.0).toFloat()
                navController.navigate("book_now_success/$reqId/$safeBookingId/$safeAmount")
            },
            onBack = { navController.popBackStack() }
        )
    }

    composable(
        route = "book_now_success/{requestId}/{bookingId}/{amount}",
        arguments = listOf(
            navArgument("requestId") { type = NavType.IntType },
            navArgument("bookingId") { type = NavType.IntType },
            navArgument("amount") { type = NavType.FloatType }
        )
    ) { backStackEntry ->
        val requestId = backStackEntry.arguments?.getInt("requestId") ?: 0
        val bookingId = backStackEntry.arguments?.getInt("bookingId") ?: 0
        val amount = backStackEntry.arguments?.getFloat("amount")?.toDouble() ?: 0.0

        BookNowSuccessScreen(
            requestId = requestId,
            bookingId = bookingId,
            amount = amount,
            onViewDetails = {
                val amountParam = Uri.encode(amount.toString())
                navController.navigate(
                    "buyer_land_details/$requestId?dealAmount=$amountParam&bookingStatus=booked&registryDate=&registryTime=&registryLocation="
                )
            },
            onContactSeller = { navController.navigate("land_detail/$requestId") },
            onGoHome = {
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                    launchSingleTop = true
                }
            }
        )
    }



    //================================================================================

    composable("settings") {
        SettingsScreen(
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
            },
            onMyListedSellerClick = { navController.navigate("seller_lands") },
            onMyListedBuyerClick = { navController.navigate("buyer_lands") },
            onMyBookmarkedClick = { navController.navigate("wishlist") },
            onMapPreferencesClick = { navController.navigate("map") },
            onMyTransactionsClick = { navController.navigate("transactions") },
            onNotificationsClick = { navController.navigate(Screen.Notifications.route) },
            onDarkModeClick = { navController.navigate("settings") },
            onLanguageClick = { navController.navigate("settings") }
        )
    }

    composable("edit_profile") { EditProfileScreen() }
}

