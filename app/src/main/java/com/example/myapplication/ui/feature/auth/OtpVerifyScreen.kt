package com.example.myapplication.ui.feature.auth

//package com.example.myapplication.ui.theme

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.network.ApiClient
import com.example.myapplication.network.VerifyOtpRequest
import kotlinx.coroutines.launch

@Composable
fun OtpVerifyScreen(navController: NavController, mobile: String) {

    val context = LocalContext.current
    val api = ApiClient.apiService
    val scope = rememberCoroutineScope()

    var otp by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    fun normalizeMobile(input: String): String {
        val v = input.trim().removePrefix("+")
        return if (v.length == 10 && v.all { it.isDigit() }) "91$v" else v
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Enter OTP")

        OutlinedTextField(
            value = otp,
            onValueChange = { otp = it },
            label = { Text("OTP") }
        )

        Button(
            onClick = {
                if (otp.isEmpty()) {
                    Toast.makeText(context, "Enter OTP", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                scope.launch {
                    loading = true
                    try {
                        val res = api.verifyOtp(
                            VerifyOtpRequest(
                                mobile = normalizeMobile(mobile),
                                otp = otp.trim(),
                                purpose = "forgot_password"
                            )
                        )

                        val status = res.body()?.status
                        if (res.isSuccessful && status == "OTP verified") {
                            Toast.makeText(context, "OTP Verified", Toast.LENGTH_SHORT).show()
                            navController.navigate("reset_password?mobile=${normalizeMobile(mobile)}")
                        } else {
                            Toast.makeText(context, status ?: "Invalid OTP", Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception) {
                        Toast.makeText(context, e.message ?: "Network error", Toast.LENGTH_SHORT).show()
                    } finally {
                        loading = false
                    }
                }
            }
        ) {
            Text(if (loading) "Please wait..." else "Verify OTP")
        }
    }
}
