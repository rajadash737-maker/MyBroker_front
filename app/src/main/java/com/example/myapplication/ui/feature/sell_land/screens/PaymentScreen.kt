package com.example.myapplication.ui.feature.sell_land.screens



import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.BuildConfig
import com.example.myapplication.ui.feature.sell_land.components.SellLandTopBar
import com.example.myapplication.ui.feature.buyer.payment.RazorpayPaymentBus
import com.example.myapplication.ui.feature.sell_land.viewmodel.SellLandViewModel
import com.razorpay.Checkout
import org.json.JSONObject
import java.text.DecimalFormat

@Composable
fun PaymentScreen(
    viewModel: SellLandViewModel,
    onBack: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    val listingFee by viewModel.paymentRequiredAmount.collectAsState()
    val orderId by viewModel.sellerOrderId.collectAsState()
    val orderAmount by viewModel.sellerOrderAmount.collectAsState()
    val orderCurrency by viewModel.sellerOrderCurrency.collectAsState()
    val paymentError by viewModel.paymentError.collectAsState()
    val paymentCompleted by viewModel.paymentCompleted.collectAsState()

    val submitResult by viewModel.submitResult.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        viewModel.refreshPaymentRequiredAmount()
    }

    LaunchedEffect(submitResult?.requestId) {
        if (submitResult != null) {
            onSubmitSuccess()
            viewModel.clearSubmitResult()
            viewModel.clearPaymentState()
        }
    }

    LaunchedEffect(paymentCompleted) {
        if (paymentCompleted) {
            viewModel.clearPaymentState()
        }
    }

    LaunchedEffect(Unit) {
        RazorpayPaymentBus.events.collect { result ->
            if (result.success) {
                viewModel.onSellerPaymentSuccess(result.paymentId, result.orderId, result.signature)
            } else {
                viewModel.onSellerPaymentFailure(result.errorMessage)
            }
        }
    }

    LaunchedEffect(orderId, orderAmount) {
        if (!orderId.isNullOrBlank() && orderAmount != null) {
            if (activity == null) {
                viewModel.onSellerPaymentFailure("Unable to launch payment screen. Please retry.")
                return@LaunchedEffect
            }
            try {
                val options = JSONObject()
                options.put("name", "MyBroker")
                options.put("description", "Listing fee payment")
                options.put("order_id", orderId)
                options.put("currency", orderCurrency ?: "INR")
                options.put("amount", orderAmount)
                val checkout = Checkout()
                checkout.setKeyID(BuildConfig.RAZORPAY_KEY_ID)
                checkout.open(activity, options)
            } catch (e: Exception) {
                viewModel.onSellerPaymentFailure(e.message ?: "Unable to open payment screen")
            }
        }
    }

    Scaffold(
        topBar = {
            SellLandTopBar(
                title = "Payment",
                onBack = onBack,
                onSaveDraft = { viewModel.saveDraft(step = 7) }


            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Listing Fee",
                    style = MaterialTheme.typography.titleMedium
                )

                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Sell Land Listing")
                        Text(
                            text = listingFee?.let { "₹${DecimalFormat("#,##0.##").format(it)}" } ?: "Loading...",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }

                Text(
                    text = "Pay securely with Razorpay",
                    style = MaterialTheme.typography.titleMedium
                )

            }

            Button(
                onClick = {
                    viewModel.createSellerRazorpayOrder()
                },
                enabled = !loading && listingFee != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                val feeLabel = listingFee?.let { "₹${DecimalFormat("#,##0.##").format(it)}" } ?: "..."
                Text(if (loading) "Processing..." else "Pay $feeLabel")
            }

            val errorText = paymentError
            if (!errorText.isNullOrBlank()) {
                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { viewModel.createSellerRazorpayOrder() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loading
                ) {
                    Text("Retry Payment")
                }
            }

        }
    }
}

