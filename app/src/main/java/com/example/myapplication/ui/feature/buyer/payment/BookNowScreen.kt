package com.example.myapplication.ui.feature.buyer.payment

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.BuildConfig
import com.example.myapplication.network.SessionManager
import com.razorpay.Checkout
import org.json.JSONObject

@Composable
fun BookNowScreen(
    viewModel: BookNowViewModel,
    requestId: Int,
    onSuccess: (Int, Int?, Double?) -> Unit,
    onBack: () -> Unit
) {
    val state = viewModel.state.collectAsState().value
    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context.applicationContext) }
    val userId = sessionManager.getUserId() ?: 0
    val activity = context as? Activity

    LaunchedEffect(state.errorMessage, state.successMessage, state.paymentError) {
        val msg = state.errorMessage ?: state.paymentError ?: state.successMessage
        if (!msg.isNullOrBlank()) {
            snackbar.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(state.paymentCompleted) {
        if (state.paymentCompleted) {
            onSuccess(requestId, state.bookingId, state.bookingFeeAmount)
        }
    }

    LaunchedEffect(Unit) {
        RazorpayPaymentBus.events.collect { result ->
            if (result.success) {
                viewModel.onPaymentSuccess(result.paymentId, result.orderId, result.signature)
            } else {
                viewModel.onPaymentFailure(result.errorMessage)
            }
        }
    }

    LaunchedEffect(state.checkoutReady, state.razorpayOrderId, state.razorpayAmount) {
        if (state.checkoutReady && !state.razorpayOrderId.isNullOrBlank() && state.razorpayAmount != null) {
            if (activity == null) {
                viewModel.onPaymentFailure("Unable to launch payment screen. Please retry.")
                return@LaunchedEffect
            }
            try {
                val options = JSONObject()
                options.put("name", "MyBroker")
                options.put("description", "Property booking")
                options.put("order_id", state.razorpayOrderId)
                options.put("currency", state.razorpayCurrency ?: "INR")
                options.put("amount", state.razorpayAmount)
                val checkout = Checkout()
                checkout.setKeyID(BuildConfig.RAZORPAY_KEY_ID)
                checkout.open(activity, options)
                viewModel.onCheckoutLaunched()
            } catch (e: Exception) {
                viewModel.onPaymentFailure(e.message ?: "Unable to open payment screen")
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbar) }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (state.step) {
                BookNowStep.DETAILS -> BookNowDetailsScreen(
                    state = state,
                    formatAmount = viewModel::formatAmount,
                    onAcceptTerms = viewModel::acceptTerms,
                    onContinue = viewModel::goToKyc,
                    onBack = onBack
                )

                BookNowStep.KYC -> BookNowKycScreen(
                    state = state,
                    onBuyerNameChange = viewModel::onBuyerNameChange,
                    onBuyerMobileChange = viewModel::onBuyerMobileChange,
                    onAadhaarPicked = viewModel::onAadhaarPicked,
                    onPanPicked = viewModel::onPanPicked,
                    onBankPicked = viewModel::onBankPicked,
                    onContinue = viewModel::createBooking,
                    onBack = onBack
                )

                BookNowStep.PAYMENT -> BookNowPaymentScreen(
                    state = state,
                    formatAmount = viewModel::formatAmount,
                    onPayNow = {
                        if (userId > 0) {
                            viewModel.createRazorpayOrder(userId)
                        } else {
                            viewModel.onPaymentFailure("Session expired. Please log in again.")
                        }
                    },
                    onBack = onBack
                )
            }
        }
    }
}
