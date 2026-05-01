package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.network.ApiClient
import com.example.myapplication.network.NetworkConnectivityObserver
import com.example.myapplication.network.SessionManager
import com.example.myapplication.ui.feature.NavigationGraph
import com.example.myapplication.ui.feature.MyApplicationTheme
import com.example.myapplication.ui.feature.buyer.data.remote.LandApi
import com.example.myapplication.ui.feature.buyer.data.repository.LandRepositoryImpl
import com.example.myapplication.ui.feature.buyer.data.repository.WishlistRepositoryImpl
import com.example.myapplication.ui.feature.buyer.payment.RazorpayPaymentBus
import com.example.myapplication.ui.feature.buyer.payment.RazorpayPaymentResult
import com.example.myapplication.ui.feature.common.LocalConnectivityObserver
import com.example.myapplication.ui.feature.home.profile.LocalThemeViewModel
import com.example.myapplication.ui.feature.home.profile.ThemeRepository
import com.example.myapplication.ui.feature.home.profile.ThemeViewModel
import com.example.myapplication.ui.feature.home.profile.ThemeViewModelFactory
import com.example.myapplication.ui.feature.home.profile.themeDataStore
import com.example.myapplication.notification.NotificationData
import com.example.myapplication.notification.NotificationRoutes
import com.example.myapplication.notification.NotificationSync
import com.example.myapplication.notification.FcmTokenManager
import android.util.Log
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.ui.feature.home.profile.LanguagePreferences
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity(), PaymentResultWithDataListener {
    private var notificationData by mutableStateOf<NotificationData?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        runBlocking {
            LanguagePreferences.applySavedLanguage(applicationContext)
        }
        super.onCreate(savedInstanceState)
        requestNotificationPermissionIfNeeded()
        enableEdgeToEdge()
        ApiClient.init(applicationContext)
        Checkout.preload(applicationContext)
        FcmTokenManager.refreshToken(applicationContext)
        FcmTokenManager.tryRegisterPendingToken(applicationContext)
        notificationData = NotificationData.fromIntent(intent)
        NotificationSync.refreshFromServer(applicationContext)

        setContent {
            val context = LocalContext.current
            val repository = remember(context) {
                ThemeRepository(context.applicationContext.themeDataStore)
            }
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ThemeViewModelFactory(repository)
            )
            val isDarkTheme by themeViewModel.isDark.collectAsState(
                initial = isSystemInDarkTheme()
            )

            CompositionLocalProvider(LocalThemeViewModel provides themeViewModel) {
                MyApplicationTheme(darkTheme = isDarkTheme) {
                    MyApplicationApp(notificationData = notificationData)
                }
            }
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?, paymentData: PaymentData?) {
        RazorpayPaymentBus.emit(
            RazorpayPaymentResult(
                success = true,
                paymentId = razorpayPaymentId,
                orderId = paymentData?.orderId,
                signature = paymentData?.signature
            )
        )
    }

    override fun onPaymentError(code: Int, response: String?, paymentData: PaymentData?) {
        val message = when {
            response?.contains("cancel", ignoreCase = true) == true -> "Payment cancelled"
            else -> response ?: "Payment failed"
        }
        RazorpayPaymentBus.emit(
            RazorpayPaymentResult(
                success = false,
                paymentId = paymentData?.paymentId,
                orderId = paymentData?.orderId,
                signature = paymentData?.signature,
                errorMessage = message
            )
        )
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        notificationData = NotificationData.fromIntent(intent)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }
    }
}

@Composable
fun MyApplicationApp(notificationData: NotificationData? = null) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = SessionManager(context.applicationContext)

    val startDestination = "splash"

    val landApi = remember {
        ApiClient.createService(LandApi::class.java)
    }

    val landRepository = remember { LandRepositoryImpl(landApi, context.contentResolver) }
    val wishlistRepository = remember { WishlistRepositoryImpl(landApi) }
    val connectivityObserver = remember(context) {
        NetworkConnectivityObserver(context.applicationContext)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        CompositionLocalProvider(LocalConnectivityObserver provides connectivityObserver) {
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                NavigationGraph(
                    navController = navController,
                    landRepository = landRepository,
                    wishlistRepository = wishlistRepository,
                    connectivityObserver = connectivityObserver
                )
            }
        }
    }

    LaunchedEffect(notificationData) {
        val data = notificationData ?: return@LaunchedEffect
        val route = NotificationRoutes.routeForNotification(
            type = data.type,
            referenceId = data.propertyId,
            roleTarget = data.roleTarget
        )
        if (route != null) {
            Log.d("Navigation", "Navigating to $route from notification")
            navController.navigate(route) {
                launchSingleTop = true
            }
        } else {
            Log.d("Navigation", "Notification missing data: type=${data.type}, propertyId=${data.propertyId}")
        }
    }
}

