package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.network.ApiClient
import com.example.myapplication.network.NetworkConnectivityObserver
import com.example.myapplication.network.SessionManager
import com.example.myapplication.ui.feature.NavigationGraph
import com.example.myapplication.ui.feature.buyer.data.remote.LandApi
import com.example.myapplication.ui.feature.buyer.data.repository.LandRepositoryImpl
import com.example.myapplication.ui.feature.buyer.data.repository.WishlistRepositoryImpl
import com.example.myapplication.ui.feature.buyer.payment.RazorpayPaymentBus
import com.example.myapplication.ui.feature.buyer.payment.RazorpayPaymentResult
import com.example.myapplication.ui.feature.common.LocalConnectivityObserver
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener

class HomeActivity : ComponentActivity(), PaymentResultWithDataListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApiClient.init(applicationContext)
        Checkout.preload(applicationContext)

        val sessionManager = SessionManager(applicationContext)
        if (!sessionManager.isLoggedIn()) {
            finish()
            return
        }

        setContent {
            val navController = rememberNavController()

            val landApi = remember { ApiClient.createService(LandApi::class.java) }
            val landRepository = remember { LandRepositoryImpl(landApi, applicationContext.contentResolver) }
            val wishlistRepository = remember { WishlistRepositoryImpl(landApi) }
            val connectivityObserver = remember(applicationContext) {
                NetworkConnectivityObserver(applicationContext)
            }

            CompositionLocalProvider(LocalConnectivityObserver provides connectivityObserver) {
                NavHost(
                    navController = navController,
                    startDestination = "home"
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
}
