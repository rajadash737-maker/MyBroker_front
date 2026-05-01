package com.example.myapplication.ui.feature.auth

//package com.example.myapplication.ui.theme

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.network.ApiClient
import com.example.myapplication.utils.normalizeMobile

@Composable
fun OtpVerifyScreen(navController: NavController, mobile: String) {

    val context = LocalContext.current
    val activity = context as? Activity
    ApiClient.init(context.applicationContext)
    val otpViewModel: FirebaseOtpViewModel = viewModel()
    val otpState by otpViewModel.uiState.collectAsState()

    LaunchedEffect(mobile) {
        otpViewModel.updateMobile(mobile.filter { it.isDigit() })
    }
    LaunchedEffect(otpState.message) {
        val msg = otpState.message ?: return@LaunchedEffect
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        otpViewModel.clearMessage()
    }
    LaunchedEffect(otpState.otpVerified) {
        if (otpState.otpVerified) {
            navController.navigate("reset_password?mobile=${normalizeMobile(mobile)}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Enter OTP")

        OutlinedTextField(
            value = otpState.otp,
            onValueChange = { otpViewModel.updateOtp(it.filter { ch -> ch.isDigit() }) },
            label = { Text("OTP") }
        )

        Button(
            onClick = {
                if (activity == null) {
                    Toast.makeText(context, "Activity not available", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                otpViewModel.verifyOtp("forgot_password")
            }
        ) {
            Text(if (otpState.loading) "Please wait..." else "Verify OTP")
        }
    }
}
